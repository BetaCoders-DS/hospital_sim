package com.github.betacoders.movement;

import com.github.betacoders.entities.GridOcuppancy;
import com.github.betacoders.entities.Patient;
import com.github.betacoders.grid.Grid;
import com.github.betacoders.types.Position;
import com.github.betacoders.types.collections.LinkedList;

public class Movement {

  private static final int[] DX = { 0, 0, 1, -1 };
  private static final int[] DY = { -1, 1, 0, 0 };

  /**
   * Finds the best free neighbor.
   *
   * Normally the patient follows the Wavefront path.
   * If another patient blocks that path, the method searches
   * for another free neighbor instead of leaving the patient stuck.
   */
  private static Position bestNeighbor(
      Position cur,
      Grid<Integer> dist,
      GridOcuppancy occupancy) {

    int curDist = dist.get(cur.x, cur.y);

    /*
     * The patient has already reached its target.
     */
    if (curDist == 0) {
      return null;
    }

    Position best = null;
    int bestDistance = Integer.MAX_VALUE;

    /*
     * First try the normal Wavefront route.
     * Only neighbors that get closer to the target are considered.
     */
    for (int i = 0; i < DX.length; ++i) {

      int nx = cur.x + DX[i];
      int ny = cur.y + DY[i];

      if (nx < 0
          || nx >= dist.sizeX()
          || ny < 0
          || ny >= dist.sizeY()) {
        continue;
      }

      int d = dist.get(nx, ny);

      if (d == -1) {
        continue;
      }

      if (d >= curDist) {
        continue;
      }

      Position candidate =
          new Position(nx, ny);

      if (!occupancy.isFree(candidate)) {
        continue;
      }

      if (d < bestDistance) {
        bestDistance = d;
        best = candidate;
      }
    }

    /*
     * If the direct route is blocked, choose another free
     * walkable neighbor.
     *
     * This allows the patient to temporarily move away from
     * the chair in order to go around another patient.
     */
    if (best == null) {

      for (int i = 0; i < DX.length; ++i) {

        int nx = cur.x + DX[i];
        int ny = cur.y + DY[i];

        if (nx < 0
            || nx >= dist.sizeX()
            || ny < 0
            || ny >= dist.sizeY()) {
          continue;
        }

        int d = dist.get(nx, ny);

        if (d == -1) {
          continue;
        }

        Position candidate =
            new Position(nx, ny);

        if (!occupancy.isFree(candidate)) {
          continue;
        }

        /*
         * Choose the free neighbor that is closest
         * to the target among the available alternatives.
         */
        if (d < bestDistance) {
          bestDistance = d;
          best = candidate;
        }
      }
    }

    return best;
  }

  public static LinkedList<MoveIntention> computeIntentions(
      LinkedList<Patient> pa,
      DistanceSource sour,
      GridOcuppancy occ) {

    LinkedList<MoveIntention> intentions =
        new LinkedList<>();

    for (Patient p : pa) {

      Grid<Integer> dist =
          sour.distancesFor(p);

      Position best =
          bestNeighbor(
              p.pos(),
              dist,
              occ);

      intentions.addLast(
          new MoveIntention(
              p,
              best));
    }

    return intentions;
  }

  private static boolean samePos(
      Position a,
      Position b) {

    return a.x == b.x
        && a.y == b.y;
  }

  private static boolean isFirstClaim(
      MoveIntention mi,
      LinkedList<MoveIntention> all) {

    for (MoveIntention other : all) {

      if (other == mi) {
        return true;
      }

      if (other.next() != null
          && samePos(
              other.next(),
              mi.next())) {

        return false;
      }
    }

    return true;
  }

  public static void resolve(
      LinkedList<MoveIntention> intentions,
      GridOcuppancy occupancy) {

    for (MoveIntention mi : intentions) {

      if (mi.next() == null) {
        continue;
      }

      if (!isFirstClaim(
          mi,
          intentions)) {

        continue;
      }

      Position origem =
          mi.patient().pos();

      /*
       * The target cell must still be free when
       * the movement is actually resolved.
       */
      if (!occupancy.isFree(mi.next())) {
        continue;
      }

      occupancy.move(
          origem,
          mi.next(),
          mi.patient());

      mi.patient().pos(
          mi.next());
    }
  }
}