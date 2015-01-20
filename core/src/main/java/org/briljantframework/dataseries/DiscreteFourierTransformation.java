package org.briljantframework.dataseries;

import static org.briljantframework.math.transform.DiscreteFourierTransform.fft;
import static org.briljantframework.math.transform.DiscreteFourierTransform.ifft;

import org.briljantframework.complex.Complex;
import org.briljantframework.dataframe.DataFrame;
import org.briljantframework.dataframe.DataFrameRow;
import org.briljantframework.dataframe.transform.InvertibleTransformation;
import org.briljantframework.matrix.ComplexMatrix;
import org.briljantframework.matrix.DoubleMatrix;
import org.briljantframework.vector.Check;
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
      Check.requireType(DoubleVector.TYPE, row);
      DoubleMatrix timeDomain = row.asMatrix().asDoubleMatrix();
      ComplexMatrix frequencyDomain = fft(timeDomain);
      ComplexVector.Builder rowBuilder = new ComplexVector.Builder(0, frequencyDomain.size());
      for (int i = 0; i < frequencyDomain.size(); i++) {
        rowBuilder.set(i, frequencyDomain.get(i));
      }
      builder.addRow(rowBuilder);
    }
    return builder.build();
  }

  @Override
  public DataFrame inverseTransform(DataFrame x) {
    DataSeriesCollection.Builder builder = new DataSeriesCollection.Builder(DoubleVector.TYPE);
    for (DataFrameRow row : x) {
      Check.requireType(ComplexVector.TYPE, row);
      ComplexMatrix timeDomain = row.asMatrix().asComplexMatrix();
      DoubleMatrix frequencyDomain = ifft(timeDomain).asDoubleMatrix(); // Let's ignore the tiny imaginary part :)
      DoubleVector.Builder rowBuilder = new DoubleVector.Builder(0, frequencyDomain.size());
      for (int i = 0; i < frequencyDomain.size(); i++) {
        rowBuilder.set(i, frequencyDomain.get(i));
      }
      builder.addRow(rowBuilder);
    }
    return builder.build();
  }
}
