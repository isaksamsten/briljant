package org.briljantframework.dataseries;

import org.briljantframework.Check;
import org.briljantframework.dataframe.DataFrame;
import org.briljantframework.dataframe.transform.InvertibleTransformation;
import org.briljantframework.matrix.ComplexArray;
import org.briljantframework.matrix.DoubleArray;
import org.briljantframework.vector.ComplexVector;
import org.briljantframework.vector.DoubleVector;
import org.briljantframework.vector.Vector;

import static org.briljantframework.math.transform.DiscreteFourierTransform.fft;
import static org.briljantframework.math.transform.DiscreteFourierTransform.ifft;

/**
 * @author Isak Karlsson
 */
public class DiscreteFourierTransformation implements InvertibleTransformation {

  /**
   * Asserts that each row has {@link org.briljantframework.vector.DoubleVector#TYPE}.
   *
   * @param x data frame to transform
   * @return a new data frame; each row has type
   * {@link org.briljantframework.vector.ComplexVector#TYPE}
   */
  @Override
  public DataFrame transform(DataFrame x) {
    DataSeriesCollection.Builder builder = new DataSeriesCollection.Builder(ComplexVector.TYPE);
    for (Vector row : x) {
      Check.type(row, DoubleVector.TYPE);
      DoubleArray timeDomain = row.toMatrix().asDouble();
      ComplexArray frequencyDomain = fft(timeDomain);
      ComplexVector.Builder rowBuilder = new ComplexVector.Builder(0, frequencyDomain.size());
      for (int i = 0; i < frequencyDomain.size(); i++) {
        rowBuilder.set(i, frequencyDomain.get(i));
      }
      builder.addRecord(rowBuilder);
    }
    return builder.build();
  }

  @Override
  public DataFrame inverseTransform(DataFrame x) {
    DataSeriesCollection.Builder builder = new DataSeriesCollection.Builder(DoubleVector.TYPE);
    for (Vector row : x) {
      Check.type(row, ComplexVector.TYPE);
      ComplexArray timeDomain = row.toMatrix().asComplex();
      DoubleArray frequencyDomain = ifft(timeDomain).asDouble();
      DoubleVector.Builder rowBuilder = new DoubleVector.Builder(0, frequencyDomain.size());
      for (int i = 0; i < frequencyDomain.size(); i++) {
        rowBuilder.set(i, frequencyDomain.get(i));
      }
      builder.addRecord(rowBuilder);
    }
    return builder.build();
  }
}
