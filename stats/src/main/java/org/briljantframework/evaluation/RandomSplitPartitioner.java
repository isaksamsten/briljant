package org.briljantframework.evaluation;

import org.briljantframework.dataframe.DataFrame;
import org.briljantframework.vector.Vector;

import com.google.common.collect.Iterators;

/**
 * Created by Isak Karlsson on 01/12/14.
 */
public class RandomSplitPartitioner implements Partitioner {

  private final double testFraction;

  public RandomSplitPartitioner(double testFraction) {
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
          xTrainingBuilder.add(j, x, i, j);
        }
        yTrainingBuilder.add(y, i);
      }

      DataFrame.Builder xValidationBuilder = x.newBuilder();
      Vector.Builder yValidationBuilder = y.newBuilder();
      for (int i = trainingSize; i < x.rows(); i++) {
        for (int j = 0; j < x.columns(); j++) {
          xValidationBuilder.add(j, x, i, j);
        }
        yValidationBuilder.add(y, i);
      }

      return Iterators.singletonIterator(new Partition(xTrainingBuilder.build(), xValidationBuilder
          .build(), yTrainingBuilder.build(), yValidationBuilder.build()));
    };
  }
}
