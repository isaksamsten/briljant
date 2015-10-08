package org.briljantframework.classification;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Callable;

import org.briljantframework.array.Arrays;
import org.briljantframework.array.BooleanArray;
import org.briljantframework.array.DoubleArray;
import org.briljantframework.classification.tree.ClassSet;
import org.briljantframework.classification.tree.Example;
import org.briljantframework.data.dataframe.DataFrame;
import org.briljantframework.data.vector.Vector;
import org.briljantframework.data.vector.Vectors;
import org.briljantframework.distance.Distance;
import org.briljantframework.evaluation.measure.AbstractMeasure;
import org.briljantframework.evaluation.result.EvaluationContext;
import org.briljantframework.evaluation.result.Sample;

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

  private final DoubleArray lengthImportance;
  private final DoubleArray positionImportance;

  private RandomShapeletForest(Vector classes, DoubleArray apriori,
      List<? extends Classifier> members, DoubleArray lengthImportance,
      DoubleArray positionImportance, BooleanArray oobIndicator) {
    super(classes, members, oobIndicator);
    this.lengthImportance = lengthImportance;
    this.positionImportance = positionImportance;
  }

  public static Configurator withSize(int size) {
    return new Configurator(size);
  }

  public DoubleArray getLengthImportance() {
    return lengthImportance;
  }

  public DoubleArray getPositionImportance() {
    return positionImportance;
  }

  @Override
  public void evaluate(EvaluationContext ctx) {
    super.evaluate(ctx);
    ctx.getOrDefault(Depth.class, Depth.Builder::new).add(Sample.OUT, getAverageDepth());
  }

  public double getAverageDepth() {
    double depth = 0;
    for (Classifier classifier : getEnsembleMembers()) {
      if (classifier instanceof ShapeletTree) {
        int d = ((ShapeletTree) classifier).getDepth();
        depth += d;
      }
    }
    return depth / getEnsembleMembers().size();
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

  public static class Configurator implements Classifier.Configurator<Learner> {

    private final ShapeletTree.Configurator shapeletTree = new ShapeletTree.Configurator();
    private int size = 100;

    public Configurator(int size) {
      this.size = size;
    }

    public Configurator setMinimumSplitSize(double minSplitSize) {
      shapeletTree.setMinimumSplit(minSplitSize);
      return this;
    }

    public Configurator setLowerLength(double lower) {
      shapeletTree.setLowerLength(lower);
      return this;
    }

    public Configurator setUpperLength(double upper) {
      shapeletTree.setUpperLength(upper);
      return this;
    }

    public Configurator setMaximumShapelets(int maxShapelets) {
      shapeletTree.setMaximumShapelets(maxShapelets);
      return this;
    }

    public Configurator setDistance(Distance distance) {
      shapeletTree.setDistance(distance);
      return this;
    }

    public Configurator setSize(int size) {
      this.size = size;
      return this;
    }

    public Configurator setSampleMode(ShapeletTree.Learner.SampleMode sampleMode) {
      shapeletTree.setSampleMode(sampleMode);
      return this;
    }

    public Configurator setAssessment(ShapeletTree.Learner.Assessment assessment) {
      shapeletTree.setAssessment(assessment);
      return this;
    }

    @Override
    public Learner configure() {
      return new Learner(shapeletTree, size);
    }
  }

  public static class Learner extends Ensemble.Learner {

    private final ShapeletTree.Configurator configurator;

    private Learner(ShapeletTree.Configurator configurator, int size) {
      super(size);
      this.configurator = configurator;
    }

    @Override
    public RandomShapeletForest fit(DataFrame x, Vector y) {
      Vector classes = Vectors.unique(y);
      ClassSet classSet = new ClassSet(y, classes);
      List<FitTask> tasks = new ArrayList<>();
      BooleanArray oobIndicator = Arrays.booleanArray(x.rows(), size());
      for (int i = 0; i < size(); i++) {
        tasks.add(new FitTask(classSet, x, y, configurator, classes, oobIndicator.getColumn(i)));
      }

      try {
        List<ShapeletTree> models = Ensemble.Learner.execute(tasks);
        DoubleArray lenSum = Arrays.doubleArray(x.columns());
        DoubleArray posSum = Arrays.doubleArray(x.columns());
        for (ShapeletTree m : models) {
          lenSum.addi(m.getLengthImportance());
          posSum.addi(m.getPositionImportance());
        }

        lenSum.update(v -> v / size());
        posSum.update(v -> v / size());

        Map<Object, Integer> counts = Vectors.count(y);
        DoubleArray apriori = Arrays.doubleArray(classes.size());
        for (int i = 0; i < classes.size(); i++) {
          apriori.set(i, counts.get(classes.loc().get(Object.class, i)) / (double) y.size());
        }

        return new RandomShapeletForest(classes, apriori, models, lenSum, posSum, oobIndicator);
      } catch (Exception e) {
        e.printStackTrace();
        throw new RuntimeException(e);
      }

    }

    @Override
    public String toString() {
      return "Ensemble of Randomized Shapelet Trees";
    }

    private static final class FitTask implements Callable<ShapeletTree> {

      private final ClassSet classSet;
      private final DataFrame x;
      private final Vector y;
      private final Vector classes;
      private final ShapeletTree.Configurator configurator;
      private final BooleanArray oobIndicator;


      private FitTask(ClassSet classSet, DataFrame x, Vector y, ShapeletTree.Configurator configurator,
          Vector classes, BooleanArray oobIndicator) {
        this.classSet = classSet;
        this.x = x;
        this.y = y;
        this.classes = classes;
        this.configurator = configurator;
        this.oobIndicator = oobIndicator;
      }

      @Override
      public ShapeletTree call() throws Exception {
        Random random = new Random(Thread.currentThread().getId() * System.nanoTime());
        ClassSet sample = sample(classSet, random);
        double low = configurator.lowerLength;
        double high = configurator.upperLength;
        return new ShapeletTree.Learner(low, high, configurator, sample, classes).fit(x, y);
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

  }
}
