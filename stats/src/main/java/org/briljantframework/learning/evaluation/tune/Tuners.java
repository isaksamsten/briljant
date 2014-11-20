package org.briljantframework.learning.evaluation.tune;

import org.briljantframework.data.DataFrame;
import org.briljantframework.data.column.Column;
import org.briljantframework.learning.Classifier;
import org.briljantframework.learning.SupervisedDataset;
import org.briljantframework.learning.evaluation.CrossValidation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Created by isak on 01/10/14.
 */
public class Tuners {

    @SafeVarargs
    public static <D extends DataFrame<?>, T extends Column, C extends Classifier<?, ? super D, ? super T>, O extends Classifier.Builder<? extends C>> Configurations<C> crossValidation(
            O builder, SupervisedDataset<? extends D, ? extends T> supervisedDataset, Comparator<Configuration<C>> comparator, int folds, Updater<O>... updaters) {
        checkArgument(updaters.length > 0, "Can't tune without updaters");
        checkArgument(folds > 1 && folds < supervisedDataset.getDataFrame().rows(), "Invalid number of cross-validation folds");
        ArrayList<Updater<O>> updaterList = new ArrayList<>(updaters.length);
        Collections.addAll(updaterList, updaters);
        return new DefaultTuner<>(updaterList, new CrossValidation<>(folds), comparator).tune(builder, supervisedDataset);
    }
}
