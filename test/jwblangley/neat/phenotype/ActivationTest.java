package jwblangley.neat.phenotype;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ActivationTest {

  final static double TOLERANCE = 0.0000001;
  final static double SIGMOID_APPROACH_TOLERANCE = 0.001;

  @Test
  public void linearIsIdenticalForPositive() {
    assertEquals(1d, Activation.LINEAR.apply(1d), TOLERANCE);
    assertEquals(10d, Activation.LINEAR.apply(10d), TOLERANCE);
    assertEquals(100d, Activation.LINEAR.apply(100d), TOLERANCE);
  }

  @Test
  public void linearIsIdenticalForNegative() {
    assertEquals(-1d, Activation.LINEAR.apply(-1d), TOLERANCE);
    assertEquals(-10d, Activation.LINEAR.apply(-10d), TOLERANCE);
    assertEquals(-100d, Activation.LINEAR.apply(-100d), TOLERANCE);
  }

  @Test
  public void linearIsZeroForZero() {
    assertEquals(0d, Activation.LINEAR.apply(0d), TOLERANCE);
  }

  @Test
  public void reLuIsIdenticalForPositive() {
    assertEquals(1d, Activation.RELU.apply(1d), TOLERANCE);
    assertEquals(10d, Activation.RELU.apply(10d), TOLERANCE);
    assertEquals(100d, Activation.RELU.apply(100d), TOLERANCE);
  }

  @Test
  public void reLuIsZeroForNegative() {
    assertEquals(0d, Activation.RELU.apply(-1d), TOLERANCE);
    assertEquals(0d, Activation.RELU.apply(-10d), TOLERANCE);
    assertEquals(0d, Activation.RELU.apply(-100d), TOLERANCE);
  }

  @Test
  public void reLuIsZeroForZero() {
    assertEquals(0d, Activation.RELU.apply(0d), TOLERANCE);
  }

  @Test
  public void sigmoidIsNearOneForLargePositive() {
    assertEquals(1d, Activation.SIGMOID.apply(100d), SIGMOID_APPROACH_TOLERANCE);
  }

  @Test
  public void sigmoidIsNearZeroForLargeNegative() {
    assertEquals(0d, Activation.SIGMOID.apply(-100d), SIGMOID_APPROACH_TOLERANCE);
  }

  @Test
  public void sigmoidIsPointFiveAtZero() {
    assertEquals(0.5d, Activation.SIGMOID.apply(0d), TOLERANCE);
  }
}
