import java.io.*;

import org.briljantframework.serialization.InstancesProto.Instance;
import org.briljantframework.serialization.InstancesProto.Instance.Type;
import org.briljantframework.serialization.InstancesProto.Instance.Variable;

/**
 * Created by Isak Karlsson on 18/12/14.
 */
public class Main {

  public static void main(String[] args) throws IOException {

    Instance instance =
        Instance.newBuilder()
            .addVariables(Variable.newBuilder().setBinaryValue(1).setType(Type.BINARY))
              .addVariables(Variable.newBuilder().setDoubleValue(1).setType(Type.DOUBLE))
            .build();

    long s1 = System.currentTimeMillis();
    BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream("test.inst"));
    for (int i = 0; i < 100000; i++) {
      instance.writeDelimitedTo(out);
    }
    System.out.println(System.currentTimeMillis() - s1);


    BufferedInputStream in = new BufferedInputStream(new FileInputStream("test.inst"));
    long start = System.currentTimeMillis();
    Instance i;
    while ((i = Instance.parseDelimitedFrom(in)) != null) {
    }
    System.out.println(System.currentTimeMillis() - start);


  }
}
