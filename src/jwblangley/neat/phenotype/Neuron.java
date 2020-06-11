package jwblangley.neat.phenotype;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Phenotype for a neuron
 */
public class Neuron {

  protected final Function<Double, Double> activation;
  private final List<Neuron> inputs;
  private final Map<Neuron, Double> inputWeightMap;

  protected boolean outputting;
  protected double output;

  /**
   * Construct a new Neuron
   * @param activation activation function
   */
  public Neuron(Function<Double, Double> activation) {
    this.activation = activation;
    this.inputs = new ArrayList<>();
    this.inputWeightMap = new HashMap<>();
  }

  /**
   *
   * @return whether this neuron has calculated it's output
   */
  public boolean isOutputting() {
    return outputting;
  }

  /**
   * Returns the output of this neuron. If this is called before the calculation has completed
   * a RuntimeException is thrown. Check prior to usage with isOutputting()
   * @return the calculated output of this neuron
   */
  public double getOutput() {
    if (!outputting) {
      throw new RuntimeException("Result has not yet been calculated");
    }
    return output;
  }

  /**
   * Stops this neuron outputting. All neurons must be cleared before network calculation begins
   */
  public void clear() {
    outputting = false;
  }

  /**
   * Connect a neuron as an input to this neuron
   * @param input input neuron
   * @param incomingWeight weight of connection from input neuron to this neuron
   */
  public void addInput(Neuron input, double incomingWeight) {
    inputs.add(input);
    inputWeightMap.put(input, incomingWeight);
  }


  /**
   * If all inputs are outputting, calculate the output, such that it can be accessed
   * with getOutput()
   * @return whether the calculation was successful (unsuccessful if an input neuron is
   * not outputting)
   */
  public boolean tryCalculate() {
    // Skip if calculation has already been carried out
    if (isOutputting()) {
      return true;
    }

    // Fail if some inputs are not outputting
    if (!inputs.stream().allMatch(Neuron::isOutputting)) {
      return false;
    }
    double inputAcc = 0;
    for (Neuron input: inputs) {
      inputAcc += inputAcc * inputWeightMap.get(input);
    }

    output = activation.apply(inputAcc);
    outputting = true;
    return true;
  }
}
