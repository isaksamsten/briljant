package org.briljantframework.learning.tree;

import org.briljantframework.dataframe.DataFrame;
import org.briljantframework.learning.Classifier;
import org.briljantframework.learning.Model;
import org.briljantframework.learning.ensemble.Ensemble;
import org.briljantframework.vector.Vector;

/**
 * Created by Isak Karlsson on 19/11/14.
 */
public class ClassificationForest implements Classifier {
  private Ensemble ensemble;

  protected ClassificationForest(Builder builder) {
    ensemble = builder.ensemble.create();
  }

  public static Builder withSize(int size) {
    return new Builder();
  }

  @Override
  public Model fit(DataFrame dataset, Vector target) {
    return ensemble.fit(dataset, target);
  }

  @Override
  public String toString() {
    return String.format("Random Classification Forest");
  }

  public static class Builder implements Classifier.Builder<ClassificationForest> {

    private RandomSplitter.Builder splitter = RandomSplitter.withMaximumFeatures(-1);
    private ClassificationTree.Builder tree = ClassificationTree.withSplitter(splitter);
    private Ensemble.Builder ensemble = Ensemble.withMember(tree);

    public Builder withSize(int size) {
      ensemble.withSize(size);
      return this;
    }

    public Builder withMaximumFeatures(int size) {
      splitter.withMaximumFeatures(size);
      return this;
    }

    @Override
    public ClassificationForest create() {
      return new ClassificationForest(this);
    }
  }
}
