package org.briljantframework.learning;

import org.briljantframework.Range;
import org.briljantframework.data.DataFrame;
import org.briljantframework.data.Datasets;
import org.briljantframework.data.column.CategoricColumn;
import org.briljantframework.data.column.Column;

/**
 * @param <D> the type parameter
 * @param <T> the type parameter
 */
public class SupervisedDataset<D extends DataFrame<?>, T extends Column> {
    private final D dataset;
    private final T target;

    private final DataFrame.CopyTo<? extends D> datasetCopyTo;
    private final Column.CopyTo<? extends T> columnCopyTo;

    /**
     * Instantiates a new Entry.
     *
     * @param dataFrame the dataset
     * @param target    the target
     */
    public SupervisedDataset(D dataFrame, T target,
                             DataFrame.CopyTo<? extends D> dataFrameCopyTo, Column.CopyTo<? extends T> columnCopyTo) {
        this.dataset = dataFrame;
        this.target = target;
        this.datasetCopyTo = dataFrameCopyTo;
        this.columnCopyTo = columnCopyTo;
    }

    public static <D extends DataFrame<?>, C extends CategoricColumn> SupervisedDataset<D, C> createClassificationInput(
            DataFrame dataFrame, DataFrame.CopyTo<D> datasetCopyTo, Column.CopyTo<C> columnCopyTo) {
        C target = Datasets.getColumnAs(dataFrame, dataFrame.columns() - 1, columnCopyTo);
        D data = Datasets.dropColumnsAs(dataFrame, Range.closed(0, dataFrame.columns() - 1), datasetCopyTo);
        return new SupervisedDataset<>(data, target, datasetCopyTo, columnCopyTo);
    }

    public DataFrame.CopyTo<? extends D> copyDataFrame() {
        return datasetCopyTo;
    }

    public Column.CopyTo<? extends T> copyTarget() {
        return columnCopyTo;
    }

    /**
     * The Dataset.
     *
     * @return the dataset
     */
    public D getDataFrame() {
        return dataset;
    }

    /**
     * The Target.
     *
     * @return the target
     */
    public T getTarget() {
        return target;
    }

}
