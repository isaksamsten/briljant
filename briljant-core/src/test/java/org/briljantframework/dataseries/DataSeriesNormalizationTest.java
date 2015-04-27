package org.briljantframework.dataseries;

import org.briljantframework.dataframe.DataFrame;
import org.briljantframework.distance.Distance;
import org.briljantframework.distance.Euclidean;
import org.briljantframework.vector.DoubleVector;
import org.junit.Test;

public class DataSeriesNormalizationTest {

  @Test
  public void testFit() throws Exception {
    DoubleVector a =
        DoubleVector.wrap(2.02, 2.33, 2.99, 6.85, 9.20, 8.80, 7.50, 6.00, 5.85, 3.85, 4.85, 3.85,
            2.22, 1.45, 1.34);
    DoubleVector b =
        DoubleVector.wrap(0.50, 1.29, 2.58, 3.83, 3.25, 4.25, 3.83, 5.63, 6.44, 6.25, 8.75, 8.83,
            3.25, 0.75, 0.72);
    Distance euclidean = Euclidean.getInstance();

    DataFrame frame =
        new DataSeriesCollection.Builder(DoubleVector.TYPE).addRecord(a).addRecord(b).build();

    System.out.println(frame);
    DataFrame normalized = new DataSeriesNormalization().transform(frame);
    System.out.println(Approximations.sax(normalized, 9, "a", "b", "c", "d"));
    //
    // System.out.println(frame);
    // System.out.println(euclidean.distance(frame.getRow(0), frame.getRow(1)));
    // assertEquals(11.42126, euclidean.distance(frame.getRow(0), frame.getRow(1)), 0.001);
    //
    // System.out.println(normalized);
    // System.out.println(euclidean.distance(normalized.getRow(0), normalized.getRow(1)));
    //
    // assertEquals(4.170542, euclidean.distance(normalized.getRow(0), normalized.getRow(1)),
    // 0.001);
    //
    // System.out.println(SymbolicAggregator.newLookupTable(Arrays.asList("a", "b", "c", "d")));
    // System.out.println(Approximations.paa(normalized, 5));
    // DataFrame sax = Approximations.sax(normalized, 9, Approximations.getAlphabet(12));
    // System.out.println(sax);
    //
    //
    // DataFrame syntheticControl = Datasets.loadSyntheticControl();
    //
    //
    // Vector y = syntheticControl.getColumn(0);
    // DataFrame x = Approximations.paa(syntheticControl.removeColumn(0), 40);
    // System.out.println(x);
    //
    //
    //
    // long start = System.currentTimeMillis();
    // DataFrame saxs = Approximations.sax(syntheticControl, 20, "a", "b", "c", "d");
    // System.out.println(System.currentTimeMillis() - start);
    //
    // Distance distance = new SaxDistance(saxs.columns(), "a", "b", "c", "d");
    //
    // start = System.currentTimeMillis();
    // System.out.println("Approx distance 0 3: " + distance.distance(saxs.getRow(0),
    // saxs.getRow(3)));
    // System.out.println("Real   distance 0 3: "
    // + Euclidean.getInstance().distance(syntheticControl.getRow(0), syntheticControl.getRow(3)));
    //
    // System.out.println(System.currentTimeMillis() - start);
    //
    // start = System.currentTimeMillis();
    // System.out.println("Approx distance 0 5: " + distance.distance(saxs.getRow(0),
    // saxs.getRow(5)));
    // System.out.println("Real   distance 0 5:"
    // + Euclidean.getInstance().distance(syntheticControl.getRow(0), syntheticControl.getRow(5)));
    //
    // System.out.println(System.currentTimeMillis() - start);
    //
    // System.out.println(saxs);



  }
}
