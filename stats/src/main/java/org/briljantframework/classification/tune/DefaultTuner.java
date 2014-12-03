/*
 * ADEB - machine learning pipelines made easy Copyright (C) 2014 Isak Karlsson
 * 
 * This program is free software; you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation; either version 2 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with this program; if
 * not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301 USA.
 */

package org.briljantframework.classification.tune;

import java.util.*;
import java.util.stream.Collectors;

import org.briljantframework.classification.Classifier;
import org.briljantframework.dataframe.DataFrame;
import org.briljantframework.evaluation.Evaluator;
import org.briljantframework.evaluation.result.Result;
import org.briljantframework.vector.Vector;

/**
 * Created by Isak Karlsson on 25/09/14.
 *
 * @param <C> the type parameter
 * @param <O> the type parameter
 */
public class DefaultTuner<C extends Classifier, O extends Classifier.Builder<? extends C>>
    implements Tuner<C, O> {

  /**
   * The Updaters.
   */
  protected final ArrayList<Updater<O>> updaters;

  /**
   * The Parameter names.
   */
  protected final List<String> parameterNames;
  private final Evaluator evaluator;
  private final Comparator<Configuration> comparator;

  /**
   * Instantiates a new Abstract optimizer.
   *
   * @param updaters the updaters
   * @param evaluator the evaluator
   * @param comparator the comparator
   */
  protected DefaultTuner(ArrayList<Updater<O>> updaters, Evaluator evaluator,
      Comparator<Configuration> comparator) {
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
  protected Configurations optimizeParameters(O builder, DataFrame x, Vector y) {
    List<Configuration> configurations = new ArrayList<>();
    optimizeParameters(builder, x, y, configurations, new Object[updaters.size()], 0);
    return Configurations.create(configurations, evaluator);
  }

  /**
   * Performs an exhaustive search for the best parameter configuration
   *
   * @param classifierBuilder the to update
   * @param results the results
   * @param parameters the parameters
   * @param n the n
   */
  private void optimizeParameters(O classifierBuilder, DataFrame x, Vector y,
      List<Configuration> results, Object[] parameters, int n) {
    if (n != updaters.size()) {
      Updater<O> updater = updaters.get(n);
      while (updater.hasUpdate()) {
        Object value = updater.update(classifierBuilder);
        parameters[n] = value;
        optimizeParameters(classifierBuilder, x, y, results, parameters, n + 1);
      }
      updater.restore();
    } else {
      C classifier = classifierBuilder.build();
      Result result = evaluator.evaluate(classifier, x, y);

      Map<String, Object> map = new HashMap<>();
      for (int i = 0; i < parameterNames.size(); i++) {
        map.put(parameterNames.get(i), parameters[i]);
      }
      results.add(Configuration.create(classifier, result, map));
    }
  }

  @Override
  public Configurations tune(O toOptimize, DataFrame x, Vector y) {
    Configurations results = optimizeParameters(toOptimize, x, y);
    results.sort(comparator);
    return results;
  }
}
