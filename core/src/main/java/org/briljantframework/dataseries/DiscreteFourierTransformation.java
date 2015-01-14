package org.briljantframework.dataseries;

import static org.briljantframework.math.transform.DiscreteFourierTransform.fft;
import static org.briljantframework.math.transform.DiscreteFourierTransform.ifft;

import org.briljantframework.dataframe.DataFrame;
import org.briljantframework.dataframe.DataFrameRow;
import org.briljantframework.dataframe.transform.InvertibleTransformation;
import org.briljantframework.matrix.ComplexMatrix;
import org.briljantframework.matrix.DoubleMatrix;
import org.briljantframework.vector.ComplexVector;
import org.briljantframework.vector.DoubleVector;

/**
 * @author Isak Karlsson
 */
public class DiscreteFourierTransformation implements InvertibleTransformation {

  /**
   * Asserts that each row has {@link org.briljantframework.vector.DoubleVector#TYPE}.
   * 
   * @param x data frame to transform
   * @return a new data frame; each row has type
   *         {@link org.briljantframework.vector.ComplexVector#TYPE}
   */
  @Override
  public DataFrame transform(DataFrame x) {
    DataSeriesCollection.Builder builder = new DataSeriesCollection.Builder(ComplexVector.TYPE);
    for (DataFrameRow row : x) {
      if (row.getType() != DoubleVector.TYPE) {
        throw new IllegalArgumentException();
      }
      DoubleMatrix time = row.asMatrix().asDoubleMatrix();
      ComplexMatrix freq = fft(time);
      ComplexVector.Builder rowBuilder = new ComplexVector.Builder(0, freq.size());
      for (int i = 0; i < freq.size(); i++) {
        rowBuilder.set(i, freq.get(i));
      }
      builder.addRow(rowBuilder);
    }
    return builder.build();
  }

  @Override
  public DataFrame inverseTransform(DataFrame x) {
    DataSeriesCollection.Builder builder = new DataSeriesCollection.Builder(DoubleVector.TYPE);
    for (DataFrameRow row : x) {
      if (row.getType() != ComplexVector.TYPE) {
        throw new IllegalArgumentException();
      }
      ComplexMatrix time = row.asMatrix().asComplexMatrix();
      DoubleMatrix freq = ifft(time).asDoubleMatrix(); // Let's ignore the tiny imaginary part :)
      DoubleVector.Builder rowBuilder = new DoubleVector.Builder(0, freq.size());
      for (int i = 0; i < freq.size(); i++) {
        rowBuilder.set(i, freq.get(i));
      }
      builder.addRow(rowBuilder);
    }
    return builder.build();
  }
}
