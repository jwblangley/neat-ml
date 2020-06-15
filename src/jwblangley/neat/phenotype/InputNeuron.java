package jwblangley.neat.phenotype;

import java.util.function.Function;

/**
 * Phenotype for an input neuron
 */
public class InputNeuron extends Neuron {

  private double input;

  /**
   * Construct a new Neuron
   *
   * @param activation activation function
   */
  public InputNeuron(Function<Double, Double> activation) {
    super(activation);
  }

  /**
   * Set the input of the InputNeuron
   *
   * @param input value to be set
   */
  public void setInput(double input) {
    this.input = input;
  }

  /**
   * Throws UnsupportedOperationException
   *
   * @param input          -
   * @param incomingWeight -
   */
  @Override
  public void addInput(Neuron input, double incomingWeight) {
    throw new UnsupportedOperationException("Input neurons cannot have neuron inputs");
  }

  /**
   * Calculates
   *
   * @return whether the calculation was successful (unsuccessful if an input neuron is not
   * outputting)
   */
  @Override
  public boolean tryCalculate() {
    // Skip if already calculated
    if (isOutputting()) {
      return true;
    }

    output = activation.apply(input);
    outputting = true;
    return true;
  }
}
