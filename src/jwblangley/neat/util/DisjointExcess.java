package jwblangley.neat.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Utility class for calculating the number of excess and disjoint integers two lists of integers
 */
public class DisjointExcess {

  /**
   * Calculate the number of excess and disjoint integers between two lists of integers
   *
   * @param xs the first list of integers
   * @param ys the second list of integers
   * @return <disjoints, excesses> pair corresponding to the number of disjoint and excess integers
   * between the two lists
   */
  public static ImmutableHomogeneousPair<Integer> calculate(List<Integer> xs, List<Integer> ys) {
    // Don't modify original lists
    xs = new ArrayList<>(xs);
    ys = new ArrayList<>(ys);

    // Ascending sort
    Collections.sort(xs);
    Collections.sort(ys);

    final int maxInXs = xs.get(xs.size() - 1);
    final int maxInYs = ys.get(ys.size() - 1);

    final int max = Math.max(maxInXs, maxInYs);
    final int minMax = Math.min(maxInXs, maxInYs);

    int disjoints = 0;
    int excesses = 0;
    for (int i = 0; i <= max; i++) {
      if (i <= minMax) {
        // Not excess
        if (xs.contains(i) ^ ys.contains(i)) {
          disjoints++;
        }
      } else {
        // Excess
        if (xs.contains(i) || ys.contains(i)) {
          excesses++;
        }
      }
    }

    return new ImmutableHomogeneousPair<>(disjoints, excesses);
  }

}
