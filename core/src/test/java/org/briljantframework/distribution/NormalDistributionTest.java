package org.briljantframework.distribution;

import static org.briljantframework.distribution.NormalDistribution.cdf;
import static org.briljantframework.distribution.NormalDistribution.ppf;
import static org.briljantframework.vector.Vectors.linspace;
import static org.junit.Assert.assertEquals;

import org.briljantframework.dataframe.DataFrame;
import org.briljantframework.dataseries.AggregateApproximation;
import org.briljantframework.dataseries.DataSeriesCollection;
import org.briljantframework.dataseries.DataSeriesNormalization;
import org.briljantframework.dataseries.MeanAggregator;
import org.briljantframework.vector.DoubleVector;
import org.briljantframework.vector.StringVector;
import org.briljantframework.vector.Vector;
import org.junit.Test;

public class NormalDistributionTest {

  @Test
  public void testPpf() throws Exception {
    assertEquals(Double.POSITIVE_INFINITY, ppf(1), 0.0001);
    assertEquals(Double.NEGATIVE_INFINITY, ppf(0), 0.0001);
    assertEquals(5.199337, ppf(0.9999999), 0.0001);
    assertEquals(0.2, cdf(ppf(0.2)), 0.0001);


    Vector vec = DoubleVector.wrap(1, 2, 3, 4);
    System.out.println(ppf(vec));

    DataSeriesCollection.Builder b =
        new DataSeriesCollection.Builder(DoubleVector.TYPE).addRow(vec);

    Vector norm = new DataSeriesNormalization().transform(b.build()).getRow(0);
    System.out.println();



  }

  @Test
  public void testName() throws Exception {
    DoubleVector a =
        DoubleVector.wrap(2.02, 2.33, 2.99, 6.85, 9.20, 8.80, 7.50, 6.00, 5.85, 3.85, 4.85, 3.85,
            2.22, 1.45, 1.34);
    DoubleVector b =
        DoubleVector.wrap(0.50, 1.29, 2.58, 3.83, 3.25, 4.25, 3.83, 5.63, 6.44, 6.25, 8.75, 8.83,
            3.25, 0.75, 0.72);
    DataFrame frame =
        new DataSeriesNormalization().transform(new DataSeriesCollection.Builder(DoubleVector.TYPE)
            .addRow(a).addRow(b).build());

    Vector alphabet = new StringVector("a", "b", "c", "d");
    Vector thresholds =
        ppf(linspace(1.0 / alphabet.size(), 1.0 - 1.0 / alphabet.size(), alphabet.size() - 1));
    System.out.println(alphabet);
    System.out.println(thresholds);
    frame = new AggregateApproximation(new MeanAggregator(9)).transform(frame);

    DataSeriesCollection.Builder builder = new DataSeriesCollection.Builder(StringVector.TYPE);
    for (int i = 0; i < frame.rows(); i++) {
      Vector series = frame.getRow(i);
      StringVector.Builder ns = new StringVector.Builder();
      for (int j = 0; j < series.size(); j++) {
        double value = series.getAsDouble(j);
        if (value <= thresholds.getAsDouble(0)) {
          ns.set(j, alphabet.getAsString(0));
        } else if (value >= thresholds.getAsDouble(thresholds.size() - 1)) {
          ns.set(j, alphabet.getAsString(alphabet.size() - 1));
        } else {
          int index = 0;
          for (int k = 0; k < thresholds.size(); k++) {
            if (thresholds.getAsDouble(k) <= value) {
              index = k;
            } else {
              break;
            }
          }
          ns.set(j, alphabet.getAsString(index + 1));
        }
      }
      builder.addRow(ns);
    }
    System.out.println(builder.build());
  }
}
