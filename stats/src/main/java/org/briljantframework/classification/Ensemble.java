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

import static org.briljantframework.evaluation.result.Sample.OUT;
import static org.briljantframework.matrix.Matrices.*;
import static org.briljantframework.vector.Vectors.find;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.briljantframework.dataframe.DataFrame;
import org.briljantframework.dataframe.Record;
import org.briljantframework.evaluation.measure.AbstractMeasure;
import org.briljantframework.evaluation.result.EvaluationContext;
import org.briljantframework.matrix.*;
import org.briljantframework.vector.Value;
import org.briljantframework.vector.Vector;

import com.carrotsearch.hppc.ObjectDoubleMap;
import com.carrotsearch.hppc.ObjectDoubleOpenHashMap;
import com.google.common.util.concurrent.AtomicDouble;

/**
 * @author Isak Karlsson
 */
public abstract class Ensemble implements Classifier {

  private final static ThreadPoolExecutor THREAD_POOL;
  private final static int CORES;

  static {
    CORES = Runtime.getRuntime().availableProcessors();
    if (CORES <= 1) {
      THREAD_POOL = null;
    } else {
      THREAD_POOL = (ThreadPoolExecutor) Executors.newFixedThreadPool(CORES, r -> {
        Thread thread = new Thread(r);
        thread.setDaemon(true);
        return thread;
      });
    }
  }

  protected final int size;

  protected Ensemble(int size) {
    this.size = size;
  }

  /**
   * Executes {@code callable} either sequential or in parallel depending on the number of available
   * cores.
   *
   * @param callables the callables
   * @param <T> the models produced
   * @return a list of produced models
   * @throws Exception if something goes wrong
   */
  protected static <T extends Predictor> List<T> execute(Collection<? extends Callable<T>> callables)
      throws Exception {
    List<T> models = new ArrayList<>();
    if (THREAD_POOL != null && THREAD_POOL.getActiveCount() < CORES) {
      for (Future<T> future : THREAD_POOL.invokeAll(callables)) {
        models.add(future.get());
      }
    } else {
      for (Callable<T> callable : callables) {
        models.add(callable.call());
      }
    }
    return models;
  }

  /**
   * Get the number of members in the ensemble
   *
   * @return the size of the ensemble
   */
  public int size() {
    return size;
  }

