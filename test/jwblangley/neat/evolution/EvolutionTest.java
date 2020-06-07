package jwblangley.neat.evolution;

import static org.junit.Assert.assertEquals;

import java.util.Random;
import jwblangley.neat.genotype.ConnectionGenotype;
import jwblangley.neat.genotype.NetworkGenotype;
import jwblangley.neat.genotype.NeuronGenotype;
import jwblangley.neat.genotype.NeuronLayer;
import org.junit.Test;

public class EvolutionTest {

  @Test
  public void evolveWeightSumToOneHundred() {
    final int target = 100;
    final double tolerance = 0.01;
    final int numGenerations = 100;
    final int populationSize = 100;

    Random random = new Random(100);

    InnovationGenerator innovationCounter = new InnovationGenerator();

    // Example starting network
    NetworkGenotype network = new NetworkGenotype();
    network.addNeuron(new NeuronGenotype(NeuronLayer.INPUT));
    network.addNeuron(new NeuronGenotype(NeuronLayer.INPUT));
    network.addNeuron(new NeuronGenotype(NeuronLayer.OUTPUT));

    network.addConnection(new ConnectionGenotype(1, 3, innovationCounter.next(), 0.5, true));
    network.addConnection(new ConnectionGenotype(2, 3, innovationCounter.next(), 0.5, true));

    Evolution evolution = new Evolution(populationSize, network, innovationCounter, geno -> {
      double weightSum = 0;
      for (ConnectionGenotype connection : geno.getConnections()) {
        if (connection.isEnabled()) {
          weightSum += Math.abs(connection.getWeight());
        }
      }
      double difference = Math.abs(weightSum - target);
      return (1000d / difference);
    });

    double weightSum = 0;

    for (int i = 1; i <= numGenerations; i++) {
      evolution.evolve(random);
      System.out.print("Generation: " + i);
      System.out.print("\tHighest fitness: " + evolution.getHighestFitness());
      System.out.print("\tNumber of species: " + evolution.getNumberOfSpecies());

      weightSum = 0;
      for (ConnectionGenotype connection : evolution.getFittestGenotype().getConnections()) {
        if (connection.isEnabled()) {
          weightSum += Math.abs(connection.getWeight());
        }
      }
      System.out.println("\tWeight sum: " + weightSum);
    }

    assertEquals(target, weightSum, tolerance);
  }

}
