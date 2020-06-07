package jwblangley.neat.evolution;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Generator for innovation markers
 */
public class InnovationGenerator {
  private final AtomicInteger atomicInteger;

  /**
   * Construct a new InnovationCounter counting from 0
   */
  public InnovationGenerator() {
    this.atomicInteger = new AtomicInteger();
  }

  /**
   *
   * @param initial
   */
  public InnovationGenerator(int initial) {
    this.atomicInteger = new AtomicInteger(initial);
  }

  public int next() {
    return atomicInteger.getAndIncrement();
  }
}
