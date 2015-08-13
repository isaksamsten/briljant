package org.briljantframework.example.dataframe;

import org.briljantframework.dataframe.DataFrame;
import org.briljantframework.dataframe.MixedDataFrame;
import org.briljantframework.vector.Vector;

/**
 * Created by Isak Karlsson on 07/01/15.
 */
public class Ex1 {

  public static void main(String[] args) {
    Vector cars = Vector.of("Volvo", "BMW", "Saab");
    DataFrame engines = MixedDataFrame.of(
        "Brand", cars,
        "Engine Size", Vector.of("large", "small", "tiny"),
        "Breaking Horse Powers", Vector.of(100, 22, 3)
    );

    DataFrame sizes = MixedDataFrame.of(
        "Brand", cars,
        "wheel", Vector.of(21, 22, 33),
        "length", Vector.of(10, 20, 30.0));

    System.out.println(engines);
    System.out.println(sizes);

    System.out.println(engines.join(sizes, "Brand"));

//    System.out.println(DataFrames.innerJoin(engines, sizes, Arrays.asList(0)));
  }
}
