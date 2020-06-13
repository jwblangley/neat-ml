package jwblangley.neat.proto;

import com.google.protobuf.Message;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class ProtoIO {

  public static void toDisk(Message item, File file) throws IOException {
    FileOutputStream fop = new FileOutputStream(file);
    item.writeTo(fop);

    fop.flush();
    fop.close();
  }

  public static Genotypes.NetworkGenotype fromDisk(File file) throws IOException {
    FileInputStream fip = new FileInputStream(file);

    Genotypes.NetworkGenotype network = Genotypes.NetworkGenotype.parseFrom(fip);
    fip.close();

    return network;
  }

}
