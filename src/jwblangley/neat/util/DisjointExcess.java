package jwblangley.neat.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DisjointExcess {

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
