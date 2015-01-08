package org.briljantframework.example.dataframe;

import com.google.common.collect.ImmutableMap;
import org.briljantframework.dataframe.DataFrame;
import org.briljantframework.dataframe.MixedDataFrame;
import org.briljantframework.vector.IntVector;
import org.briljantframework.vector.StringVector;
import org.briljantframework.vector.Vector;

/**
 * Created by Isak Karlsson on 07/01/15.
 */
public class Ex1 {

  public static void main(String[] args) {
    Vector car = new StringVector("Volvo", "BMW", "Saab");
    Vector engine = new StringVector("large", "small", "tiny");
    Vector bhp = new IntVector(100, 22, 3);

    DataFrame frame =
        MixedDataFrame.of("Brand", car, "Engine Size", engine, "Breaking Horse Powers", bhp);
    System.out.println(frame);
  }
}
