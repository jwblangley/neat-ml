# neat-ml
An implementation of the NEAT (Neuroevolution through augmenting topologies) algorithm in Java. Originally found at http://nn.cs.utexas.edu/downloads/papers/stanley.ec02.pdf

## Quick start
This library has been built with the intention of making it as easy as possible to add NEAT to any project - without the need for deep understanding of how NEAT works.

Take the following quick example that shows how to use this library to learn the XOR function:

**TLDR** 
* a little bit of config
* create evolution (all the hard work has been done for you!)
    * Write the evaluator for a genotype
        * Convert the genotype into a neural network with the provided functions
        * Give the neural network inputs and receive its outputs in a single function
* Run the `evolve` function once for every generation
* Get the best performing genotype from the population
* Create a neural network from that genotype as before
* Done! You have a neural network that has learnt your desired behaviour

```java
import java.util.Random;
import jwblangley.neat.evolution.Evolution;
import jwblangley.neat.evolution.EvolutionFactory;
import jwblangley.neat.phenotype.Network;
import jwblangley.neat.genotype.NetworkGenotype;

public class LearnXor {
  public static void learnXor() {

    Random random = new Random();

    // Some configuration for the evolution
    final int numGenerations = 100;
    final int populationSize = 100;
    final int targetNumSpecies = 5;
    final int numProcessingThreads = 8;
    
    // Create a new optimisation with 2 inputs and 1 output
    Evolution evolution = EvolutionFactory.createOptimisation(2, 1, populationSize, 
      targetNumSpecies, numProcessingThreads, networkGenotype -> {
          
          // Build a neural network from the genotype. This is all done for you with this method!
          // There is an equivalent method for building linear output networks
          Network network = Network.createSigmoidOutputNetworkFromGenotype(networkGenotype);

          // Now that a network has been created, evaluate it however you see fit!
          // To prevent over-fitting to a particular problem, we want to actually evaluate it 
          // several times against randomised input and aggregate a score. (For XOR there are only
          // 4 possible combinations, but this is a good habit to get into for the general case)
          int numCorrect = 0;
          for (int i = 0; i < 5; i++) {
            final double a = Math.round(random.nextDouble());
            final double b = Math.round(random.nextDouble());

            // This is as simple as it is to get the output from your neural network!
            // We get the 0th index item as we want the first (and in this case only) output
            final double output = network.calculateOutputs(a, b).get(0);

            // Using "> 0.5" here is a good way to turn a sigmoid output (0-1) into a binary output! 
            final boolean expected = (a > 0.5) ^ (b > 0.5);
            final boolean actual = output > 0.5;

            if (expected == actual) {
              numCorrect++;
            }
          }
          
          // Return a score that tells the program how well the network did! Higher is better!
          return numCorrect * 100d / testsInEvaluate;
        });

    // Evolve!
    evolution.setVerbose(true);
    for (int i = 1; i <= numGenerations; i++) {
      System.out.println("Generation: " + i);
      // This call is all you need to evolve your population (one generation)!
      evolution.evolve(random);

      System.out.println();
    }

    // After the evolution is done (you will need to experiment to know how much training you need)
    // Get the best genotype from the population
    NetworkGenotype bestInPop = evolution.getFittestGenotype();
    
    // Create the network from this genotype as we did before
    Network bestNetwork = Network.createSigmoidOutputNetworkFromGenotype(bestInPop);

    // Congratulations! You now have a neural network that knows XOR
    boolean ff = bestNetwork.calculateOutputs(0d, 0d).get(0) > 0.5;
    boolean ft = bestNetwork.calculateOutputs(0d, 1d).get(0) > 0.5;
    boolean tf = bestNetwork.calculateOutputs(1d, 0d).get(0) > 0.5;
    boolean tt = bestNetwork.calculateOutputs(1d, 1d).get(0) > 0.5;

    System.out.println("false XOR false = " + ff);
    System.out.println("false XOR true = " + ft);
    System.out.println("true XOR false = " + tf);
    System.out.println("true XOR true = " + tt);

    // Prints:
    // false XOR false = false
    // false XOR true = true
    // true XOR false = true
    // true XOR true = false

  }
}    
```
