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

package org.briljantframework.learning.evaluation.tune;

import org.briljantframework.data.DataFrame;
import org.briljantframework.data.column.Column;
import org.briljantframework.learning.Classifier;
import org.briljantframework.learning.SupervisedDataset;
import org.briljantframework.learning.evaluation.Evaluator;
import org.briljantframework.learning.evaluation.result.Result;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Isak Karlsson on 25/09/14.
 *
 * @param <D> the type parameter
 * @param <T> the type parameter
 * @param <C> the type parameter
 * @param <O> the type parameter
 */
public class DefaultTuner<D extends DataFrame<?>, T extends Column, C extends Classifier<?, ? super D, ? super T>, O extends Classifier.Builder<? extends C>> implements Tuner<D, T, C, O> {

    /**
     * The Updaters.
     */
    protected final ArrayList<Updater<O>> updaters;

    /**
     * The Parameter names.
     */
    protected final List<String> parameterNames;
    private final Evaluator<D, T> evaluator;
    private final Comparator<Configuration<C>> comparator;

    /**
     * Instantiates a new Abstract optimizer.
     *
     * @param updaters   the updaters
     * @param evaluator  the evaluator
     * @param comparator the comparator
     */
    protected DefaultTuner(ArrayList<Updater<O>> updaters, Evaluator<D, T> evaluator, Comparator<Configuration<C>> comparator) {
        this.updaters = updaters;
        this.evaluator = evaluator;
        this.comparator = comparator;
        parameterNames = updaters.stream().map(Updater::getParameter).collect(Collectors.toList());
    }

    /**
     * Optimize parameters.
     *
     * @param builder the builder
     * @return the list
     */
    protected Configurations<C> optimizeParameters(O builder, SupervisedDataset<? extends D, ? extends T> supervisedDataset) {
        List<Configuration<C>> configurations = new ArrayList<>();
        optimizeParameters(builder, supervisedDataset, configurations, new Object[updaters.size()], 0);
        return Configurations.create(configurations, evaluator);
    }

    /**
     * Cartesian void.
     *
     * @param classifierBuilder the to update
     * @param results           the results
     * @param parameters        the parameters
     * @param n                 the n
     */
    private void optimizeParameters(O classifierBuilder, SupervisedDataset<? extends D, ? extends T> supervisedDataset, List<Configuration<C>> results, Object[] parameters, int n) {
        if (n != updaters.size()) {
            Updater<O> updater = updaters.get(n);
            while (updater.hasUpdate()) {
                Object value = updater.update(classifierBuilder);
                parameters[n] = value;
                optimizeParameters(classifierBuilder, supervisedDataset, results, parameters, n + 1);
            }
            updater.restore();
        } else {
            C classifier = classifierBuilder.create();
            Result result = evaluator.evaluate(classifier, supervisedDataset);

            Map<String, Object> map = new HashMap<>();
            for (int i = 0; i < parameterNames.size(); i++) {
                map.put(parameterNames.get(i), parameters[i]);
            }
            results.add(Configuration.create(classifier, result, map));
        }
    }

    @Override
    public Configurations<C> tune(O toOptimize, SupervisedDataset<? extends D, ? extends T> supervisedDataset) {
        Configurations<C> results = optimizeParameters(toOptimize, supervisedDataset);
        results.sort(comparator);
        return results;
    }
}
