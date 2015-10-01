/*
 * The MIT License (MIT)
 * 
 * Copyright (c) 2015 Isak Karlsson
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package org.briljantframework.classification.tune;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.briljantframework.classification.Classifier;
import org.briljantframework.data.dataframe.DataFrame;
import org.briljantframework.data.vector.Vector;
import org.briljantframework.evaluation.Validator;
import org.briljantframework.evaluation.result.Result;

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
  protected final ArrayList<ParameterUpdater<O>> updaters;

  /**
   * The Parameter names.
   */
  protected final List<String> parameterNames;
  private final Validator evaluator;
  private final Comparator<Configuration> comparator;

  /**
   * Instantiates a new Abstract optimizer.
   *
   * @param updaters the updaters
   * @param evaluator the evaluator
   * @param comparator the comparator
   */
  protected DefaultTuner(ArrayList<ParameterUpdater<O>> updaters, Validator evaluator,
      Comparator<Configuration> comparator) {
    this.updaters = updaters;
    this.evaluator = evaluator;
    this.comparator = comparator;
    parameterNames =
        updaters.stream().map(ParameterUpdater::getParameter).collect(Collectors.toList());
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
      ParameterUpdater<O> updater = updaters.get(n);
      while (updater.hasUpdate()) {
        Object value = updater.update(classifierBuilder);
        parameters[n] = value;
        optimizeParameters(classifierBuilder, x, y, results, parameters, n + 1);
      }
      updater.restore();
    } else {
      C classifier = classifierBuilder.build();
      Result result = evaluator.test(classifier, x, y);

      Map<String, Object> map = new HashMap<>();
      for (int i = 0; i < parameterNames.size(); i++) {
        map.put(parameterNames.get(i), parameters[i]);
      }
      results.add(new Configuration(classifier, result, map));
    }
  }

  @Override
  public Configurations tune(O toOptimize, DataFrame x, Vector y) {
    Configurations results = optimizeParameters(toOptimize, x, y);
    results.sort(comparator);
    return results;
  }
}
