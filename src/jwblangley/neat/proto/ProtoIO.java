package jwblangley.neat.proto;

import com.google.protobuf.Message;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class ProtoIO {

  /**
   * Write a protobuf object to a file
   *
   * @param item protobuf object to be written
   * @param file file to be written to
   * @throws IOException when writing to the file fails
   */
  public static void toFile(Message item, File file) throws IOException {
    FileOutputStream fop = new FileOutputStream(file);
    item.writeTo(fop);

    fop.flush();
    fop.close();
  }

  /**
   * Read a NetworkGenotype protobuf object from a file
   *
   * @param file file to read from
   * @return NetworkGenotype protobuf object (needs converting before use)
   * @throws IOException when reading from the file fails
   */
  public static Genotypes.NetworkGenotype fromFile(File file) throws IOException {
    FileInputStream fip = new FileInputStream(file);

    Genotypes.NetworkGenotype network = Genotypes.NetworkGenotype.parseFrom(fip);
    fip.close();

    return network;
  }

}