  public static class EnsemblePredictor extends AbstractPredictor implements
      org.briljantframework.classification.EnsemblePredictor {

    private final List<? extends Predictor> members;
    private final BitMatrix oobIndicator;

    public EnsemblePredictor(Vector classes, List<? extends Predictor> members,
        BitMatrix oobIndicator) {
      super(classes);
      this.members = members;
      this.oobIndicator = oobIndicator;
    }

    /**
     * Shape = {@code [no training samples, no members]}, if element e<sup>i,j</sup> is {@code true}
     * the i:th training sample is out of the j:th members training sample.
     * 
     * @return the out of bag indicator matrix
     */
    public BitMatrix getOobIndicator() {
      return oobIndicator;
    }

    @Override
    public void evaluation(EvaluationContext ctx) {
      super.evaluation(ctx);
      meanSquareErrorDecomposition(ctx);
      expectedCorrelation(ctx);
    }

    private void expectedCorrelation(EvaluationContext ctx) {
      // if (ctx != null) {
      // System.out.println("Dont estimate strength");
      // return;
      // }
      if (!ctx.getPredictor().getCharacteristics().contains(Characteristics.ESTIMATOR)) {
        return;
      }

      double oobAccuracy = 0;
      Vector classes = getClasses();
      DataFrame x = ctx.getPartition().getTrainingData();
      Vector y = ctx.getPartition().getTrainingTarget();

      // Store the out-of-bag and in-bag probability estimates
      DoubleMatrix oobEstimates = newDoubleMatrix(x.rows(), classes.size());
      DoubleMatrix inbEstimates = newDoubleMatrix(x.rows(), classes.size());

      // Count the number of times each training sample have been included
      IntMatrix counts = oobIndicator.asIntMatrix().reduceRows(Matrices::sum);

      for (int i = 0; i < x.rows(); i++) {
        int inbSize = members.size() - counts.get(i);
        int oobSize = counts.get(i);
        Record record = x.getRecord(i);
        for (int j = 0; j < members.size(); j++) {
          DoubleMatrix estimate = members.get(j).estimate(record);
          if (oobIndicator.get(i, j)) {
            oobEstimates.getRowView(i).assign(estimate, (e, v) -> e + v / oobSize);
          } else {
            inbEstimates.getRowView(i).assign(estimate, (e, v) -> e + v / inbSize);
          }
        }
        oobAccuracy += find(classes, y, i) == argmax(oobEstimates.getRowView(i)) ? 1 : 0;
      }
      oobAccuracy /= x.rows();
      ctx.getOrDefault(OobAccuracy.class, OobAccuracy.Builder::new).add(OUT, oobAccuracy);


      double strength = 0, strengthSquare = 0;
      for (int i = 0; i < oobEstimates.rows(); i++) {
        DoubleMatrix estimation = oobEstimates.getRowView(i);
        int c = find(classes, y, i);
        double ma = estimation.get(c) - maxnot(estimation, c);
        strength += ma;
        strengthSquare += ma * ma;
      }
      strength /= y.size();
      strengthSquare /= y.size();
      double s2 = strength * strength;
      double variance = strengthSquare - s2;
      double std = 0;
      for (int j = 0; j < members.size(); j++) {
        Predictor member = members.get(j);
        double p1 = 0, p2 = 0;
        int oobSize = 0;
        for (int i = 0; i < x.rows(); i++) {
          if (oobIndicator.get(i, j)) {
            oobSize++;
            int c = find(classes, y, i);
            DoubleMatrix memberEstimation = member.estimate(x.getRecord(i));
            DoubleMatrix ibEstimation = inbEstimates.getRowView(i);
            p1 += argmax(memberEstimation) == c ? 1 : 0;
            p2 += argmax(memberEstimation) == argmaxnot(ibEstimation, c) ? 1 : 0;
          }
        }
        p1 /= oobSize;
        p2 /= oobSize;
        std += Math.sqrt(p1 + p2 + Math.pow(p1 - p2, 2));
      }

      std = Math.pow(std / members.size(), 0.5);
      double correlation = variance / std;
      double errorBound = (correlation * (1 - s2)) / s2;
      ctx.getOrDefault(Strength.class, Strength.Builder::new).add(OUT, strength);
      ctx.getOrDefault(Correlation.class, Correlation.Builder::new).add(OUT, correlation);
      ctx.getOrDefault(Quality.class, Quality.Builder::new).add(OUT, correlation / s2);
      ctx.getOrDefault(ErrorBound.class, ErrorBound.Builder::new).add(OUT, errorBound);
    }

    public int argmaxnot(DoubleMatrix m, int not) {
      double max = Double.NEGATIVE_INFINITY;
      int argMax = -1;
      for (int i = 0; i < m.size(); i++) {
        if (not != i && m.get(i) > max) {
          argMax = i;
          max = m.get(i);
        }
      }
      return argMax;
    }

    private double maxnot(DoubleMatrix m, int not) {
      double max = Double.NEGATIVE_INFINITY;
      for (int i = 0; i < m.size(); i++) {
        if (not != i && m.get(i) > max) {
          max = m.get(i);
        }
      }
      return max;
    }

    private void meanSquareErrorDecomposition(EvaluationContext ctx) {
      DataFrame x = ctx.getPartition().getValidationData();
      Vector y = ctx.getPartition().getValidationTarget();
      Vector classes = getClasses();

      DoubleMatrix prob = newDoubleMatrix(members.size(), classes.size());

      AtomicDouble meanVarA = new AtomicDouble(0);
      AtomicDouble meanBiasA = new AtomicDouble(0);
      AtomicDouble meanAccuracyA = new AtomicDouble(0);

      long start = System.nanoTime();
      IntStream.range(0, x.rows()).parallel().forEach(i -> {
        Record record = x.getRecord(i);
        DoubleMatrix c = newDoubleVector(classes.size());
        for (int j = 0; j < classes.size(); j++) {
          if (classes.equals(j, y, i)) {
            c.set(j, 1);
          }
        }
        for (int j = 0; j < members.size(); j++) {
          Predictor member = members.get(j);
          prob.setRow(j, member.estimate(record));
        }

        DoubleMatrix mean = mean(prob, Axis.COLUMN);
        double var = 0;
        double bias = 0;
        double accuracy = 0;
        for (int j = 0; j < prob.rows(); j++) {
          DoubleMatrix r = prob.getRowView(j);
          double meanDiff = 0, trueDiff = 0;
          for (int k = 0; k < r.size(); k++) {
            meanDiff += Math.pow(r.get(k) - mean.get(k), 2);
            trueDiff += Math.pow(r.get(k) - c.get(k), 2);
          }
          var += meanDiff;
          bias += trueDiff;
          accuracy += argmax(r) == find(classes, y, i) ? 1 : 0;
        }
        meanVarA.getAndAdd(var / members.size());
        meanBiasA.getAndAdd(bias / members.size());
        meanAccuracyA.getAndAdd(accuracy / members.size());
      });

      // for (int i = 0; i < x.rows(); i++) {
      // Record record = x.getRecord(i);
      // DoubleMatrix c = newDoubleVector(classes.size());
      // for (int j = 0; j < classes.size(); j++) {
      // if (classes.equals(j, y, i)) {
      // c.set(j, 1);
      // }
      // }
      // for (int j = 0; j < members.size(); j++) {
      // Predictor member = members.get(j);
      // prob.setRow(j, member.estimate(record));
      // }
      //
      // DoubleMatrix mean = mean(prob, Axis.COLUMN);
      // double var = 0;
      // double bias = 0;
      // double accuracy = 0;
      // for (int j = 0; j < prob.rows(); j++) {
      // DoubleMatrix r = prob.getRowView(j);
      // double meanDiff = 0, trueDiff = 0;
      // for (int k = 0; k < r.size(); k++) {
      // meanDiff += Math.pow(r.get(k) - mean.get(k), 2);
      // trueDiff += Math.pow(r.get(k) - c.get(k), 2);
      // }
      // var += meanDiff;
      // bias += trueDiff;
      // accuracy += argmax(r) == find(classes, y, i) ? 1 : 0;
      // }
      // meanVar += var / members.size();
      // meanBias += bias / members.size();
      // baseAccuracy += accuracy / members.size();
      // }
      System.out.println((System.nanoTime() - start) / 1e6);
      ctx.getOrDefault(Variance.class, Variance.Builder::new).add(OUT, meanVarA.get() / x.rows());
      ctx.getOrDefault(Bias.class, Bias.Builder::new).add(OUT, meanBiasA.get() / x.rows());
      ctx.getOrDefault(BaseAccuracy.class, BaseAccuracy.Builder::new).add(OUT,
          meanAccuracyA.get() / x.rows());
    }

    @Override
    public EnumSet<Characteristics> getCharacteristics() {
      return EnumSet.of(Characteristics.ESTIMATOR);
    }

    @Override
    public List<Predictor> getPredictors() {
      return Collections.unmodifiableList(members);
    }

    @Override
    public DoubleMatrix estimate(Vector row) {
      List<Value> predictions =
          members.parallelStream().map(model -> model.predict(row)).collect(Collectors.toList());
      ObjectDoubleMap<String> votes = new ObjectDoubleOpenHashMap<>();
      for (Value prediction : predictions) {
        votes.putOrAdd(prediction.getAsString(), 1, 1);
      }

      int estimators = getPredictors().size();
      Vector classes = getClasses();
      DoubleMatrix m = Matrices.newDoubleVector(classes.size());
      for (int i = 0; i < classes.size(); i++) {
        m.set(i, votes.getOrDefault(classes.getAsString(i), 0) / estimators);
      }
      return m;
    }
  }

