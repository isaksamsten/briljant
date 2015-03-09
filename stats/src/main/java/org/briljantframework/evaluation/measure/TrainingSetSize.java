package org.briljantframework.evaluation.measure;

/**
 * @author Isak Karlsson
 */
public class TrainingSetSize extends AbstractMeasure {
  protected TrainingSetSize(Builder builder) {
    super(builder);
  }

  @Override
  public String getName() {
    return "Training-set size";
  }

  public static class Builder extends AbstractMeasure.Builder<TrainingSetSize> {

    @Override
    public TrainingSetSize build() {
      return new TrainingSetSize(this);
    }
  }
}
