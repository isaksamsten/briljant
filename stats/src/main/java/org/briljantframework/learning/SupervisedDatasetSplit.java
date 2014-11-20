package org.briljantframework.learning;

import com.google.common.base.Preconditions;
import org.briljantframework.data.DataFrame;
import org.briljantframework.data.Row;
import org.briljantframework.data.column.Column;
import org.briljantframework.data.values.Value;

import java.util.Iterator;

/**
 * Created by isak on 01/10/14.
 */
public class SupervisedDatasetSplit<D extends DataFrame<?>, T extends Column> {

    private final SupervisedDataset<D, T> trainingSet, validationSet;

    private SupervisedDatasetSplit(SupervisedDataset<D, T> trainingSet, SupervisedDataset<D, T> validationSet) {
        this.trainingSet = trainingSet;
        this.validationSet = validationSet;
    }

    /**
     * Create split.
     *
     * @param testFraction the test fraction
     * @return the split
     */
    public static <D extends DataFrame<?>, T extends Column> SupervisedDatasetSplit<D, T> withFraction(SupervisedDataset<? extends D, ? extends T> supervisedDataset, double testFraction) {
        DataFrame.CopyTo<? extends D> datasetCopyTo = supervisedDataset.copyDataFrame();
        Column.CopyTo<? extends T> columnCopyTo = supervisedDataset.copyTarget();
        D dataset = supervisedDataset.getDataFrame();
        T target = supervisedDataset.getTarget();

        Preconditions.checkArgument(testFraction > 0 && testFraction < 1);
        Preconditions.checkNotNull(dataset);

        // Safe int cast (rows() <= Integer.MAX_VALUE)
        int testSize = (int) Math.round(dataset.rows() * testFraction);
        if (testSize == dataset.rows()) {
            throw new IllegalArgumentException("Validation set size, equals the dataset size");
        }

        DataFrame.Builder<? extends D> datasetBuilder = datasetCopyTo.newBuilder(dataset.getTypes());
        Column.Builder<? extends T> columnBuilder = columnCopyTo.newBuilder(target.getType());

        Iterator<? extends Row> entries = dataset.iterator();
        Iterator<Value> values = target.iterator();
        for (int i = 0; i < testSize; i++) {
            datasetBuilder.addRow(entries.next());
            columnBuilder.add(values.next());
        }
        SupervisedDataset<D, T> validationSet = new SupervisedDataset<>(datasetBuilder.create(), columnBuilder.create(), datasetCopyTo, columnCopyTo);

        datasetBuilder = datasetCopyTo.newBuilder(dataset.getTypes());
        columnBuilder = columnCopyTo.newBuilder(target.getType());

        for (int i = testSize; i < dataset.rows(); i++) {
            datasetBuilder.addRow(entries.next());
            columnBuilder.add(values.next());
        }
        SupervisedDataset<D, T> trainingSet = new SupervisedDataset<>(datasetBuilder.create(), columnBuilder.create(), datasetCopyTo, columnCopyTo);
        return new SupervisedDatasetSplit<>(trainingSet, validationSet);
    }

    /**
     * Gets training set.
     *
     * @return the training set
     */
    public SupervisedDataset<D, T> getTrainingSet() {
        return trainingSet;
    }

    /**
     * Gets validation set.
     *
     * @return the validation set
     */
    public SupervisedDataset<D, T> getValidationSet() {
        return validationSet;
    }
}
