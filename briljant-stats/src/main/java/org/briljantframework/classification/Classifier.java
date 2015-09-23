/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Isak Karlsson
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.briljantframework.classification;

import org.briljantframework.data.dataframe.DataFrame;
import org.briljantframework.data.vector.Vector;

/**
 * <p>
 * A classifier is a function {@code f(X, y)} which produces a hypothesis {@code g = X -> y} that
 * as
 * accurately as possible model the true function {@code h} used to generate {@code X -> y}.
 * </p>
 *
 * <p>
 * The input {@code x} is usually denoted as the instances and the output {@code y} as the classes.
 * The input instances is represented as a {@link org.briljantframework.data.dataframe.DataFrame} which
 * consists of possibly heterogeneous vectors of values characterizing each instance.
 * </p>
 *
 * <p>
 * The output of the classifier is a {@link Predictor} (i.e., the {@code g}) which (hopefully)
 * approximates {@code h}. To estimate how well {@code g} approximates {@code h}, cross-validation
 * {@link org.briljantframework.evaluation.Validators#crossValidation(int)} can be
 * employed.
 * </p>
 *
 * A classifier is always atomic, i.e. does not have mutable state.
 *
 * <p>
 * Implementors are encouraged to also provide a convenient builder for the classifier, for example
 * in cases where the number of parameters are large. For example,
 * {@code RandomForest forest = RandomForest.withSize(100).withMaximumFeatures(2).build();} is more
 * clear than {@code RandomForest forest = new RandomForest(100, 2);}. If the builder implements
 * {@link org.briljantframework.classification.Classifier.Builder}, the
 * {@link org.briljantframework.classification.tune.Tuner} can be used to optimize parameter
 * configurations.
 *
 * <pre>{@code
 * Tuners.crossValidation(new RandomForest.Builder(), x, y,
 *   Configuration.measureComparator(Accuracy.class), 10,
 *   range(&quot;No. trees&quot;, RandomForest.Builder::withSize, 10, 1000, 10),
 *   range(&quot;No. features&quot;, RandomForest.Builder::withMaximumFeatures, 2, x.columns(),1));
 * }
 * </pre>
 *
 * </p>
 *
 * @author Isak Karlsson
 */
public interface Classifier {

  /**
   * Fit a hypothesis using the instances in {@code x} to the output classes in {@code y}
   *
   * @param x the instances
   * @param y the classes
   * @return a classification model
   */
  Predictor fit(DataFrame x, Vector y);

  /**
   * The interface Builder.
   */

  interface Builder<C extends Classifier> {

    /**
     * Create classifier.
     *
     * @return the classifier
     */
    C build();
  }

}
