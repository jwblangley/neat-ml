package jwblangley.neat.phenotype;

import java.util.function.Function;

public class Activation {

  public static Function<Double, Double> reLu() {
    return x -> Math.max(0, x);
  }

  public static Function<Double, Double> sigmoid() {
    return x -> Math.exp(x) / (Math.exp(x) + 1);
  }
}
