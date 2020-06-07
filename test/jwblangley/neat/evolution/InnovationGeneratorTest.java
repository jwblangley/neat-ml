package jwblangley.neat.evolution;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class InnovationGeneratorTest {

  @Test
  public void innovationGeneratorStartsFromZero() {
    assertEquals(0, new InnovationGenerator().next());
  }

  @Test
  public void innovationGeneratorStartsFromGiven() {
    assertEquals(5, new InnovationGenerator(5).next());
  }

  @Test
  public void innovationGeneratorIncrements() {
    InnovationGenerator innovationGenerator = new InnovationGenerator();

    for (int i = 0; i < 100; i++) {
      assertEquals(i, innovationGenerator.next());
    }
  }
}
