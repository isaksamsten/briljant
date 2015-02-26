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
import org.briljantframework.classification.tree.Example;
import org.briljantframework.dataframe.DataFrame;
import org.briljantframework.distance.Distance;
import org.briljantframework.distribution.Distribution;
import org.briljantframework.distribution.TriangleDistribution;
import org.briljantframework.evaluation.measure.AbstractMeasure;
import org.briljantframework.evaluation.result.EvaluationContext;
import org.briljantframework.evaluation.result.Sample;
import org.briljantframework.matrix.BitMatrix;
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
public class RandomShapeletForest extends Ensemble {

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
    BitMatrix oobIndicator = Matrices.newBitMatrix(x.rows(), size());
    for (int i = 0; i < size(); i++) {
      tasks.add(new FitTask(classSet, x, y, builder, classes, oobIndicator.getColumnView(i)));
    }

    try {
      List<ShapeletTree.Predictor> models = execute(tasks);
      DoubleMatrix lenSum = Matrices.newDoubleVector(x.columns());
      DoubleMatrix posSum = Matrices.newDoubleVector(x.columns());
      for (ShapeletTree.Predictor m : models) {
        lenSum.assign(m.getLengthImportance(), Double::sum);
        posSum.assign(m.getPositionImportance(), Double::sum);
      }

      lenSum.update(v -> v / size());
      posSum.update(v -> v / size());
      return new Predictor(classes, models, lenSum, posSum, oobIndicator);
    } catch (Exception e) {
      e.printStackTrace();
      throw new RuntimeException(e);
    }

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
    private final BitMatrix outbag;


    private FitTask(ClassSet classSet, DataFrame x, Vector y, ShapeletTree.Builder builder,
        Vector classes, BitMatrix outbag) {
      this.classSet = classSet;
      this.x = x;
      this.y = y;
      this.classes = classes;
      this.builder = builder;
      this.outbag = outbag;
    }

    @Override
    public ShapeletTree.Predictor call() throws Exception {
      Random random = new Random(Thread.currentThread().getId() * System.nanoTime());
      ClassSet sample = sample(classSet, random);
      Distribution lowerDist = new TriangleDistribution(random, 0, builder.lowerLength, 1);
      Distribution upperDist =
          new TriangleDistribution(random, builder.lowerLength, builder.upperLength, 1);
      double low = lowerDist.next();
      // builder.lowerLength + (builder.upperLength - builder.lowerLength) * random.nextDouble();
      double high = upperDist.next();
      // low + (builder.upperLength - low) * random.nextDouble();
      // builder.withLowerLength(low).withUpperLength(high);
      // System.out.println(low + " " + high);
      ShapeletTree tree = new ShapeletTree(low, high, builder, sample, classes);
      ShapeletTree.Predictor fit = tree.fit(x, y);
      return fit;
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
            outbag.set(id, true);
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

  public static class Predictor extends EnsemblePredictor {

    private final DoubleMatrix lengthImportance;
    private final DoubleMatrix positionImportance;

    public Predictor(Vector classes,
        List<? extends org.briljantframework.classification.Predictor> members,
        DoubleMatrix lengthImportance, DoubleMatrix positionImportance, BitMatrix outbag) {
      super(classes, members, outbag);
      this.lengthImportance = lengthImportance;
      this.positionImportance = positionImportance;
    }

    public DoubleMatrix getLengthImportance() {
      return lengthImportance;
    }

    public DoubleMatrix getPositionImportance() {
      return positionImportance;
    }

    @Override
    public void evaluation(EvaluationContext ctx) {
      super.evaluation(ctx);
      double depth = 0;
      double depthSquare = 0;
      for (org.briljantframework.classification.Predictor predictor : getPredictors()) {
        if (predictor instanceof ShapeletTree.Predictor) {
          int d = ((ShapeletTree.Predictor) predictor).getDepth();
          depth += d;
          depthSquare += d * d;
        }
      }
      double avg = depth / getPredictors().size();
      double d2 = depthSquare / getPredictors().size();
      System.out.println(d2 - avg * avg);
      ctx.getOrDefault(Depth.class, Depth.Builder::new).add(Sample.OUT, avg);
    }
  }

  public static class Depth extends AbstractMeasure {

    protected Depth(Builder builder) {
      super(builder);
    }

    @Override
    public String getName() {
      return "Depth";
    }

    public static class Builder extends AbstractMeasure.Builder<Depth> {

      @Override
      public Depth build() {
        return new Depth(this);
      }
    }
  }

  public static class Builder implements Classifier.Builder<RandomShapeletForest> {

    // private final RandomShapeletSplitter.Builder randomShapeletSplitter = RandomShapeletSplitter
    // .withDistance(new EarlyAbandonSlidingDistance(Euclidean.getInstance()));

    private final ShapeletTree.Builder shapeletTree = new ShapeletTree.Builder();
    private int size = 100;

    public Builder withLowerLength(double lower) {
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

    public Builder withUpperLength(double upper) {
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

    public Builder withSampleMode(ShapeletTree.SampleMode sampleMode) {
      shapeletTree.withMode(sampleMode);
      return this;
    }

    public Builder withAssessment(ShapeletTree.Assessment assessment) {
      shapeletTree.withAssessment(assessment);
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
