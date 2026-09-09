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
   * Finds a complete path from the patient to its target,
   * considering other patients as temporary obstacles.
   *
   * This prevents patients from oscillating between two cells
   * when their normal Wavefront path is blocked.
   */
  private static Position findPathStep(
      Patient patient,
      Grid<Integer> staticDistances,
      GridOcuppancy occupancy) {

    Position start = patient.pos();
    Position target = patient.targetPosition();

    if (target == null) {
      return null;
    }

    if (samePos(start, target)) {
      return null;
    }

    int width = staticDistances.sizeX();
    int height = staticDistances.sizeY();

    boolean[][] visited =
        new boolean[height][width];

    Position[][] previous =
        new Position[height][width];

    Position[] queue =
        new Position[width * height];

    int front = 0;
    int back = 0;

    queue[back++] =
        new Position(
            start.x,
            start.y);

    visited[start.y][start.x] = true;

    /*
     * BFS from the patient's current position
     * to the target.
     *
     * Occupied cells are treated as obstacles.
     * The target itself is always allowed.
     */
    while (front < back) {

      Position current =
          queue[front++];

      if (samePos(current, target)) {
        break;
      }

      for (int i = 0;
           i < DX.length;
           ++i) {

        int nx =
            current.x + DX[i];

        int ny =
            current.y + DY[i];

        if (nx < 0
            || nx >= width
            || ny < 0
            || ny >= height) {

          continue;
        }

        if (visited[ny][nx]) {
          continue;
        }

        /*
         * Ignore cells that are unreachable in the
         * static map. This preserves walls and other
         * map obstacles.
         */
        if (staticDistances.get(nx, ny) == -1) {
          continue;
        }

        Position next =
            new Position(nx, ny);

        /*
         * Other patients are temporary obstacles.
         *
         * The target is allowed even if the occupancy
         * system reports it as occupied.
         */
        if (!samePos(next, target)
            && !samePos(next, start)
            && !occupancy.isFree(next)) {

          continue;
        }

        visited[ny][nx] = true;

        previous[ny][nx] =
            current;

        queue[back++] = next;
      }
    }

    /*
     * No complete route exists right now.
     *
     * The patient remains still and the route will
     * be calculated again on the next simulation step.
     */
    if (!visited[target.y][target.x]) {
      return null;
    }

    /*
     * Reconstruct the path backwards.
     *
     * We start at the target and walk backwards until
     * reaching the patient's current position.
     */
    Position current =
        new Position(
            target.x,
            target.y);

    while (previous[current.y][current.x] != null) {

      Position previousPosition =
          previous[current.y][current.x];

      /*
       * This is the first cell the patient must enter.
       */
      if (samePos(previousPosition, start)) {
        return current;
      }

      current = previousPosition;
    }

    return null;
  }

  public static LinkedList<MoveIntention> computeIntentions(
      LinkedList<Patient> patients,
      DistanceSource source,
      GridOcuppancy occupancy) {

    LinkedList<MoveIntention> intentions =
        new LinkedList<>();

    for (Patient patient : patients) {

      /*
       * The static Wavefront is still calculated first.
       * It tells us which cells are valid according
       * to the map.
       */
      Grid<Integer> distances =
          source.distancesFor(patient);

      /*
       * Now calculate the actual route while treating
       * other patients as obstacles.
       */
      Position next =
          findPathStep(
              patient,
              distances,
              occupancy);

      intentions.addLast(
          new MoveIntention(
              patient,
              next));
    }

    return intentions;
  }

  private static boolean samePos(
      Position a,
      Position b) {

    return a.x == b.x
        && a.y == b.y;
  }

  /**
   * Checks whether another patient has already claimed
   * the same destination.
   */
  private static boolean isFirstClaim(
      MoveIntention intention,
      LinkedList<MoveIntention> all) {

    for (MoveIntention other : all) {

      if (other == intention) {
        return true;
      }

      if (other.next() != null
          && samePos(
              other.next(),
              intention.next())) {

        return false;
      }
    }

    return true;
  }

  public static void resolve(
      LinkedList<MoveIntention> intentions,
      GridOcuppancy occupancy) {

    for (MoveIntention intention : intentions) {

      if (intention.next() == null) {
        continue;
      }

      /*
       * Only one patient can claim a cell.
       */
      if (!isFirstClaim(
          intention,
          intentions)) {

        continue;
      }

      Position origin =
          intention.patient().pos();

      Position destination =
          intention.next();

      /*
       * The destination may have become occupied
       * since the intention was calculated.
       *
       * If that happened, the patient waits and
       * recalculates the route on the next step.
       */
      if (!occupancy.isFree(destination)) {
        continue;
      }

      occupancy.move(
          origin,
          destination,
          intention.patient());

      intention.patient().pos(
          destination);
    }
  }
}