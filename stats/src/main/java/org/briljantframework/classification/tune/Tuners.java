package org.briljantframework.classification.tune;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import org.briljantframework.classification.Classifier;
import org.briljantframework.dataframe.DataFrame;
import org.briljantframework.evaluation.Validators;
import org.briljantframework.vector.Vector;

/**
 * Created by isak on 01/10/14.
 */
public class Tuners {

  @SafeVarargs
  public static <C extends Classifier, O extends Classifier.Builder<? extends C>> Configurations crossValidation(
      O builder, DataFrame x, Vector y, Comparator<Configuration> comparator, int folds,
      Updater<O>... updaters) {
    checkArgument(updaters.length > 0, "Can't tune without updaters");
    checkArgument(folds > 1 && folds < x.rows(), "Invalid number of cross-validation folds");
    ArrayList<Updater<O>> updaterList = new ArrayList<>(updaters.length);
    Collections.addAll(updaterList, updaters);
    return new DefaultTuner<>(updaterList, Validators.crossValidation(folds),
        comparator).tune(builder, x, y);
  }
}
