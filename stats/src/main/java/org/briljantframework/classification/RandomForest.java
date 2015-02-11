package org.briljantframework.classification;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;

import org.briljantframework.classification.tree.ClassSet;
import org.briljantframework.classification.tree.RandomSplitter;
import org.briljantframework.classification.tree.Splitter;
import org.briljantframework.dataframe.DataFrame;
import org.briljantframework.vector.Vector;

/**
 * Created by Isak Karlsson on 19/11/14.
 */
public class RandomForest extends AbstractEnsemble {

  private Splitter splitter;

  public RandomForest(Splitter splitter, int size) {
    super(size);
    this.splitter = splitter;
  }

  public static Builder withSize(int size) {
    return new Builder().withSize(10);
  }

  @Override
  public AbstractEnsemble.Model fit(DataFrame x, Vector y) {
    ClassSet classSet = ClassSet.fromVector(y);
    List<FitTask> fitTasks = new ArrayList<>();
    for (int i = 0; i < size(); i++) {
      fitTasks.add(new FitTask(classSet, x, y, splitter));
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

  private static final class FitTask implements Callable<ClassifierModel> {

    private final ClassSet classSet;
    private final DataFrame x;
    private final Vector y;
    private final Splitter splitter;


    private FitTask(ClassSet classSet, DataFrame x, Vector y, Splitter splitter) {
      this.classSet = classSet;
      this.x = x;
      this.y = y;
      this.splitter = splitter;
    }

    @Override
    public ClassifierModel call() throws Exception {
      Random random = new Random(Thread.currentThread().getId() * System.currentTimeMillis());
      return new DecisionTree(splitter, sample(classSet, random)).fit(x, y);
    }

    public ClassSet sample(ClassSet classSet, Random random) {
      ClassSet inBag = ClassSet.create();
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
