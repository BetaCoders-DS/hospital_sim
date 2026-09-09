package com.github.betacoders.simulation;

import com.github.betacoders.entities.GridOcuppancy;
import com.github.betacoders.entities.Patient;
import com.github.betacoders.entities.StaticEntities;
import com.github.betacoders.grid.Grid;
import com.github.betacoders.movement.MapDistanceSource;
import com.github.betacoders.movement.MoveIntention;
import com.github.betacoders.movement.Movement;
import com.github.betacoders.path.Wavefront;
import com.github.betacoders.time.GeneratorTimer;
import com.github.betacoders.time.SimulationClock;
import com.github.betacoders.time.Statistics;
import com.github.betacoders.types.Position;
import com.github.betacoders.types.Vitals;
import com.github.betacoders.types.collections.LinkedList;
import com.github.betacoders.types.collections.ManchesterTree;
import com.github.betacoders.types.collections.Vector;

import java.util.Random;

public final class Simulation {

  private static final int[] DX = { 0, 0, -1, 1 };
  private static final int[] DY = { -1, 1, 0, 0 };

  private static final int TICKET_QUEUE_CAPACITY = 5;

  private final Grid<StaticEntities> map;
  private final SimulationConfig cfg;
  private final GridOcuppancy occupancy;
  private final MapDistanceSource distanceSource;
  private final SimulationClock clock;
  private final Statistics stats = new Statistics();
  private final Random rng = new Random();
  private final ManchesterTree manchester = new ManchesterTree();

  private boolean paused = false;

  private final LinkedList<Patient> patients = new LinkedList<>();
  private final Vector<SeatSpot> seats = new Vector<>();
  private final TriageQueue triageQueue = new TriageQueue();
  private final MedicQueue medicQueue = new MedicQueue();

  private final LinkedList<Patient> ticketQueue = new LinkedList<>();
  private final LinkedList<Position> ticketQueuePositions = new LinkedList<>();

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

  private Patient inTriage = null;
  private Patient inConsult = null;

  private double nextSpawnAt = 0;
  private double nurseBusyUntil = 0;
  private double medicBusyUntil = 0;

  public Simulation(Grid<StaticEntities> map, SimulationConfig cfg) {
    this(map, cfg, new SimulationClock());
  }

