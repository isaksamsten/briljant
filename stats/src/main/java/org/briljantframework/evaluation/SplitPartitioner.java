package org.briljantframework.evaluation;

import com.google.common.collect.Iterators;

import org.briljantframework.dataframe.DataFrame;
import org.briljantframework.vector.Vector;

/**
 * The split partitioner simply partitions the input {@code DataFrame} (with {@code m rows}) and
 * {@code Vector} (of length {@code m}) into two parts (according to {@code testFraction} (in the
 * range {@code [0, 1]}).
 *
 * <p>The training partition is of size {@code m*(1-testFraction)} and the validation partition of
 * size {@code m*testFraction}
 *
 * @author Isak Karlsson
 */
public class SplitPartitioner implements Partitioner {

  private final double testFraction;

  public SplitPartitioner(double testFraction) {
    this.testFraction = testFraction;
  }

  @Override
  public Iterable<Partition> partition(DataFrame x, Vector y) {
    return () -> {
      int trainingSize = x.rows() - (int) Math.round(x.rows() * testFraction);

      DataFrame.Builder xTrainingBuilder = x.newBuilder();
      Vector.Builder yTrainingBuilder = y.newBuilder();
      for (int i = 0; i < trainingSize; i++) {
        for (int j = 0; j < x.columns(); j++) {
          xTrainingBuilder.set(i, j, x, i, j);
        }
        yTrainingBuilder.add(y, i);
      }

      DataFrame.Builder xValidationBuilder = x.newBuilder();
      Vector.Builder yValidationBuilder = y.newBuilder();
      int index = 0;
      for (int i = trainingSize; i < x.rows(); i++) {
        for (int j = 0; j < x.columns(); j++) {
          xValidationBuilder.set(index, j, x, i, j);
        }
        yValidationBuilder.add(y, i);
        index += 1;
      }

      return Iterators.singletonIterator(new Partition(xTrainingBuilder.build(), xValidationBuilder
          .build(), yTrainingBuilder.build(), yValidationBuilder.build()));
    };
  }
}
