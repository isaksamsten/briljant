package org.briljantframework.classification;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;

import org.briljantframework.classification.tree.Examples;
import org.briljantframework.classification.tree.RandomSplitter;
import org.briljantframework.dataframe.DataFrame;
import org.briljantframework.vector.Vector;

/**
 * Created by Isak Karlsson on 19/11/14.
 */
public class RandomForest extends AbstractEnsemble {

  private DecisionTree.Builder tree;

  public RandomForest() {
    this(withSize(100));
  }

  public RandomForest(int trees) {
    this(withSize(trees));
  }

  public RandomForest(Builder builder) {
    super(builder.size);
    this.tree = builder.tree;
  }

  public static Builder withSize(int size) {
    return new Builder().withSize(10);
  }

  @Override
  public AbstractEnsemble.Model fit(DataFrame x, Vector y) {
    Examples examples = Examples.fromVector(y);
    List<FitTask> fitTasks = new ArrayList<>();
    for (int i = 0; i < size(); i++) {
      fitTasks.add(new FitTask(examples, x, y, tree));
    }

    try {
      return new Model(execute(fitTasks));
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  @Override
  public String toString() {
    return String.format("Random Classification Forest");
  }

  private static final class FitTask implements Callable<Classifier.Model> {

    private final Examples examples;
    private final DataFrame x;
    private final Vector y;
    private final DecisionTree.Builder builder;


    private FitTask(Examples examples, DataFrame x, Vector y, DecisionTree.Builder builder) {
      this.examples = examples;
      this.x = x;
      this.y = y;
      this.builder = builder;
    }

    @Override
    public Classifier.Model call() throws Exception {
      Random random = new Random(Thread.currentThread().getId() * System.currentTimeMillis());
      return builder.create(sample(examples, random)).fit(x, y);
    }

    public Examples sample(Examples examples, Random random) {
      Examples inBag = Examples.create();
      for (Examples.Sample sample : examples.samples()) {
        Examples.Sample inSample = Examples.Sample.create(sample.getTarget());
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

    private int[] bootstrap(Examples.Sample sample, Random random) {
      int[] bootstrap = new int[sample.size()];
      for (int i = 0; i < bootstrap.length; i++) {
        bootstrap[random.nextInt(bootstrap.length)]++;
      }

      return bootstrap;
    }
  }

  public static class Builder implements Classifier.Builder<RandomForest> {

    private RandomSplitter.Builder splitter = RandomSplitter.withMaximumFeatures(-1);
    private DecisionTree.Builder tree = DecisionTree.withSplitter(splitter);
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
      return new RandomForest(this);
    }
  }
}