  Simulation(
      Grid<StaticEntities> map,
      SimulationConfig cfg,
      SimulationClock clock) {

    this.map = map;
    this.cfg = cfg;
    this.clock = clock;

    this.occupancy = new GridOcuppancy(map.sizeX(), map.sizeY());

    this.distanceSource = new MapDistanceSource(map);

    scanMap();
    buildTicketQueuePositions();
    reset();
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
          seats.add(
              new SeatSpot(
                  s,
                  new Position(x, y)));
        }
      }
    }

    requireEntity(
        "generator",
        generator,
        genPos);
    requireEntity(
        "remover",
        remover,
        removerPos);
    requireEntity(
        "totem",
        totem,
        totemPos);
    requireEntity(
        "nurse",
        nurse,
        nursePos);
    requireEntity(
        "medic",
        medic,
        medicPos);
  }

  private static void requireEntity(
      String name,
      StaticEntities entity,
      Position pos) {

    if (entity == null) {

      throw new IllegalArgumentException(
          "Map is missing the required "
              + name
              + " entity.");
    }
  }

  private void buildTicketQueuePositions() {
    Grid<Integer> distances = Wavefront.calculate(
        map,
        totemPos);
    LinkedList<Position> path = new LinkedList<>();
    Position current = new Position(
        genPos.x,
        genPos.y);

    path.addLast(current);
    while (!samePos(current, totemPos)) {
      int currentDistance = distances.get(
          current.x,
          current.y);
      if (currentDistance <= 0) {
        throw new IllegalArgumentException(
            "No path from generator to totem.");
      }

      Position next = null;

      for (int i = 0; i < DX.length; ++i) {
        int nx = current.x + DX[i];
        int ny = current.y + DY[i];

        if (nx < 0
            || nx >= map.sizeX()
            || ny < 0
            || ny >= map.sizeY()) {
          continue;
        }

        if (distances.get(nx, ny) == currentDistance - 1) {
          next = new Position(nx, ny);
          break;
        }
      }

      if (next == null) {
        throw new IllegalArgumentException(
            "Could not build generator-to-totem queue path.");
      }

      current = next;
      path.addLast(current);
    }

    int firstIndex = Math.max(
        1,
        path.size()
            - TICKET_QUEUE_CAPACITY
            - 1);

    for (int i = path.size() - 2; i >= firstIndex; --i) {
      ticketQueuePositions.addLast(
          path.get(i));
    }

    if (ticketQueuePositions.size() < TICKET_QUEUE_CAPACITY) {
      throw new IllegalArgumentException(
          "The map does not have enough space "
              + "for the ticket queue.");
    }
  }

  public void reset() {
    for (SeatSpot spot : seats) {
      spot.seat().release();
    }

    nurse.release();
    medic.release();
    totem.reset();

    inTriage = null;
    inConsult = null;

    nurseBusyUntil = 0;
    medicBusyUntil = 0;

    triageQueue.clear();
    medicQueue.clear();
    ticketQueue.clear();

    patients.clear();

    occupancy.clean();

    clock.reset();
    stats.reset();

    nextSpawnAt = GeneratorTimer.nextSpawn(
        cfg.spawnAvg());
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

    updateTicketQueueTargets();

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

  public LinkedList<Patient> patients() {
    return patients;
  }

  public int activeCount() {
    return patients.size();
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
    return !nurse.isIdle();
  }

  public boolean medicBusy() {
    return !medic.isIdle();
  }

  public int normalQueueSize() {
    return triageQueue.normalSize();
  }

  public int preferentialQueueSize() {
    return triageQueue.preferentialSize();
  }

  public int[] medicQueueSizes() {
    return medicQueue.sizes();
  }

  private void trySpawn(double t) {
    if (t < nextSpawnAt) {
      return;
    }

    nextSpawnAt = t + GeneratorTimer.nextSpawn(
        cfg.spawnAvg());

    if (ticketQueue.size() >= TICKET_QUEUE_CAPACITY) {
      return;
    }

    if (!occupancy.isFree(genPos)) {
      return;
    }

    Patient p = new Patient(
        new Position(
            genPos.x,
            genPos.y),
        rng.nextDouble() < cfg.preferentialP(),
        generateVitals());

    p.spawnTime(t);

    ticketQueue.addLast(p);

    occupancy.occupy(
        genPos,
        p);

    patients.addLast(p);
  }

  private void updateTicketQueueTargets() {
    for (int i = 0; i < ticketQueue.size(); ++i) {
      Patient p = ticketQueue.get(i);
      if (i == 0
          && samePos(
              p.pos(),
              ticketQueuePositions.get(0))) {

        if (p.target() != totem) {
          p.target(totem);
        }

        continue;
      }
      Position slot = ticketQueuePositions.get(i);

      Position currentTarget = p.targetPosition();
      if (currentTarget != null
          && samePos(
              currentTarget,
              slot)) {
        continue;
      }

      p.target(null);
      p.targetPosition(slot);
    }
  }

  private Vitals generateVitals() {
    int o2 = clampInt(
        rng.nextGaussian()
            * cfg.o2Dev()
            + cfg.o2Mean(),
        cfg.o2Min(),
        cfg.o2Max());

    int temp = clampInt(
        rng.nextGaussian()
            * cfg.tempDev()
            + cfg.tempMean(),
        cfg.tempMin(),
        cfg.tempMax());

    int pain = clampInt(
        rng.nextGaussian()
            * cfg.painDev()
            + cfg.painMean(),
        0,
        10);

    int conscious = rng.nextDouble() < cfg.consciousP()
        ? 1
        : 0;

    return new Vitals(
        o2,
        temp,
        pain,
        conscious);
  }

  private static int clampInt(
      double v,
      double lo,
      double hi) {

    int x = (int) Math.round(v);
    return (int) Math.max(
        lo,
        Math.min(hi, x));
  }

  private void checkServiceCompletions(
      double t) {
    if (inTriage != null
        && t >= nurseBusyUntil) {
      Patient p = inTriage;

      inTriage = null;
      p.completeTriage(manchester);
      nurse.release();
      medicQueue.enqueue(p);

      p.waitStart(t);

      goToSeat(p);
    }

    if (inConsult != null
        && t >= medicBusyUntil) {

      Patient p = inConsult;
      inConsult = null;

      p.completeConsultation();
      medic.release();

      p.target(remover);
    }
  }

  private void checkIdleStations() {
    if (nurse.isIdle()) {
      Patient next = nurse.callNext(
          triageQueue);
      if (next != null) {
        inTriage = next;
        nurseBusyUntil = Double.POSITIVE_INFINITY;
      }
    }

    if (medic.isIdle()) {
      Patient next = medic.callNext(
          medicQueue);
      if (next != null) {
        inConsult = next;
        medicBusyUntil = Double.POSITIVE_INFINITY;
      }
    }
  }

  private void movePatients() {
    LinkedList<Patient> moving = new LinkedList<>();

    for (Patient p : patients) {
      switch (p.getState()) {
        case GOING_TO_TOTEM,
            GOING_TO_TRIAGE,
            GOING_TO_MEDIC,
            GOING_TO_REMOVER,
            WAITING_FOR_TRIAGE,
            WAITING_FOR_MEDIC ->

          moving.addLast(p);

        case AT_TOTEM,
            IN_TRIAGE,
            IN_CONSULTATION,
            REMOVED ->
          {
          }
      }
    }

    LinkedList<MoveIntention> intentions = Movement.computeIntentions(
        moving,
        distanceSource,
        occupancy);

    Movement.resolve(
        intentions,
        occupancy);
  }

  private void updateLifecycle(double t) {
    for (Patient p : patients) {
      switch (p.getState()) {
        case GOING_TO_TOTEM:
          if (samePos(
              p.pos(),
              totemPos)) {
            p.changeState(
                Patient.State.AT_TOTEM);
            totem.issueTicket(p);
            ticketQueue.remove(p);
            triageQueue.enqueue(p);
            p.waitStart(t);

            goToSeat(p);
          }
          break;

        case WAITING_FOR_TRIAGE:
        case WAITING_FOR_MEDIC:
          if (p.target() == null) {
            goToSeat(p);
          }
          if (p.targetPosition() != null
              && samePos(
                  p.pos(),
                  p.targetPosition())) {

            if (p.target() instanceof StaticEntities.Seat s) {
              s.occupy();
            }
          }

          break;

        case GOING_TO_TRIAGE:
          if (adjacent(
              p.pos(),
              nursePos)) {
            p.startTriage();
            nurseBusyUntil = t + GeneratorTimer.serviceTime(
                cfg.triageAvg(),
                cfg.triageDev(),
                cfg.triageMin());
            p.waitAccum(
                p.waitAccum()
                    + (t - p.waitStart()));
          }
          break;

        case GOING_TO_MEDIC:
          if (adjacent(
              p.pos(),
              medicPos)) {
            p.startConsultation();
            medicBusyUntil = t + GeneratorTimer.serviceTime(
                cfg.consultAvg(),
                cfg.consultDev(),
                cfg.consultMin());
            p.waitAccum(
                p.waitAccum()
                    + (t - p.waitStart()));
          }
          break;

        case GOING_TO_REMOVER:
          if (samePos(
              p.pos(),
              removerPos)) {
            p.arriveAtRemover();
            occupancy.free(
                removerPos);
            stats.logPacientExit(
                p.waitAccum(),
                t - p.spawnTime());
          }
          break;

        case AT_TOTEM:
        case IN_TRIAGE:
        case IN_CONSULTATION:
        case REMOVED:
          break;
      }
    }
  }

  private void goToSeat(Patient p) {
    SeatSpot spot = nearestFreeSeat(
        p.pos());

    if (spot == null) {
      p.target(null);
    } else {
      spot.seat().reserve();
      p.target(
          spot.seat());
    }
  }

  private SeatSpot nearestFreeSeat(
      Position from) {
    boolean[][] visited = new boolean[map.sizeY()][map.sizeX()];
    LinkedList<Position> queue = new LinkedList<>();

    visited[from.y][from.x] = true;
    queue.addLast(from);

    while (!queue.isEmpty()) {
      Position cur = queue.removeFirst();

      StaticEntities e = map.get(
          cur.x,
          cur.y);

      if (e instanceof StaticEntities.Seat s
          && s.isFree()) {
        return new SeatSpot(
            s,
            cur);
      }

      for (int i = 0; i < DX.length; ++i) {
        int nx = cur.x + DX[i];
        int ny = cur.y + DY[i];

        if (nx < 0
            || nx >= map.sizeX()
            || ny < 0
            || ny >= map.sizeY()) {
          continue;
        }

        if (visited[ny][nx]) {
          continue;
        }

        StaticEntities ne = map.get(nx, ny);
        if (ne instanceof StaticEntities.Wall
            || ne instanceof StaticEntities.Nurse
            || ne instanceof StaticEntities.Medic) {

          continue;
        }

        visited[ny][nx] = true;
        queue.addLast(
            new Position(
                nx,
                ny));
      }
    }

    return null;
  }

  private void purge() {
    Vector<Patient> removed = new Vector<>();

    for (Patient p : patients) {
      if (p.getState() == Patient.State.REMOVED) {
        removed.add(p);
      }
    }

    for (Patient p : removed) {
      patients.remove(p);
    }
  }

  private static boolean samePos(
      Position a,
      Position b) {
    return a.x == b.x
        && a.y == b.y;
  }

  private static boolean adjacent(
      Position a,
      Position b) {

    return Math.abs(a.x - b.x)
        + Math.abs(a.y - b.y) == 1;
  }
}
