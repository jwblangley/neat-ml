package jwblangley.neat.util;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Test;

public class DisjointExcessTest {

  @Test
  public void equalSingletonListsHaveNoDisjoint() {
    List<Integer> a = Collections.singletonList(0);
    List<Integer> b = Collections.singletonList(0);
    ImmutableHomogeneousPair<Integer> result = DisjointExcess.calculate(a, b);

    assertEquals((Integer) 0, result.getFirst());
  }

  @Test
  public void equalSingletonListsHaveNoExcess() {
    List<Integer> a = Collections.singletonList(0);
    List<Integer> b = Collections.singletonList(0);
    ImmutableHomogeneousPair<Integer> result = DisjointExcess.calculate(a, b);

    assertEquals((Integer) 0, result.getSecond());
  }

  @Test
  public void oneDifferenceListsHaveOneDisjoint() {
    List<Integer> a = Collections.singletonList(0);
    List<Integer> b = Collections.singletonList(1);
    ImmutableHomogeneousPair<Integer> result = DisjointExcess.calculate(a, b);

    assertEquals((Integer) 1, result.getFirst());
  }

  @Test
  public void oneDifferenceListsHaveOneExcess() {
    List<Integer> a = Collections.singletonList(0);
    List<Integer> b = Collections.singletonList(1);
    ImmutableHomogeneousPair<Integer> result = DisjointExcess.calculate(a, b);

    assertEquals((Integer) 1, result.getSecond());
  }

  @Test
  public void oneDifferenceListsWithBigValueDifferenceHaveOneExcess() {
    List<Integer> a = Collections.singletonList(0);
    List<Integer> b = Collections.singletonList(100);
    ImmutableHomogeneousPair<Integer> result = DisjointExcess.calculate(a, b);

    assertEquals((Integer) 1, result.getSecond());
  }

  @Test
  public void missingMiddleElementHasNoExcess() {
    List<Integer> a = Arrays.asList(0, 1, 2);
    List<Integer> b = Arrays.asList(0, 2);
    ImmutableHomogeneousPair<Integer> result = DisjointExcess.calculate(a, b);

    assertEquals((Integer) 0, result.getSecond());
  }

  @Test
  public void missingMiddleElementHasOneDisjoint() {
    List<Integer> a = Arrays.asList(0, 1, 2);
    List<Integer> b = Arrays.asList(0, 2);
    ImmutableHomogeneousPair<Integer> result = DisjointExcess.calculate(a, b);

    assertEquals((Integer) 1, result.getFirst());
  }

  @Test
  public void noDisjointWhenMissingFromBoth() {
    List<Integer> a = Arrays.asList(0, 1, 2, 4);
    List<Integer> b = Arrays.asList(0, 1, 2, 4);
    ImmutableHomogeneousPair<Integer> result = DisjointExcess.calculate(a, b);

    assertEquals((Integer) 0, result.getFirst());
  }

  @Test
  public void twoDisjointWhenBothOnlyInOneList() {
    List<Integer> a = Arrays.asList(0, 1, 2, 4);
    List<Integer> b = Arrays.asList(0, 1, 3, 4);
    ImmutableHomogeneousPair<Integer> result = DisjointExcess.calculate(a, b);

    assertEquals((Integer) 2, result.getFirst());
  }

  @Test
  public void oneDisjointsWhenDisjointAtEndOfSmallerList() {
    List<Integer> a = Arrays.asList(0, 1, 2, 4);
    List<Integer> b = Arrays.asList(0, 1, 2, 5);
    ImmutableHomogeneousPair<Integer> result = DisjointExcess.calculate(a, b);

    assertEquals((Integer) 1, result.getFirst());
  }

  @Test
  public void disjointWorksForComplicatedLists() {
    List<Integer> a = Arrays.asList(0, 1, 2, 4, 8, 11);
    List<Integer> b = Arrays.asList(0, 1, 3, 4, 6, 8, 10, 14, 15);
    ImmutableHomogeneousPair<Integer> result = DisjointExcess.calculate(a, b);

    assertEquals((Integer) 5, result.getFirst());
  }

  @Test
  public void excessWorksForComplicatedLists() {
    List<Integer> a = Arrays.asList(0, 1, 2, 4, 8, 11);
    List<Integer> b = Arrays.asList(0, 1, 3, 4, 6, 10, 14, 15);
    ImmutableHomogeneousPair<Integer> result = DisjointExcess.calculate(a, b);

    assertEquals((Integer) 2, result.getSecond());
  }

  @Test
  public void disjointWorksForShuffledComplicatedLists() {
    List<Integer> a = Arrays.asList(0, 1, 2, 4, 8, 11);
    List<Integer> b = Arrays.asList(0, 1, 3, 4, 6, 8, 10, 14, 15);

    Collections.shuffle(a);
    Collections.shuffle(b);

    ImmutableHomogeneousPair<Integer> result = DisjointExcess.calculate(a, b);

    assertEquals((Integer) 5, result.getFirst());
  }

  @Test
  public void excessWorksForShuffledComplicatedLists() {
    List<Integer> a = Arrays.asList(0, 1, 2, 4, 8, 11);
    List<Integer> b = Arrays.asList(0, 1, 3, 4, 6, 8, 10, 14, 15);

    Collections.shuffle(a);
    Collections.shuffle(b);

    ImmutableHomogeneousPair<Integer> result = DisjointExcess.calculate(a, b);

    assertEquals((Integer) 2, result.getSecond());
  }
}
