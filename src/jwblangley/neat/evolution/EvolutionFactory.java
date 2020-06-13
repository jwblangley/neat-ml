package jwblangley.neat.evolution;

import java.util.ArrayList;
import java.util.List;
import jwblangley.neat.genotype.ConnectionGenotype;
import jwblangley.neat.genotype.NetworkGenotype;
import jwblangley.neat.genotype.NeuronGenotype;
import jwblangley.neat.genotype.NeuronLayer;

public class EvolutionFactory {

  public static Evolution createOptimisation(int numInputs, int numOutputs, int populationSize,
      int targetNumSpecies, int numProcessingThreads, Evaluator evaluator) {

    // Create starting genotype
    final NetworkGenotype network = new NetworkGenotype();

    final InnovationGenerator innovationGenerator = new InnovationGenerator();

    final List<NeuronGenotype> inputNeurons = new ArrayList<>(numInputs);
    final List<NeuronGenotype> outputNeurons = new ArrayList<>(numOutputs);

    // Add input neurons
    for (int i = 0; i < numInputs; i++) {
      NeuronGenotype inputNeuron = new NeuronGenotype(NeuronLayer.INPUT);
      inputNeurons.add(inputNeuron);
      network.addNeuron(inputNeuron);
    }

    // Add output neurons
    for (int i = 0; i < numOutputs; i++) {
      NeuronGenotype outputNeuron = new NeuronGenotype(NeuronLayer.OUTPUT);
      outputNeurons.add(outputNeuron);
      network.addNeuron(outputNeuron);
    }

    // Fully connect starting genotype
    for (NeuronGenotype inputNeuron : inputNeurons) {
      for (NeuronGenotype outputNeuron : outputNeurons) {
        ConnectionGenotype connection = new ConnectionGenotype(inputNeuron.getUid(),
            outputNeuron.getUid(), innovationGenerator.next(), 0, true);
        network.addConnection(connection);
      }
    }

    return new Evolution(populationSize, targetNumSpecies, network, innovationGenerator,
        numProcessingThreads, evaluator);
  }

}
