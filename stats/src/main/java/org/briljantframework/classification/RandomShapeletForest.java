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

package org.briljantframework.classification;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;

import org.briljantframework.classification.tree.ClassSet;
import org.briljantframework.dataframe.DataFrame;
import org.briljantframework.distance.Distance;
import org.briljantframework.matrix.DoubleMatrix;
import org.briljantframework.matrix.Matrices;
import org.briljantframework.vector.Vector;
import org.briljantframework.vector.Vectors;

/**
 * <h1>Publications</h1>
 * <ul>
 * <li>Karlsson, I., Bostrom, H., Papapetrou, P. Forests of Randomized Shapelet Trees In Proc. the
 * 3rd International Symposium on Learning and Data Sciences (SLDS), 2015</li>
 * </ul>
 *
 * @author Isak Karlsson
 */
public class RandomShapeletForest extends AbstractEnsemble {

  private final ShapeletTree.Builder builder;

  private RandomShapeletForest(ShapeletTree.Builder builder, int size) {
    super(size);
    this.builder = builder;
  }

  public static Builder withSize(int size) {
    return new Builder().withSize(size);
  }

  @Override
  public Predictor fit(DataFrame x, Vector y) {
    Vector classes = Vectors.unique(y);
    ClassSet classSet = new ClassSet(y, classes);
    List<FitTask> tasks = new ArrayList<>();
    for (int i = 0; i < size(); i++) {
      tasks.add(new FitTask(classSet, x, y, builder, classes));
    }

    List<ShapeletTree.Predictor> models;
    try {
      models = execute(tasks);
    } catch (Exception e) {
      e.printStackTrace();
      throw new RuntimeException(e);
    }

    DoubleMatrix lenSum = Matrices.newDoubleVector(x.columns());
    DoubleMatrix posSum = Matrices.newDoubleVector(x.columns());
    for (ShapeletTree.Predictor m : models) {
      lenSum.assign(m.getLengthImportance(), Double::sum);
      posSum.assign(m.getPositionImportance(), Double::sum);
    }

    return new Predictor(classes, models, lenSum.update(v -> v / size()), posSum.update(v -> v
        / size()));
  }

  @Override
  public String toString() {
    return "Ensemble of Randomized Shapelet Trees";
  }

  private static final class FitTask implements Callable<ShapeletTree.Predictor> {

    private final ClassSet classSet;
    private final DataFrame x;
    private final Vector y;
    private final Vector classes;
    private final ShapeletTree.Builder builder;


    private FitTask(ClassSet classSet, DataFrame x, Vector y, ShapeletTree.Builder builder,
        Vector classes) {
      this.classSet = classSet;
      this.x = x;
      this.y = y;
      this.classes = classes;
      this.builder = builder;
    }

    @Override
    public ShapeletTree.Predictor call() throws Exception {
      Random random = new Random(Thread.currentThread().getId() * System.currentTimeMillis());
      // double aggregateFraction = Utils.randDouble(0.3, 0.7);
      // System.out.println(aggregateFraction);
      // builder.withAggregateFraction(aggregateFraction);
      return new ShapeletTree(builder, sample(classSet, random), classes).fit(x, y);
    }

    public ClassSet sample(ClassSet classSet, Random random) {
      ClassSet inBag = new ClassSet(classSet.getDomain());
      for (ClassSet.Sample sample : classSet.samples()) {
        ClassSet.Sample inSample = ClassSet.Sample.create(sample.getTarget());
        int[] bootstrap = bootstrap(sample, random);
        for (int i = 0; i < bootstrap.length; i++) {
          if (bootstrap[i] > 0) {
            inSample.add(sample.get(i).updateWeight(bootstrap[i]));
          }
        }
        inBag.add(inSample);
      }
      return inBag;
    }

    private int[] bootstrap(ClassSet.Sample sample, Random random) {
      int[] bootstrap = new int[sample.size()];
      for (int i = 0; i < bootstrap.length; i++) {
        bootstrap[random.nextInt(bootstrap.length)]++;
      }

      return bootstrap;
    }
  }

  public static class Predictor extends AbstractEnsemble.AbstractEnsemblePredictor {

    private final DoubleMatrix lengthImportance;
    private final DoubleMatrix positionImportance;

    public Predictor(Vector classes,
        List<? extends org.briljantframework.classification.Predictor> models,
        DoubleMatrix lengthImportance, DoubleMatrix positionImportance) {
      super(classes, models);
      this.lengthImportance = lengthImportance;
      this.positionImportance = positionImportance;
    }

    public DoubleMatrix getLengthImportance() {
      return lengthImportance;
    }

    public DoubleMatrix getPositionImportance() {
      return positionImportance;
    }
  }

  public static class Builder implements Classifier.Builder<RandomShapeletForest> {

    // private final RandomShapeletSplitter.Builder randomShapeletSplitter = RandomShapeletSplitter
    // .withDistance(new EarlyAbandonSlidingDistance(Euclidean.getInstance()));

    private final ShapeletTree.Builder shapeletTree = new ShapeletTree.Builder();
    private int size = 100;

    public Builder withLowerLength(int lower) {
      shapeletTree.withLowerLength(lower);
      return this;
    }

    public Builder withSampleSize(int sampleSize) {
      // randomShapeletSplitter.withSampleSize(sampleSize);
      return this;
    }

    public Builder withInspectedShapelets(int maxShapelets) {
      shapeletTree.withInspectedShapelets(maxShapelets);
      return this;
    }

    public Builder withDistance(Distance distance) {
      shapeletTree.withDistance(distance);
      return this;
    }

    public Builder withUpperLength(int upper) {
      shapeletTree.withUpperLength(upper);
      return this;
    }

    public Builder withSize(int size) {
      this.size = size;
      return this;
    }

    public Builder withAlpha(double alpha) {
      shapeletTree.withAlpha(alpha);
      return this;
    }

    public Builder withMode(ShapeletTree.Mode mode) {
      shapeletTree.withMode(mode);
      return this;
    }

    @Override
    public RandomShapeletForest build() {
      return new RandomShapeletForest(shapeletTree, size);
    }

    public Builder withAggregateFraction(double v) {
      shapeletTree.withAggregateFraction(v);
      return this;
    }
  }
}
