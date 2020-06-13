package jwblangley.neat.proto;

import com.google.protobuf.Message;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
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
  public static NetworkGenotype fromFile(File file) throws IOException {
    FileInputStream fip = new FileInputStream(file);

    Genotypes.NetworkGenotype network = Genotypes.NetworkGenotype.parseFrom(fip);
    fip.close();

    return new NetworkGenotype(network);
  }

}
