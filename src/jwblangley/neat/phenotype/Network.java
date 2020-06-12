package jwblangley.neat.phenotype;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import jwblangley.neat.genotype.ConnectionGenotype;
import jwblangley.neat.genotype.NetworkGenotype;
import jwblangley.neat.genotype.NeuronGenotype;
import jwblangley.neat.genotype.NeuronLayer;

/**
 * Phenotype for a neural network
 */
public class Network {

  private final List<Neuron> neurons;
  private final List<InputNeuron> inputNeurons;
  private final List<Neuron> outputNeurons;

  private Network() {
    this.neurons = new ArrayList<>();
    this.inputNeurons = new ArrayList<>();
    this.outputNeurons = new ArrayList<>();
  }

  /**
   * Creates a new neural network for solving regression problems from a given NetworkGenotype.
   * Input and Hidden neurons use ReLu for activation whilst Output neurons use a linear activation
   * function to ensure a full range
   *
   * @param genotype genotype to build phenotype from
   * @return constructed neural network (phenotype)
   */
  public static Network createRegressionNetworkFromGenotype(NetworkGenotype genotype) {
    return createNetworkFromGenotype(genotype, Activation.LINEAR);
  }

  /**
   * Creates a new neural network for solving various optimisation problems from a given
   * NetworkGenotype. Input and Hidden neurons use ReLu for activation whilst Output neurons use
   * sigmoid for activation.
   *
   * @param genotype genotype to build phenotype from
   * @return constructed neural network (phenotype)
   */
  public static Network createSigmoidOutputNetworkFromGenotype(NetworkGenotype genotype) {
    return createNetworkFromGenotype(genotype, Activation.SIGMOID);
  }

  private static Network createNetworkFromGenotype(NetworkGenotype genotype,
      Function<Double, Double> outputActivation) {

    Network network = new Network();

    Map<Integer, Neuron> uidNeuronMap = new HashMap<>();

    /*
     Sort neurons first by guid to ensure that input and output neurons
     are always added in the same order
     */
    List<NeuronGenotype> neuronGenotypes = new ArrayList<>(genotype.getNeurons());
    neuronGenotypes.sort(Comparator.comparingDouble(NeuronGenotype::getUid));

    // Add neurons
    for (NeuronGenotype neuronGenotype : neuronGenotypes) {
      Neuron neuron;

      if (neuronGenotype.getLayer() == NeuronLayer.INPUT) {
        neuron = new InputNeuron(Activation.RELU);
        network.inputNeurons.add((InputNeuron) neuron);
      } else {
        neuron = new Neuron(neuronGenotype.getLayer() == NeuronLayer.OUTPUT
            ? outputActivation
            : Activation.RELU);

        if (neuronGenotype.getLayer() == NeuronLayer.OUTPUT) {
          network.outputNeurons.add(neuron);
        }
      }

      network.neurons.add(neuron);
      uidNeuronMap.put(neuronGenotype.getUid(), neuron);
    }

    // Add connections
    for (ConnectionGenotype connectionGenotype : genotype.getConnections()) {
      // Skip disabled connections
      if (!connectionGenotype.isEnabled()) {
        continue;
      }

      Neuron toNeuron = uidNeuronMap.get(connectionGenotype.getNeuronTo());

      toNeuron.addInput(uidNeuronMap.get(connectionGenotype.getNeuronFrom()),
          connectionGenotype.getWeight());
    }

    return network;
  }

  /**
   * Calculate the output of this neural network for given (ordered) inputs
   *
   * @param inputs ordered inputs in a List
   * @return calculated result
   */
  public List<Double> calculateOutputs(List<Double> inputs) {
    if (inputs.size() != inputNeurons.size()) {
      throw new InputMismatchException(
          "Number of provided inputs does not match number of input neurons");
    }

    // Clear any residual stored outputs
    for (Neuron neuron : neurons) {
      neuron.clear();
    }

    // Set inputs
    for (int i = 0; i < inputs.size(); i++) {
      inputNeurons.get(i).setInput(inputs.get(i));
    }

    // Try to calculate each node until eventually the output nodes will be outputting
    while (!outputNeurons.stream().allMatch(Neuron::isOutputting)) {
      for (Neuron neuron : neurons) {
        neuron.tryCalculate();
      }
    }

    // Get results
    List<Double> results = new ArrayList<>(outputNeurons.size());
    for (Neuron outputNeuron : outputNeurons) {
      results.add(outputNeuron.getOutput());
    }
    return results;
  }

  /**
   * Calculate the output of this neural network for given (ordered) inputs
   *
   * @param inputs ordered inputs as varargs
   * @return calculated result
   */
  public List<Double> calculateOutputs(Double... inputs) {
    return calculateOutputs(Arrays.asList(inputs));
  }
}
