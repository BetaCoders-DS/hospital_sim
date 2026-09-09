package com.github.betacoders.simulation;

import com.github.betacoders.entities.GridOcuppancy;
import com.github.betacoders.entities.Pacient;
import com.github.betacoders.entities.StaticEntities;
import com.github.betacoders.grid.Grid;
import com.github.betacoders.movement.MapDistanceSource;
import com.github.betacoders.movement.MoveIntention;
import com.github.betacoders.movement.Movement;
import com.github.betacoders.time.GeneratorTimer;
import com.github.betacoders.time.SimulationClock;
import com.github.betacoders.time.Statistics;
import com.github.betacoders.types.Position;
import com.github.betacoders.types.Vitals;
import com.github.betacoders.types.collections.DecisionTree;
import com.github.betacoders.types.collections.LinkedList;
import com.github.betacoders.types.collections.Vector;

import java.util.Random;

public final class Simulation {
  private static final int[] DX = { 0, 0, -1, 1 };
  private static final int[] DY = { -1, 1, 0, 0 };

  private final Grid<StaticEntities> map;
  private final SimulationConfig cfg;
  private final GridOcuppancy occupancy;
  private final MapDistanceSource distanceSource;
  private final SimulationClock clock = new SimulationClock();
  private final Statistics stats = new Statistics();
  private final Random rng = new Random();
  private final DecisionTree<Vitals, ManchesterColor> manchester;
  private boolean paused = false;

  private final LinkedList<Pacient> pacients = new LinkedList<>();
  private final Vector<SeatSpot> seats = new Vector<>();
  private final LinkedList<Pacient> prefQueue = new LinkedList<>();
  private final LinkedList<Pacient> normalQueue = new LinkedList<>();
  private final Vector<LinkedList<Pacient>> medicQueues = new Vector<>(5);

  private Position genPos;
  private Position removerPos;
  private Position totemPos;
  private Position nursePos;
  private Position medicPos;
  private StaticEntities.Generator generator;
  private StaticEntities.Remover remover;
  private StaticEntities.Totem totem;
  private StaticEntities.Nurse nurse;
  private StaticEntities.Medic medic;

  private Pacient inTriage = null;
  private Pacient inConsult = null;
  private int consecutivePref = 0;
  private int ticketP = 0;
  private int ticketN = 0;
  private double nextSpawnAt = 0;
  private double nurseBusyUntil = 0;
  private double medicBusyUntil = 0;

  public Simulation(Grid<StaticEntities> map, SimulationConfig cfg) {
    this.map = map;
    this.cfg = cfg;
    this.occupancy = new GridOcuppancy(map.sizeX(), map.sizeY());
    this.distanceSource = new MapDistanceSource(map);
    this.manchester = buildManchesterTree();
    for (int i = 0; i < ManchesterColor.values().length; ++i) {
      medicQueues.add(new LinkedList<>());
    }
    scanMap();
    reset();
  }

  private static DecisionTree<Vitals, ManchesterColor> buildManchesterTree() {
    return DecisionTree.<Vitals, ManchesterColor>builder()
        .root(v -> v.conscious() == 1)
        .leafTrue(ManchesterColor.RED)
        .up()
        .ifFalse(v -> v.oxigenSat() < 92)
        .leafTrue(ManchesterColor.ORANGE)
        .up()
        .ifFalse(v -> v.painLevel() >= 8)
        .leafTrue(ManchesterColor.YELLOW)
        .up()
        .ifFalse(v -> v.bodyTemp() >= 38)
        .leafTrue(ManchesterColor.GREEN)
        .up()
        .leafFalse(ManchesterColor.BLUE)
        .build();
  }

  private void scanMap() {
    for (int y = 0; y < map.sizeY(); ++y) {
      for (int x = 0; x < map.sizeX(); ++x) {
        StaticEntities e = map.get(x, y);
        if (e instanceof StaticEntities.Generator g) {
          generator = g;
          genPos = new Position(x, y);
        } else if (e instanceof StaticEntities.Remover r) {
          remover = r;
          removerPos = new Position(x, y);
        } else if (e instanceof StaticEntities.Totem t) {
          totem = t;
          totemPos = new Position(x, y);
        } else if (e instanceof StaticEntities.Nurse n) {
          nurse = n;
          nursePos = new Position(x, y);
        } else if (e instanceof StaticEntities.Medic m) {
          medic = m;
          medicPos = new Position(x, y);
        } else if (e instanceof StaticEntities.Seat s) {
          seats.add(new SeatSpot(s, new Position(x, y)));
        }
      }
    }
  }

