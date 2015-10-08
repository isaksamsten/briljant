package org.briljantframework.classification;

import static org.briljantframework.array.Arrays.argmax;
import static org.briljantframework.data.vector.Vectors.find;
import static org.briljantframework.evaluation.result.Sample.OUT;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.DoubleAdder;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.briljantframework.array.Arrays;
import org.briljantframework.array.BooleanArray;
import org.briljantframework.array.DoubleArray;
import org.briljantframework.array.IntArray;
import org.briljantframework.data.dataframe.DataFrame;
import org.briljantframework.data.vector.Vector;
import org.briljantframework.evaluation.measure.AbstractMeasure;
import org.briljantframework.evaluation.result.EvaluationContext;

/**
 * @author Isak Karlsson <isak-kar@dsv.su.se>
 */
public class Ensemble extends AbstractClassifier {

  private final List<? extends Classifier> members;
  private final BooleanArray oobIndicator;

  protected Ensemble(Vector classes, List<? extends Classifier> members, BooleanArray oobIndicator) {
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
  public BooleanArray getOobIndicator() {
    return oobIndicator;
  }

  public List<Classifier> getEnsembleMembers() {
    return Collections.unmodifiableList(members);
  }

  private void computeOOBCorrelation(EvaluationContext ctx) {
    if (!ctx.getPredictor().getCharacteristics().contains(Characteristics.ESTIMATOR)) {
      return;
    }

    Vector classes = getClasses();
    DataFrame x = ctx.getPartition().getTrainingData();
    Vector y = ctx.getPartition().getTrainingTarget();

    // Store the out-of-bag and in-bag probability estimates
    DoubleArray oobEstimates = Arrays.doubleArray(x.rows(), classes.size());
    DoubleArray inbEstimates = Arrays.doubleArray(x.rows(), classes.size());

    // Count the number of times each training sample have been included
    IntArray counts = Arrays.sum(1, oobIndicator.asInt());
    // TODO: was reduceRows (should be 1)

    // Compute the in-bag and out-of-bag estimates for all examples
    DoubleAdder oobAccuracy = new DoubleAdder();
    IntStream.range(0, x.rows()).parallel().forEach(i -> {
      int inbSize = members.size() - counts.get(i);
      int oobSize = counts.get(i);
      Vector record = x.loc().getRecord(i);
      for (int j = 0; j < members.size(); j++) {
        DoubleArray estimate = members.get(j).estimate(record);
        if (oobIndicator.get(i, j)) {
          oobEstimates.getRow(i).assign(estimate, (e, v) -> e + v / oobSize);
        } else {
          inbEstimates.getRow(i).assign(estimate, (e, v) -> e + v / inbSize);
        }
      }
      oobAccuracy.add(find(classes, y, i) == argmax(oobEstimates.getRow(i)) ? 1 : 0);
    });
    double avgOobAccuracy = oobAccuracy.sum() / x.rows();
    ctx.getOrDefault(OobAccuracy.class, OobAccuracy.Builder::new).add(OUT, avgOobAccuracy);

    DoubleAdder strengthA = new DoubleAdder();
    DoubleAdder strengthSquareA = new DoubleAdder();
    IntStream.range(0, oobEstimates.rows()).parallel().forEach(i -> {
      DoubleArray estimation = oobEstimates.getRow(i);
      int c = find(classes, y, i);
      double ma = estimation.get(c) - Learner.maxnot(estimation, c);
      strengthA.add(ma);
      strengthSquareA.add(ma * ma);
    });

    double strength = strengthA.doubleValue() / y.size();
    double strengthSquare = strengthSquareA.doubleValue() / y.size();
    double s2 = strength * strength;
    double variance = strengthSquare - s2;
    double std = 0;
    for (int j = 0; j < members.size(); j++) {
      Classifier member = members.get(j);
      AtomicInteger oobSizeA = new AtomicInteger(0);
      DoubleAdder p1A = new DoubleAdder();
      DoubleAdder p2A = new DoubleAdder();
      final int memberIndex = j;
      IntStream.range(0, x.rows()).parallel().forEach(i -> {
        if (oobIndicator.get(i, memberIndex)) {
          oobSizeA.getAndIncrement();
          int c = find(classes, y, i);
          DoubleArray memberEstimation = member.estimate(x.loc().getRecord(i));
          DoubleArray ibEstimation = inbEstimates.getRow(i);
          p1A.add(argmax(memberEstimation) == c ? 1 : 0);
          p2A.add(argmax(memberEstimation) == Learner.argmaxnot(ibEstimation, c) ? 1 : 0);
        }
      });
      double p1 = p1A.sum() / oobSizeA.get();
      double p2 = p2A.sum() / oobSizeA.get();
      std += Math.sqrt(p1 + p2 + Math.pow(p1 - p2, 2));
    }
    std = Math.pow(std / members.size(), 2);
    double correlation = variance / std;
    double errorBound = (correlation * (1 - s2)) / s2;
    ctx.getOrDefault(Strength.class, Strength.Builder::new).add(OUT, strength);
    ctx.getOrDefault(Correlation.class, Correlation.Builder::new).add(OUT, correlation);
    ctx.getOrDefault(Quality.class, Quality.Builder::new).add(OUT, correlation / s2);
    ctx.getOrDefault(ErrorBound.class, ErrorBound.Builder::new).add(OUT, errorBound);
  }

  private void computeMeanSquareError(EvaluationContext ctx) {
    DataFrame x = ctx.getPartition().getValidationData();
    Vector y = ctx.getPartition().getValidationTarget();
    Vector classes = getClasses();

    DoubleAdder meanVariance = new DoubleAdder();
    DoubleAdder meanSquareError = new DoubleAdder();
    DoubleAdder meanBias = new DoubleAdder();
    DoubleAdder baseAccuracy = new DoubleAdder();
    IntStream.range(0, x.rows()).parallel().forEach(i -> {
      Vector record = x.loc().getRecord(i);
      DoubleArray c = createTrueClassVector(y, classes, i);


      /* Stores the probability of the m:th member for the j:th class */
      int estimators = members.size();
      DoubleArray memberEstimates = Arrays.doubleArray(estimators, classes.size());
      for (int j = 0; j < estimators; j++) {
        Classifier member = members.get(j);
        memberEstimates.setRow(j, member.estimate(record));
      }

      /* Get the mean probability vector for the i:th example */
      DoubleArray meanEstimate = Arrays.mean(0, memberEstimates); // TODO: check
        double variance = 0, mse = 0, bias = 0, accuracy = 0;
        for (int j = 0; j < memberEstimates.rows(); j++) {
          DoubleArray r = memberEstimates.getRow(j);
          double meanDiff = 0;
          double trueDiff = 0;
          double meanTrueDiff = 0;
          for (int k = 0; k < r.size(); k++) {
            meanDiff += Math.pow(r.get(k) - meanEstimate.get(k), 2);
            trueDiff += Math.pow(r.get(k) - c.get(k), 2);
            meanTrueDiff += Math.pow(meanEstimate.get(k) - c.get(k), 2);
          }
          variance += meanDiff;
          mse += trueDiff;
          bias += meanTrueDiff;
          accuracy += argmax(r) == find(classes, y, i) ? 1 : 0;
        }
        meanVariance.add(variance / estimators);
        meanSquareError.add(mse / estimators);
        baseAccuracy.add(accuracy / estimators);
        meanBias.add(bias / estimators);
      });

    double avgVariance = meanVariance.doubleValue() / x.rows();
    double avgBias = meanBias.doubleValue() / x.rows();
    double avgMse = meanSquareError.doubleValue() / x.rows();
    double avgBaseAccuracy = baseAccuracy.doubleValue() / x.rows();

    ctx.getOrDefault(Variance.class, Variance.Builder::new).add(OUT, avgVariance);
    ctx.getOrDefault(Bias.class, Bias.Builder::new).add(OUT, avgBias);
    ctx.getOrDefault(MeanSquareError.class, MeanSquareError.Builder::new).add(OUT, avgMse);
    ctx.getOrDefault(BaseAccuracy.class, BaseAccuracy.Builder::new).add(OUT, avgBaseAccuracy);
  }

  private DoubleArray createTrueClassVector(Vector y, Vector classes, int i) {
    DoubleArray c = Arrays.doubleArray(classes.size());
    for (int j = 0; j < classes.size(); j++) {
      if (classes.loc().equals(j, y, i)) {
        c.set(j, 1);
      }
    }
    return c;
  }

  @Override
  public EnumSet<Characteristics> getCharacteristics() {
    return EnumSet.of(Characteristics.ESTIMATOR);
  }

  @Override
  public void evaluate(EvaluationContext ctx) {
    super.evaluate(ctx);
    computeMeanSquareError(ctx);
    computeOOBCorrelation(ctx);
  }

  @Override
  public DoubleArray estimate(Vector record) {
    List<DoubleArray> predictions =
        members.parallelStream().map(model -> model.estimate(record)).collect(Collectors.toList());

    int estimators = getEnsembleMembers().size();
    Vector classes = getClasses();
    DoubleArray m = Arrays.doubleArray(classes.size());
    for (DoubleArray prediction : predictions) {
      m.assign(prediction, (t, o) -> t + o / estimators);
    }
    return m;
  }

  /**
   * @author Isak Karlsson
   */
  public abstract static class Learner implements Classifier.Learner {

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

    protected Learner(int size) {
      this.size = size;
    }

    /**
     * Executes {@code callable} either sequential or in parallel depending on the number of
     * available cores.
     *
     * @param callables the callables
     * @param <T> the models produced
     * @return a list of produced models
     * @throws Exception if something goes wrong
     */
    protected static <T extends Classifier> List<T> execute(
        Collection<? extends Callable<T>> callables) throws Exception {
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

    private static int argmaxnot(DoubleArray m, int not) {
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

    private static double maxnot(DoubleArray m, int not) {
      double max = Double.NEGATIVE_INFINITY;
      for (int i = 0; i < m.size(); i++) {
        if (not != i && m.get(i) > max) {
          max = m.get(i);
        }
      }
      return max;
    }
  }

  /**
   * @author Isak Karlsson
   */
  public static class BaseAccuracy extends AbstractMeasure {

    protected BaseAccuracy(Builder builder) {
      super(builder);
    }

    public static final class Builder extends AbstractMeasure.Builder<BaseAccuracy> {

      @Override
      public BaseAccuracy build() {
        return new BaseAccuracy(this);
      }
    }

    @Override
    public String getName() {
      return "Base classifier accuracy";
    }
  }

  /**
   * @author Isak Karlsson
   */
  public static class MeanSquareError extends AbstractMeasure {

    protected MeanSquareError(Builder builder) {
      super(builder);
    }

    public static class Builder extends AbstractMeasure.Builder<MeanSquareError> {

      @Override
      public MeanSquareError build() {
        return new MeanSquareError(this);
      }
    }

    @Override
    public String getName() {
      return "Ensemble Mean Square Error";
    }
  }

  public static class Bias extends AbstractMeasure {

    protected Bias(Builder builder) {
      super(builder);
    }

    public static class Builder extends AbstractMeasure.Builder<Bias> {

      @Override
      public Bias build() {
        return new Bias(this);
      }
    }

    @Override
    public String getName() {
      return "Ensemble Bias";
    }
  }

  /**
   * @author Isak Karlsson
   */
  public static class Variance extends AbstractMeasure {

    protected Variance(Builder builder) {
      super(builder);
    }

    public static class Builder extends AbstractMeasure.Builder<Variance> {

      @Override
      public Variance build() {
        return new Variance(this);
      }
    }

    @Override
    public String getName() {
      return "Ensemble Variance";
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

    public static class Builder extends AbstractMeasure.Builder<OobAccuracy> {

      @Override
      public OobAccuracy build() {
        return new OobAccuracy(this);
      }
    }

    @Override
    public String getName() {
      return "OOB Accuracy";
    }
  }

  /**
   * @author Isak Karlsson
   */
  public static class Correlation extends AbstractMeasure {

    protected Correlation(Builder builder) {
      super(builder);
    }

    public static class Builder extends AbstractMeasure.Builder<Correlation> {

      @Override
      public Correlation build() {
        return new Correlation(this);
      }
    }

    @Override
    public String getName() {
      return "Correlation";
    }
  }

  /**
   * @author Isak Karlsson
   */
  public static class Strength extends AbstractMeasure {

    protected Strength(Builder builder) {
      super(builder);
    }

    public static class Builder extends AbstractMeasure.Builder<Strength> {

      @Override
      public Strength build() {
        return new Strength(this);
      }
    }

    @Override
    public String getName() {
      return "Strength";
    }
  }

  /**
   * @author Isak Karlsson
   */
  public static class ErrorBound extends AbstractMeasure {

    protected ErrorBound(Builder builder) {
      super(builder);
    }

    public static class Builder extends AbstractMeasure.Builder<ErrorBound> {

      @Override
      public ErrorBound build() {
        return new ErrorBound(this);
      }
    }

    @Override
    public String getName() {
      return "Ensemble Error Bound";
    }
  }
}
