package jwblangley.neat.proto;

import com.google.protobuf.Message;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import jwblangley.neat.evolution.BulkEvaluator;
import jwblangley.neat.evolution.SingleEvaluator;
import jwblangley.neat.evolution.Evolution;
import jwblangley.neat.genotype.NetworkGenotype;

public class ProtoIO {

  /**
   * Write a protobuf equivalent object to a file
   *
   * @param item protobuf equivalent object to be written
   * @param file file to be written to
   * @throws IOException when writing to the file fails
   */
  public static void toFile(ProtoEquivalent item, File file) throws IOException {
    FileOutputStream fop = new FileOutputStream(file);
    Message proto = item.toProto();
    proto.writeTo(fop);

    fop.flush();
    fop.close();
  }

  /**
   * Read a NetworkGenotype object from a file
   *
   * @param file file to read from
   * @return NetworkGenotype object read from file
   * @throws IOException when reading from the file fails
   */
  public static NetworkGenotype networkFromFile(File file) throws IOException {
    FileInputStream fip = new FileInputStream(file);

    Genotypes.NetworkGenotype network = Genotypes.NetworkGenotype.parseFrom(fip);
    fip.close();

    return new NetworkGenotype(network);
  }

  /**
   * Read an Evolution object from a file
   * At least one call to evolve on the new object must happen before statistics are available
   *
   * @param file                 file to read from
   * @param targetNumSpecies     number of targeted species in the population
   * @param numProcessingThreads Number of concurrent threads to evaluate the population with
   * @param singleEvaluator      Function to simulate and evaluate a single genotype
   * @return Evolution object read from file
   * @throws IOException when reading from the file fails
   */
  public static Evolution evolutionFromFile(File file, int targetNumSpecies,
      int numProcessingThreads, SingleEvaluator singleEvaluator) throws IOException {

    FileInputStream fip = new FileInputStream(file);

    EvolutionOuterClass.Evolution protoEvolution = EvolutionOuterClass.Evolution.parseFrom(fip);

    fip.close();

    return new Evolution(protoEvolution, targetNumSpecies, numProcessingThreads, singleEvaluator);
  }

  /**
   * Read an Evolution object from a file
   * At least one call to evolve on the new object must happen before statistics are available
   *
   * @param file                 file to read from
   * @param targetNumSpecies     number of targeted species in the population
   * @param bulkEvaluator        Function to simulate and evaluate a list of genotype
   * @return Evolution object read from file
   * @throws IOException when reading from the file fails
   */
  public static Evolution evolutionFromFile(File file, int targetNumSpecies,
      BulkEvaluator bulkEvaluator) throws IOException {

    FileInputStream fip = new FileInputStream(file);

    EvolutionOuterClass.Evolution protoEvolution = EvolutionOuterClass.Evolution.parseFrom(fip);

    fip.close();

    return new Evolution(protoEvolution, targetNumSpecies, bulkEvaluator);
  }

}
