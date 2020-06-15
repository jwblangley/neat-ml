package jwblangley.neat.evolution;

import java.util.List;
import jwblangley.neat.genotype.NetworkGenotype;

/**
 * Functional interface to be implemented for the given optimisation that returns a List of
 * fitnesses for a given List of genotypes, usually by simulation. A strictly greater fitness must
 * only be achieved if and only if the genotype was strictly better at solving the problem to be
 * optimised. The returned list MUST be kept in the order of input such that the nth double returned
 * is the fitness for the nth genotype inputted
 */
@FunctionalInterface
public interface BulkEvaluator {

  List<Double> evaluate(List<NetworkGenotype> networkGenotypes);

}
