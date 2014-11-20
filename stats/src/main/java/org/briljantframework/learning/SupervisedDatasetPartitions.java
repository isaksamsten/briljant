/*
 * ADEB - machine learning pipelines made easy
 * Copyright (C) 2014  Isak Karlsson
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package org.briljantframework.learning;

import org.briljantframework.data.DataFrame;
import org.briljantframework.data.Row;
import org.briljantframework.data.column.Column;
import org.briljantframework.data.values.Value;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by isak on 13/08/14.
 *
 * @param <D> the type parameter
 * @param <T> the type parameter
 */
public class SupervisedDatasetPartitions<D extends DataFrame<?>, T extends Column> {

    private final List<SupervisedDataset<? extends D, ? extends T>> partitions;
    private final Column.CopyTo<? extends T> columnCopyTo;
    private final DataFrame.CopyTo<? extends D> datasetCopyTo;


    public SupervisedDatasetPartitions(DataFrame.CopyTo<? extends D> datasetCopyTo, Column.CopyTo<? extends T> columnCopyTo, List<SupervisedDataset<? extends D, ? extends T>> partitions) {
        ensureCompatible(partitions);
        this.partitions = partitions;
        this.datasetCopyTo = datasetCopyTo;
        this.columnCopyTo = columnCopyTo;
    }

    /**
     * Create partitions.
     * <p>
     *
     * @param <D>   the type parameter
     * @param <T>   the type parameter
     * @param parts the parts
     * @return the partitions
     */
    public static <D extends DataFrame<?>, T extends Column> SupervisedDatasetPartitions<D, T> create(SupervisedDataset<? extends D, ? extends T> supervisedDataset, int parts) {
        DataFrame.CopyTo<? extends D> datasetCopyTo = supervisedDataset.copyDataFrame();
        Column.CopyTo<? extends T> columnCopyTo = supervisedDataset.copyTarget();
        D dataset = supervisedDataset.getDataFrame();
        T target = supervisedDataset.getTarget();

        List<SupervisedDataset<? extends D, ? extends T>> split = new ArrayList<>();
        List<DataFrame.Builder<? extends D>> datasetBuilders = new ArrayList<>();
        List<Column.Builder<? extends T>> targetBuilders = new ArrayList<>();

        // Create dataset and target builders
        for (int i = 0; i < parts; i++) {
            datasetBuilders.add(datasetCopyTo.newBuilder(dataset.getTypes()));
            targetBuilders.add(columnCopyTo.newBuilder(target.getType()));

        }

        // assign values and targets to one of the parts, iteratively
        Iterator<? extends Row> entries = dataset.iterator();
        Iterator<Value> values = target.iterator();

        for (int i = 0; i < dataset.rows(); i++) {
            int index = i % parts;
            datasetBuilders.get(index).addRow(entries.next());
            targetBuilders.get(index).add(values.next());
        }

        // create the splits
        for (int i = 0; i < parts; i++) {
            D d = datasetBuilders.get(i).create();
            T t = targetBuilders.get(i).create();
            split.add(new SupervisedDataset<>(d, t, datasetCopyTo, columnCopyTo));
        }

        return new SupervisedDatasetPartitions<>(datasetCopyTo, columnCopyTo, split);
    }

    private void ensureCompatible(List<SupervisedDataset<? extends D, ? extends T>> partitions) {
        Iterator<SupervisedDataset<? extends D, ? extends T>> it = partitions.iterator();
        SupervisedDataset<? extends D, ? extends T> supervisedDataset = it.next();
        while (it.hasNext()) {
            SupervisedDataset<? extends D, ? extends T> next = it.next();
            if (supervisedDataset.getDataFrame().columns() != next.getDataFrame().columns()) {
                throw new IllegalArgumentException("partitions not compatible (cannot be stacked)");
            }
        }
    }

    /**
     * Take and merge.
     *
     * @param args the args
     * @return the storage
     */
    public SupervisedDataset<D, T> takeAndMerge(int... args) {
        return take(args).merge();
    }

    /**
     * Merge storage.
     *
     * @return the storage
     */
    public SupervisedDataset<D, T> merge() {
        SupervisedDataset<? extends D, ? extends T> first = partitions.get(0);
        DataFrame.Builder<? extends D> datasetBuilder = datasetCopyTo.newBuilder(first.getDataFrame().getTypes());
        Column.Builder<? extends T> targetBuilder = columnCopyTo.newBuilder(first.getTarget().getType());

        for (SupervisedDataset<? extends D, ? extends T> part : partitions) {
            part.getDataFrame().forEach(datasetBuilder::addRow);
            part.getTarget().forEach(targetBuilder::add);
        }

        return new SupervisedDataset<>(datasetBuilder.create(), targetBuilder.create(), datasetCopyTo, columnCopyTo);
    }

    /**
     * Take partitions.
     *
     * @param args the args
     * @return the partitions
     */
    public SupervisedDatasetPartitions<D, T> take(int... args) {
        List<SupervisedDataset<? extends D, ? extends T>> newPartitions = new ArrayList<>();
        for (int i : args) {
            newPartitions.add(get(i));
        }

        return new SupervisedDatasetPartitions<>(datasetCopyTo, columnCopyTo, newPartitions);
    }

    /**
     * Get storage.
     *
     * @param part the part
     * @return the storage
     */
    public SupervisedDataset<? extends D, ? extends T> get(int part) {
        return partitions.get(part);
    }

    /**
     * Take partitions.
     *
     * @param from the from
     * @param to   the to
     * @return the partitions
     */
    public SupervisedDatasetPartitions<D, T> take(int from, int to) {
        return new SupervisedDatasetPartitions<>(datasetCopyTo, columnCopyTo, partitions.subList(from, to));
    }

    @Override
    public String toString() {
        return String.format("Partions(%d parts)", partitions.size());
    }

}
