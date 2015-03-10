package org.briljantframework.io;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

import org.briljantframework.dataframe.DataFrame;
import org.briljantframework.dataseries.DataSeriesCollection;
import org.briljantframework.distance.Distance;
import org.briljantframework.distance.SimilarityDistance;
import org.briljantframework.matrix.DoubleMatrix;
import org.briljantframework.similiarity.SmithWatermanSimilarity;
import org.briljantframework.vector.StringVector;
import org.briljantframework.vector.Vector;
import org.junit.Test;

public class SequenceInputStreamTest {

  @Test
  public void testReadSequenceFile() throws Exception {
    DataInputStream in =
        new SequenceInputStream(new FileInputStream("/Users/isak-kar/Desktop/ade_hist.sequences"));

    DataFrame frame = new DataSeriesCollection.Builder(StringVector.TYPE).read(in).build();
    DoubleMatrix distanceMatrix = DoubleMatrix.newMatrix(frame.rows(), frame.rows());

    Distance distance = new SimilarityDistance(new SmithWatermanSimilarity(-1, 0, 0));
    for (int i = 0; i < frame.rows(); i++) {
      System.out.println("Done with " + i);
      for (int j = 0; j < frame.rows(); j++) {
        if (i == j) {
          distanceMatrix.set(i, j, 0);
          continue;
        }
        Vector a = frame.getRecord(i);
        Vector b = frame.getRecord(j);
        double compute = distance.compute(a, b);
        distanceMatrix.set(i, j, compute);
      }
    }
    BufferedWriter out =
        new BufferedWriter(new OutputStreamWriter(new FileOutputStream(
            "/Users/isak-kar/Desktop/out.matrix")));
    for (int i = 0; i < distanceMatrix.rows(); i++) {
      for (int j = 0; j < distanceMatrix.columns(); j++) {
        out.write(String.valueOf(distanceMatrix.get(i, j)));
        out.write(" ");
      }
      out.write("\n");
    }
  }
}
