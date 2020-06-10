package jwblangley.neat.phenotype;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class Neuron {

  private final Function<Double, Double> activation;
  private final List<Neuron> inputs;
  private final Map<Neuron, Double> inputWeightMap;

  private boolean outputting;

  private double output;

  private Neuron(Activation activation) {
    this.activation = activation;
    this.inputs = new ArrayList<>();
    this.inputWeightMap = new HashMap<>();
  }

  /**
   * Create a new neuron with ReLu activation function
   * @return the neuron
   */
  public static Neuron createReLuNeuron() {
    return new Neuron(Activation.RELU);
  }

  /**
   * Create a new neuron with Sigmoid activation function
   * @return the neuron
   */
  public static Neuron createSigmoidNeuron() {
    return new Neuron(Activation.SIGMOID);
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
   * If all inputs are outputting, calculate the output, such that it can be acccessed
   * with getOutput()
   * @return whether the calculation was successful (unsuccessful if an input neuron is
   * not outputting)
   */
  public boolean tryCalculate() {
    if (!inputs.stream().allMatch(Neuron::isOutputting)) {
      return false;
    }
    double inputAcc = 0;
    for (Neuron input: inputs) {
      inputAcc += inputAcc * inputWeightMap.get(input);
    }

    output = activation.apply(inputAcc);
    return true;
  }
}