  public void reset() {
    for (SeatSpot spot : seats) {
      spot.seat().liberar();
    }
    nurse.liberar();
    medic.liberar();
    inTriage = null;
    inConsult = null;
    consecutivePref = 0;
    ticketP = 0;
    ticketN = 0;
    nurseBusyUntil = 0;
    medicBusyUntil = 0;
    prefQueue.clear();
    normalQueue.clear();
    for (LinkedList<Pacient> q : medicQueues) {
      q.clear();
    }
    pacients.clear();
    occupancy.clean();
    clock.reset();
    stats.reset();
    nextSpawnAt = GeneratorTimer.nextSpawn(cfg.spawnAvg());
  }

  public void step() {
    if (paused) {
      return;
    }
    clock.update();
    double t = clock.getTimePassed();

    trySpawn(t);
    checkServiceCompletions(t);
    checkIdleStations();
    movePatients();
    updateLifecycle(t);
    purge();
  }

  public void pause() {
    paused = true;
    clock.pause();
  }

  public void resume() {
    paused = false;
    clock.resume();
  }

  public boolean isPaused() {
    return paused;
  }

  public Grid<StaticEntities> map() {
    return map;
  }

  public LinkedList<Pacient> pacients() {
    return pacients;
  }

  public int activeCount() {
    return pacients.size();
  }

  public double timePassed() {
    return clock.getTimePassed();
  }

  public double waitingTimeAvg() {
    return stats.waitingTimeAvg();
  }

  public double systemTimeAvg() {
    return stats.systemTimeAvg();
  }

  public int totalServed() {
    return stats.getTotalServed();
  }

  public boolean nurseBusy() {
    return !nurse.estaOciosa();
  }

  public boolean medicBusy() {
    return !medic.estaOcioso();
  }

  public int normalQueueSize() {
    return normalQueue.size();
  }

  public int preferentialQueueSize() {
    return prefQueue.size();
  }

  public int[] medicQueueSizes() {
    int[] out = new int[medicQueues.size()];
    for (int i = 0; i < out.length; ++i) {
      out[i] = medicQueues.get(i).size();
    }
    return out;
  }

  private void trySpawn(double t) {
    if (t < nextSpawnAt) {
      return;
    }
    nextSpawnAt = t + GeneratorTimer.nextSpawn(cfg.spawnAvg());
    if (!occupancy.isFree(genPos)) {
      return;
    }
    Pacient p = new Pacient(new Position(genPos.x, genPos.y),
        rng.nextDouble() < cfg.preferentialP());
    p.vitals(generateVitals());
    p.spawnTime(t);
    p.target(totem);
    occupancy.occupy(genPos, p);
    pacients.addLast(p);
  }

  private Vitals generateVitals() {
    int o2 = clampInt(rng.nextGaussian() * cfg.o2Dev() + cfg.o2Mean(), cfg.o2Min(), cfg.o2Max());
    int temp = clampInt(rng.nextGaussian() * cfg.tempDev() + cfg.tempMean(), cfg.tempMin(), cfg.tempMax());
    int pain = clampInt(rng.nextGaussian() * cfg.painDev() + cfg.painMean(), 0, 10);
    int conscious = rng.nextDouble() < cfg.consciousP() ? 1 : 0;
    return new Vitals(o2, temp, pain, conscious);
  }

  private static int clampInt(double v, double lo, double hi) {
    int x = (int) Math.round(v);
    return (int) Math.max(lo, Math.min(hi, x));
  }

  private void checkServiceCompletions(double t) {
    if (inTriage != null && t >= nurseBusyUntil) {
      Pacient p = inTriage;
      inTriage = null;
      nurse.liberar();
      ManchesterColor color = manchester.classify(p.vitals());
      p.color(color);
      p.changeState(Pacient.State.MEDIC_QUEUE);
      medicQueues.get(color.ordinal()).addLast(p);
      p.waitStart(t);
      goToSeat(p);
    }
    if (inConsult != null && t >= medicBusyUntil) {
      Pacient p = inConsult;
      inConsult = null;
      medic.liberar();
      p.changeState(Pacient.State.GOING_REMOVER);
      p.target(remover);
    }
  }

  private void checkIdleStations() {
    if (nurse.estaOciosa()) {
      callNextTriage();
    }
    if (medic.estaOcioso()) {
      callNextMedic();
    }
  }

  private void callNextTriage() {
    boolean prefPending = !prefQueue.isEmpty();
    boolean normalPending = !normalQueue.isEmpty();
    if (!prefPending && !normalPending) {
      return;
    }
    Pacient next;
    if (prefPending && (!normalPending || consecutivePref < 2)) {
      ++consecutivePref;
      next = prefQueue.removeFirst();
    } else {
      consecutivePref = 0;
      next = normalQueue.removeFirst();
    }
    nurse.ocupar();
    inTriage = next;
    nurseBusyUntil = Double.POSITIVE_INFINITY;
    next.changeState(Pacient.State.GOING_TRIAGE);
    releaseSeat(next);
    next.target(nurse);
  }

