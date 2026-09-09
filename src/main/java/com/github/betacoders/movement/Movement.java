package com.github.betacoders.movement;

import com.github.betacoders.entities.GridOcuppancy;
import com.github.betacoders.entities.Patient;
import com.github.betacoders.grid.Grid;
import com.github.betacoders.types.Position;
import com.github.betacoders.types.collections.LinkedList;

public class Movement {
  private static final int[] DX = { 0, 0, 1, -1 };
  private static final int[] DY = { -1, 1, 0, 0 };

  private static Position bestNeighbor(Position cur, Grid<Integer> dist, GridOcuppancy occupancy) {
    int curDist = dist.get(cur.x, cur.y);

    // Ja chegou no alvo (distancia 0): nao ha motivo pra sair da celula.
    // Sem essa checagem, o paciente saia pro vizinho mais proximo (distancia 1)
    // e no quadro seguinte via que a propria celula de origem (distancia 0)
    // era a "melhor vizinha" e voltava - ficando preso indo e voltando entre
    // as duas celulas para sempre.
    if (curDist == 0)
      return null;

    Position[] candidates = new Position[DX.length];
    int[] dists = new int[DX.length];
    int count = 0;

    for (int i = 0; i < DX.length; ++i) {
      int nx = cur.x + DX[i];
      int ny = cur.y + DY[i];

      if (nx < 0 || nx >= dist.sizeX() || ny < 0 || ny >= dist.sizeY())
        continue;

      int d = dist.get(nx, ny);
      if (d == -1)
        continue;

      // So aceita vizinhos que realmente aproximam do alvo. Isso evita que o
      // paciente ande para um lado so pra, no quadro seguinte, perceber que a
      // celula de onde veio tem distancia menor e volte - o mesmo efeito de
      // "vai e volta" citado acima, só que a meio caminho em vez de no alvo.
      if (curDist != -1 && d >= curDist)
        continue;

      candidates[count] = new Position(nx, ny);
      dists[count] = d;
      ++count;
    }

    for (int i = 1; i < count; ++i) {
      int dKey = dists[i];
      Position pKey = candidates[i];
      int j = i - 1;

      while (j >= 0 && dists[j] > dKey) {
        dists[j + 1] = dists[j];
        candidates[j + 1] = candidates[j];
        --j;
      }
      dists[j + 1] = dKey;
      candidates[j + 1] = pKey;
    }

    for (int i = 0; i < count; ++i) {
      if (occupancy.isFree(candidates[i]))
        return candidates[i];
    }
    return null;
  }

  public static LinkedList<MoveIntention> computeIntentions(LinkedList<Patient> pa, DistanceSource sour,
      GridOcuppancy occ) {
    LinkedList<MoveIntention> intentions = new LinkedList<>();

    for (Patient p : pa) {
      Grid<Integer> dist = sour.distancesFor(p);
      Position best = bestNeighbor(p.pos(), dist, occ);
      intentions.addLast(new MoveIntention(p, best));
    }

    return intentions;
  }

  private static boolean samePos(Position a, Position b) {
    return a.x == b.x && a.y == b.y;
  }

  private static boolean isFirstClaim(MoveIntention mi, LinkedList<MoveIntention> all) {
    for (MoveIntention other : all) {
      if (other == mi)
        return true;
      if (other.next() != null && samePos(other.next(), mi.next()))
        return false;
    }
    return true;
  }

  public static void resolve(LinkedList<MoveIntention> intentions, GridOcuppancy occupancy) {
    for (MoveIntention mi : intentions) {
      if (mi.next() == null)
        continue;
      if (!isFirstClaim(mi, intentions))
        continue;

      Position origem = mi.patient().pos();
      occupancy.move(origem, mi.next(), mi.patient());
      mi.patient().pos(mi.next());
    }
  }
}