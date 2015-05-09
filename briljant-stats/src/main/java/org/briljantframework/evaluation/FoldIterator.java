package org.briljantframework.evaluation;

import org.briljantframework.dataframe.DataFrame;
import org.briljantframework.vector.Vector;

import java.util.Iterator;
import java.util.NoSuchElementException;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Isak Karlsson
 */
class FoldIterator implements Iterator<Partition> {

  private final int folds, foldSize, rows;
  private final DataFrame x;
  private final Vector y;
  private final int reminder;

  private int current = 0;

  public FoldIterator(DataFrame x, Vector y, int folds) {
    checkArgument(x.rows() == y.size(), "Data and target must be of equal size.");
    checkArgument(folds > 1 && folds <= x.rows(), "Invalid fold count.");

    this.x = checkNotNull(x);
    this.y = checkNotNull(y);
    this.rows = this.x.rows();
    this.folds = folds;
    this.foldSize = this.rows / folds;
    this.reminder = this.rows % folds;
  }

  @Override
  public boolean hasNext() {
    return current < folds;
  }

  @Override
  public Partition next() {
    if (!hasNext()) {
      throw new NoSuchElementException();
    }

    current += 1;
    DataFrame.Builder xTrainingBuilder = x.newBuilder();
//    xTrainingBuilder.getColumnNames().putAll(x.getColumnNames());
    Vector.Builder yTrainingBuilder = y.newBuilder();
    DataFrame.Builder xValidationBuilder = x.newBuilder();
//    xValidationBuilder.getColumnNames().putAll(x.getColumnNames());
    Vector.Builder yValidationBuilder = y.newBuilder();

    int index = 0;
    int foldEnd = rows - foldSize * current;

    // Account for the case when rows % folds != 0
    // by adding an extra validation example to the
    // first `reminder` folds
    int pad = 0;
    if (current <= this.reminder) {
      pad = 1;
    }

    // Part 1: this is a training part add the first
    // foldSize * current examples as training examples
    int trainingEnd = foldEnd - pad;
    for (int i = 0; i < trainingEnd; i++) {
      for (int j = 0; j < x.columns(); j++) {
        xTrainingBuilder.set(i, j, x, index, j);
      }
      yTrainingBuilder.add(y, index);
      index += 1;
    }

    // Part 2: this is a validation part. Add the second
    // next foldSize * current examples until validation end
    int newIndex = 0;
    int validationEnd = foldEnd + foldSize;
    for (int i = trainingEnd; i < validationEnd; i++) {
      for (int j = 0; j < x.columns(); j++) {
        xValidationBuilder.set(newIndex, j, x, index, j);
      }
      yValidationBuilder.add(y, index);
      index += 1;
      newIndex += 1;
    }

    // Part 3: this is a training part
    newIndex = trainingEnd;
    for (int i = validationEnd; i < rows; i++) {
      for (int j = 0; j < x.columns(); j++) {
        xTrainingBuilder.set(newIndex, j, x, index, j);
      }
      yTrainingBuilder.add(y, index);
      index += 1;
      newIndex += 1;
    }

    assert index == rows;
    return new Partition(xTrainingBuilder.build(), xValidationBuilder.build(),
                         yTrainingBuilder.build(), yValidationBuilder.build());
  }
}