  private void callNextMedic() {
    Pacient next = null;
    for (LinkedList<Pacient> q : medicQueues) {
      if (!q.isEmpty()) {
        next = q.removeFirst();
        break;
      }
    }
    if (next == null) {
      return;
    }
    medic.ocupar();
    inConsult = next;
    medicBusyUntil = Double.POSITIVE_INFINITY;
    next.changeState(Pacient.State.GOING_MEDIC);
    releaseSeat(next);
    next.target(medic);
  }

  private void releaseSeat(Pacient p) {
    if (p.target() instanceof StaticEntities.Seat s) {
      s.liberar();
    }
  }

  private void movePatients() {
    LinkedList<MoveIntention> intentions = Movement.computeIntentions(pacients, distanceSource, occupancy, map);
    Movement.resolve(intentions, occupancy);
  }

  private void updateLifecycle(double t) {
    for (Pacient p : pacients) {
      switch (p.state()) {
        case GOING_TOTEM:
          if (samePos(p.pos(), totemPos)) {
            int num = p.preferential() ? ++ticketP : ++ticketN;
            p.giveTicketNum(num);
            p.changeState(Pacient.State.TRIAGE_QUEUE);
            if (p.preferential()) {
              prefQueue.addLast(p);
            } else {
              normalQueue.addLast(p);
            }
            p.waitStart(t);
            goToSeat(p);
          }
          break;
        case TRIAGE_QUEUE:
        case MEDIC_QUEUE:
          if (p.targetPosition() != null && samePos(p.pos(), p.targetPosition())) {
            if (p.target() instanceof StaticEntities.Seat s) {
              s.ocupar();
            }
          }
          break;
        case GOING_TRIAGE:
          if (adjacent(p.pos(), nursePos)) {
            p.changeState(Pacient.State.IN_TRIAGE);
            nurseBusyUntil = t + GeneratorTimer.serviceTime(cfg.triageAvg(), cfg.triageDev(), cfg.triageMin());
            p.waitAccum(p.waitAccum() + (t - p.waitStart()));
            p.target(null);
          }
          break;
        case GOING_MEDIC:
          if (adjacent(p.pos(), medicPos)) {
            p.changeState(Pacient.State.IN_MEDIC);
            medicBusyUntil = t + GeneratorTimer.serviceTime(cfg.consultAvg(), cfg.consultDev(), cfg.consultMin());
            p.waitAccum(p.waitAccum() + (t - p.waitStart()));
            p.target(null);
          }
          break;
        case GOING_REMOVER:
          if (samePos(p.pos(), removerPos)) {
            p.changeState(Pacient.State.REMOVED);
            occupancy.free(removerPos);
            stats.logPacientExit(p.waitAccum(), t - p.spawnTime());
          }
          break;
        case IN_TRIAGE:
        case IN_MEDIC:
        case REMOVED:
          break;
      }
    }
  }

  private void goToSeat(Pacient p) {
    SeatSpot spot = nearestFreeSeat(p.pos());
    if (spot == null) {
      p.target(null);
    } else {
      spot.seat().reservar();
      p.target(spot.seat());
    }
  }

  private SeatSpot nearestFreeSeat(Position from) {
    boolean[][] visited = new boolean[map.sizeY()][map.sizeX()];
    LinkedList<Position> queue = new LinkedList<>();
    visited[from.y][from.x] = true;
    queue.addLast(from);

    while (!queue.isEmpty()) {
      Position cur = queue.removeFirst();
      StaticEntities e = map.get(cur.x, cur.y);
      if (e instanceof StaticEntities.Seat s && s.estaLivre()) {
        return new SeatSpot(s, cur);
      }
      for (int i = 0; i < DX.length; ++i) {
        int nx = cur.x + DX[i];
        int ny = cur.y + DY[i];
        if (nx < 0 || nx >= map.sizeX() || ny < 0 || ny >= map.sizeY()) {
          continue;
        }
        if (visited[ny][nx]) {
          continue;
        }
        StaticEntities ne = map.get(nx, ny);
        if (ne instanceof StaticEntities.Wall || ne instanceof StaticEntities.Nurse
            || ne instanceof StaticEntities.Medic) {
          continue;
        }
        visited[ny][nx] = true;
        queue.addLast(new Position(nx, ny));
      }
    }
    return null;
  }

  private void purge() {
    Vector<Pacient> removed = new Vector<>();
    for (Pacient p : pacients) {
      if (p.state() == Pacient.State.REMOVED) {
        removed.add(p);
      }
    }
    for (Pacient p : removed) {
      pacients.remove(p);
    }
  }

  private static boolean samePos(Position a, Position b) {
    return a.x == b.x && a.y == b.y;
  }

  private static boolean adjacent(Position a, Position b) {
    return Math.abs(a.x - b.x) + Math.abs(a.y - b.y) == 1;
  }
}