package jwblangley.neat.phenotype;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import jwblangley.neat.genotype.ConnectionGenotype;
import jwblangley.neat.genotype.NetworkGenotype;
import jwblangley.neat.genotype.NeuronGenotype;
import jwblangley.neat.genotype.NeuronLayer;

public class Network {

  private final List<Neuron> neurons;

  private Network() {
    this.neurons = new ArrayList<>();
  }

  public static Network createRegressionNetworkFromGenotype(NetworkGenotype genotype) {
    return createNetworkFromGenotype(genotype, Activation.LINEAR);
  }

  public static Network createSigmoidOutputNetworkFromGenotype(NetworkGenotype genotype) {
    return createNetworkFromGenotype(genotype, Activation.SIGMOID);
  }

  private static Network createNetworkFromGenotype(NetworkGenotype genotype,
      Function<Double, Double> outputActivation) {

    Network network = new Network();

    Map<Integer, Neuron> uidNeuronMap = new HashMap<>();

    // Add neurons
    // TODO: manage input neurons
    for (NeuronGenotype neuronGenotype : genotype.getNeurons()) {
      Neuron neuron = new Neuron(neuronGenotype.getLayer() == NeuronLayer.OUTPUT
          ? outputActivation
          : Activation.RELU);
      uidNeuronMap.put(neuronGenotype.getUid(), neuron);
    }

    for (ConnectionGenotype connectionGenotype : genotype.getConnections()) {
      Neuron toNeuron = uidNeuronMap.get(connectionGenotype.getNeuronTo());

      toNeuron.addInput(uidNeuronMap.get(connectionGenotype.getNeuronFrom()),
          connectionGenotype.getWeight());
    }

    return network;
  }
}
