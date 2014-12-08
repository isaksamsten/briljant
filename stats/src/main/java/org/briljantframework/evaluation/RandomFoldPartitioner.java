package org.briljantframework.evaluation;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Iterator;

import org.briljantframework.dataframe.DataFrame;
import org.briljantframework.vector.Vector;

/**
 * Creates a k-fold partitioner
 * <p>
 * Created by Isak Karlsson on 01/12/14.
 */
public class RandomFoldPartitioner implements Partitioner {

  private final int folds;

  public RandomFoldPartitioner(int folds) {
    this.folds = folds;
  }

  @Override
  public Iterable<Partition> partition(DataFrame x, Vector y) {
    return () -> new KFoldCrossValidationIterator(x, y, folds);
  }

  private static class KFoldCrossValidationIterator implements Iterator<Partition> {
    private final int folds, foldSize, rows;
    private final DataFrame x;
    private final Vector y;

    private int current = 0;

    public KFoldCrossValidationIterator(DataFrame x, Vector y, int folds) {
      checkArgument(x.rows() == y.rows(), "Data and target must be of equal size");
      checkArgument(folds > 1 && folds < x.rows());

      this.x = checkNotNull(x);
      this.y = checkNotNull(y);
      this.rows = this.x.rows();
      this.folds = folds;
      this.foldSize = this.rows / folds;
    }

    @Override
    public boolean hasNext() {
      return current < folds;
    }

    @Override
    public Partition next() {
      current += 1;
      DataFrame.Builder xTrainingBuilder = x.newBuilder();
      Vector.Builder yTrainingBuilder = y.newBuilder();
      DataFrame.Builder xValidationBuilder = x.newBuilder();
      Vector.Builder yValidationBuilder = y.newBuilder();

      int index = 0;
      int foldEnd = rows - foldSize * current;

      // Account for the case when rows % folds != 0
      int pad = 0;
      if (current == 1) {
        pad = rows - foldSize * folds;
      }

      // Part 1: this is a training part
      for (int i = 0; i < foldEnd - pad; i++) {
        for (int j = 0; j < x.columns(); j++) {
          xTrainingBuilder.add(j, x, index, j);
        }
        yTrainingBuilder.add(y, index);
        index += 1;
      }

      // Part 2: this is a validation part
      for (int i = foldEnd - pad; i < foldSize + foldEnd; i++) {
        for (int j = 0; j < x.columns(); j++) {
          xValidationBuilder.add(j, x, index, j);
        }
        yValidationBuilder.add(y, index);
        index += 1;
      }

      // Part 3: this is a training part
      for (int i = foldEnd + foldSize; i < rows; i++) {
        for (int j = 0; j < x.columns(); j++) {
          xTrainingBuilder.add(j, x, index, j);
        }
        yTrainingBuilder.add(y, index);
        index += 1;
      }

      assert index == rows;


      return new Partition(xTrainingBuilder.build(), xValidationBuilder.build(),
          yTrainingBuilder.build(), yValidationBuilder.build());
    }
  }


}