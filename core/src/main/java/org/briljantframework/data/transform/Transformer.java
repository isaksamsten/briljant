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

/**
 * Fit a Transformer to a dataset and return a transformation which can be used to transform other datasets using the
 * parameters of the fitted dataset. This can be particularly useful when a transformation must be fitted on a dataset
 * and applied on another. For example, in the case of normalizing training and testing data.
 * <p>
 * <p>
 * Created by Isak Karlsson on 12/08/14.
 */
@FunctionalInterface
public interface Transformer {

    /**
     * Fit a transformation to data frame
     *
     * @param dataFrame the dataset to use in the fit procedure
     * @return the transformation
     */
    Transformation fit(DataFrame dataFrame);

    /**
     * Fit and transform the data frame in a single operation
     *
     * @param dataFrame the data frame
     * @return the transformed data frame
     */
    default DataFrame fitTransform(DataFrame dataFrame) {
        return fit(dataFrame).transform(dataFrame);
    }
}
