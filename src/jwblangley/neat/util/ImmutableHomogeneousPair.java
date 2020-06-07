package jwblangley.neat.util;

public class ImmutableHomogeneousPair<T>  {
  private final T first;
  private final T second;

  public ImmutableHomogeneousPair(T first, T second) {
    this.first = first;
    this.second = second;
  }

  public T getFirst() {
    return first;
  }

  public T getSecond() {
    return second;
  }
}
