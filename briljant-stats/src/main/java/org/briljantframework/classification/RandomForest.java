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

import org.briljantframework.Bj;
import org.briljantframework.classification.tree.ClassSet;
import org.briljantframework.classification.tree.Example;
import org.briljantframework.classification.tree.RandomSplitter;
import org.briljantframework.classification.tree.Splitter;
import org.briljantframework.data.dataframe.DataFrame;
import org.briljantframework.array.BitArray;
import org.briljantframework.data.vector.Vector;
import org.briljantframework.data.vector.Vectors;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;

/**
 * @author Isak Karlsson
 */
public class RandomForest extends Ensemble {

  private Splitter splitter;

  public RandomForest(Splitter splitter, int size) {
    super(size);
    this.splitter = splitter;
  }

  public static Builder withSize(int size) {
    return new Builder().withSize(size);
  }

  @Override
  public DefaultEnsemblePredictor fit(DataFrame x, Vector y) {
    Vector classes = Vectors.unique(y);
    ClassSet classSet = new ClassSet(y, classes);
    List<FitTask> fitTasks = new ArrayList<>();
    BitArray oobIndicator = Bj.booleanArray(x.rows(), size());
    for (int i = 0; i < size(); i++) {
      fitTasks.add(new FitTask(classSet, x, y, splitter, classes, oobIndicator.getColumn(i)));
    }
    try {
      return new DefaultEnsemblePredictor(classes, execute(fitTasks), oobIndicator);
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  @Override
  public String toString() {
    return String.format("Random Classification Forest");
  }

  private static final class FitTask implements
                                     Callable<org.briljantframework.classification.Predictor> {

    private final ClassSet classSet;
    private final DataFrame x;
    private final Vector y;
    private final Splitter splitter;
    private final Vector classes;
    private final BitArray oobIndicator;

    private FitTask(ClassSet classSet, DataFrame x, Vector y, Splitter splitter, Vector classes,
                    BitArray oobIndicator) {
      this.classSet = classSet;
      this.x = x;
      this.y = y;
      this.splitter = splitter;
      this.classes = classes;
      this.oobIndicator = oobIndicator;
    }

    @Override
    public org.briljantframework.classification.Predictor call() throws Exception {
      Random random = new Random(Thread.currentThread().getId() * System.currentTimeMillis());
      ClassSet bootstrap = sample(classSet, random);
      return new DecisionTree(splitter, bootstrap, classes).fit(x, y);
    }

    public ClassSet sample(ClassSet classSet, Random random) {
      ClassSet inBag = new ClassSet(classSet.getDomain());
      int[] bootstrap = bootstrap(classSet, random);
      for (ClassSet.Sample sample : classSet.samples()) {
        ClassSet.Sample inSample = ClassSet.Sample.create(sample.getTarget());
        for (Example example : sample) {
          int id = example.getIndex();
          if (bootstrap[id] > 0) {
            inSample.add(example.updateWeight(bootstrap[id]));
          } else {
            oobIndicator.set(id, true);
          }
        }
        if (!inSample.isEmpty()) {
          inBag.add(inSample);
        }
      }
      return inBag;
    }

    private int[] bootstrap(ClassSet sample, Random random) {
      int[] bootstrap = new int[sample.size()];
      for (int i = 0; i < bootstrap.length; i++) {
        int idx = random.nextInt(bootstrap.length);
        bootstrap[idx]++;
      }

      return bootstrap;
    }
  }

  public static class Builder implements Classifier.Builder<RandomForest> {

    private RandomSplitter.Builder splitter = RandomSplitter.withMaximumFeatures(-1);
    private int size = 100;

    public Builder withSize(int size) {
      this.size = size;
      return this;
    }

    public Builder withMaximumFeatures(int size) {
      splitter.withMaximumFeatures(size);
      return this;
    }

    @Override
    public RandomForest build() {
      return new RandomForest(splitter.create(), size);
    }
  }
}
