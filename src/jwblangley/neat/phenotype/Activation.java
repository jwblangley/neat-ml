package jwblangley.neat.phenotype;

import java.util.function.Function;

public enum Activation implements Function<Double, Double> {
  RELU(x -> Math.max(0, x)),
  SIGMOID(x -> Math.exp(x) / (Math.exp(x) + 1)),
  LINEAR(Function.identity());

  private final Function<Double, Double> func;

  Activation(Function<Double, Double> func) {
    this.func = func;
  }

  /**
   * Applies the activation function to a given input
   *
   * @param input the given input
   * @return the result
   */
  @Override
  public Double apply(Double input) {
    return func.apply(input);
  }
}
