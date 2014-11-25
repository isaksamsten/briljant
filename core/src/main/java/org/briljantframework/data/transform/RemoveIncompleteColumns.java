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


import org.briljantframework.dataframe.DataFrame;
import org.briljantframework.vector.Vector;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Isak Karlsson on 18/08/14.
 */
public class RemoveIncompleteColumns implements Transformer {

    @Override
    public Transformation fit(DataFrame dataset) {
        boolean[] hasNA = new boolean[dataset.columns()];
        for (int i = 0; i < dataset.columns(); i++) {
            if (dataset.getColumn(i).hasNA()) {
                hasNA[i] = true;
            }
        }


        return new DoRemoveIncompleteColumns(hasNA);
    }

    private static class DoRemoveIncompleteColumns implements Transformation {

        private final boolean[] hasNA;

        private DoRemoveIncompleteColumns(boolean[] hasNA) {
            this.hasNA = hasNA;
        }

        @Override
        public DataFrame transform(DataFrame dataset) {
            List<Vector> vectors = new ArrayList<>();
            for (int i = 0; i < dataset.columns(); i++) {
                if (!hasNA[i]) {
                    vectors.add(dataset.getColumn(0).newCopyBuilder().create());
                }
            }

            return dataset.newDataFrame(vectors);
        }
    }
}
