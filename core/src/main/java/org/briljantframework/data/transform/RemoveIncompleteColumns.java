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

package org.briljantframework.data.transform;

import org.briljantframework.data.DataFrame;
import org.briljantframework.data.Row;
import org.briljantframework.data.types.Type;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by Isak Karlsson on 18/08/14.
 */
public class RemoveIncompleteColumns<D extends DataFrame<?>> implements Transformer<D> {

    @Override
    public Transformation<D> fit(D dataset) {
        boolean[] hasMissing = new boolean[dataset.columns()];

        for (Row e : dataset) {
            for (int i = 0; i < e.size(); i++) {
                if (e.getValue(i).na()) {
                    hasMissing[i] = true;
                }
            }
        }

        return new DoRemoveIncompleteColumns<>(hasMissing);
    }

    private static class DoRemoveIncompleteColumns<C extends DataFrame<?>> implements Transformation<C> {

        private final boolean[] hasMissing;

        private DoRemoveIncompleteColumns(boolean[] hasMissing) {
            this.hasMissing = hasMissing;
        }

        @Override
        public C transform(C dataset, DataFrame.CopyTo<C> copyTo) {
            List<Type> presentTypes = IntStream.range(0, dataset.getTypes().size())
                    .filter(x -> !hasMissing[x])
                    .mapToObj(dataset::getType)
                    .collect(Collectors.toCollection(ArrayList::new));

            DataFrame.Builder<C> builder = copyTo.newBuilder(presentTypes);
            for (Row row : dataset) {
                for (int i = 0; i < dataset.columns(); i++) {
                    if (!hasMissing[i]) {
                        builder.add(row.getValue(i));
                    }
                }
            }

            return builder.create();
        }
    }
}