  public static class Quality extends AbstractMeasure {

    protected Quality(Builder builder) {
      super(builder);
    }

    @Override
    public String getName() {
      return "Quality (c/s^2)";
    }

    public static class Builder extends AbstractMeasure.Builder<Quality> {

      @Override
      public Quality build() {
        return new Quality(this);
      }
    }
  }

  /**
   * @author Isak Karlsson
   */
  public static class OobAccuracy extends AbstractMeasure {

    protected OobAccuracy(Builder builder) {
      super(builder);
    }

    @Override
    public String getName() {
      return "OOB Accuracy";
    }

    public static class Builder extends AbstractMeasure.Builder<OobAccuracy> {

      @Override
      public OobAccuracy build() {
        return new OobAccuracy(this);
      }
    }
  }


  /**
   * @author Isak Karlsson
   */
  public static class Correlation extends AbstractMeasure {

    protected Correlation(Builder builder) {
      super(builder);
    }

    @Override
    public String getName() {
      return "Correlation";
    }

    public static class Builder extends AbstractMeasure.Builder<Correlation> {

      @Override
      public Correlation build() {
        return new Correlation(this);
      }
    }
  }

  /**
   * @author Isak Karlsson
   */
  public static class Strength extends AbstractMeasure {

    protected Strength(Builder builder) {
      super(builder);
    }

    @Override
    public String getName() {
      return "Strength";
    }

    public static class Builder extends AbstractMeasure.Builder<Strength> {

      @Override
      public Strength build() {
        return new Strength(this);
      }
    }
  }

  /**
   * @author Isak Karlsson
   */
  public static class ErrorBound extends AbstractMeasure {

    protected ErrorBound(Builder builder) {
      super(builder);
    }

    @Override
    public String getName() {
      return "Ensemble Error Bound";
    }

    public static class Builder extends AbstractMeasure.Builder<ErrorBound> {

      @Override
      public ErrorBound build() {
        return new ErrorBound(this);
      }
    }
  }

  /**
   * @author Isak Karlsson
   */
  public static class Variance extends AbstractMeasure {
    protected Variance(Builder builder) {
      super(builder);
    }

    @Override
    public String getName() {
      return "Ensemble Variance";
    }

    public static class Builder extends AbstractMeasure.Builder<Variance> {
      @Override
      public Variance build() {
        return new Variance(this);
      }
    }
  }

  /**
   * @author Isak Karlsson
   */
  public static class Bias extends AbstractMeasure {

    protected Bias(Builder builder) {
      super(builder);
    }

    @Override
    public String getName() {
      return "Ensemble Bias";
    }

    public static class Builder extends AbstractMeasure.Builder<Bias> {

      @Override
      public Bias build() {
        return new Bias(this);
      }
    }
  }

  /**
   * @author Isak Karlsson
   */
  public static class BaseAccuracy extends AbstractMeasure {

    protected BaseAccuracy(Builder builder) {
      super(builder);
    }

    @Override
    public String getName() {
      return "Base classifier accuracy";
    }

    public static final class Builder extends AbstractMeasure.Builder<BaseAccuracy> {

      @Override
      public BaseAccuracy build() {
        return new BaseAccuracy(this);
      }
    }
  }
}
