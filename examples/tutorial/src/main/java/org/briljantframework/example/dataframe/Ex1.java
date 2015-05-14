package org.briljantframework.example.dataframe;

import org.briljantframework.dataframe.DataFrame;
import org.briljantframework.dataframe.DataFrames;
import org.briljantframework.dataframe.MixedDataFrame;
import org.briljantframework.vector.DoubleVector;
import org.briljantframework.vector.IntVector;
import org.briljantframework.vector.StringVector;
import org.briljantframework.vector.Vector;

import java.util.Arrays;

/**
 * Created by Isak Karlsson on 07/01/15.
 */
public class Ex1 {

  public static void main(String[] args) {
    Vector car = new StringVector("Volvo", "BMW", "Saab");
    Vector engine = new StringVector("large", "small", "tiny");
    Vector bhp = new IntVector(100, 22, 3);

    DataFrame engines =
        MixedDataFrame.of("Brand", car, "Engine Size", engine, "Breaking Horse Powers", bhp);


    Vector wheel = new IntVector(21, 22, 33);
    Vector length = new DoubleVector(10, 20, 30.0);
    DataFrame sizes = MixedDataFrame.of("Brand", car, "wheel", wheel, "length", length);
    System.out.println(engines);
    System.out.println(sizes);

//    System.out.println(DataFrames.innerJoin(engines, sizes, Arrays.asList(0)));
  }
}
