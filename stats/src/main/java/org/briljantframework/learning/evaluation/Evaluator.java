package org.briljantframework.learning.evaluation;/*
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

import org.briljantframework.data.DataFrame;
import org.briljantframework.data.column.Column;
import org.briljantframework.learning.Classifier;
import org.briljantframework.learning.SupervisedDataset;
import org.briljantframework.learning.evaluation.result.Result;

/**
 * An Evaluator is used to evaluate an algorithm on a particular dataset
 * <p>
 * TODO - evaluator should most probably take type-parameters
 * <p>
 * Created by Isak Karlsson on 20/08/14.
 */
public interface Evaluator<D extends DataFrame<?>, T extends Column> {

    /**
     * @param classifier classifier to use for classification
     * @param supervisedDataset
     */
    Result evaluate(Classifier<?, ? super D, ? super T> classifier, SupervisedDataset<? extends D, ? extends T> supervisedDataset);

}
