package jwblangley.neat.evolution;

import jwblangley.neat.genotype.NetworkGenotype;

/**
 * Functional interface to be implemented for the given optimisation that returns a fitness for a
 * given genotype, usually by simulation. A strictly greater fitness must only be achieved if and
 * only if the genotype was strictly better at solving the problem to be optimised.
 */
@FunctionalInterface
public interface Evaluator {

  /**
   * Method to be implemented for the given optimisation that returns a fitness for a given
   * genotype, usually by simulation. A strictly greater fitness must only be achieved if and only
   * if the genotype was strictly better at solving the problem to be optimised.
   *
   * @param networkGenotype The genotype to be evaluated
   * @return fitness: The fitness score
   */
  double evaluate(NetworkGenotype networkGenotype);
}
