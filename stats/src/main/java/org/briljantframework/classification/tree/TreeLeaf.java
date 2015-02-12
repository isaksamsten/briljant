package org.briljantframework.classification.tree;

import org.briljantframework.vector.Vector;

/**
 * Created by isak on 2/11/15.
 */
public final class TreeLeaf<T> implements TreeNode<T> {

  private String label;
  private double relativeFrequency;

  private Vector labels;
  private Vector probabilities;

  public TreeLeaf(String label, double relativeFrequency) {
    this.label = label;
    this.relativeFrequency = relativeFrequency;
  }

  public static <T> TreeLeaf<T> fromExamples(ClassSet classSet) {
    String probable = classSet.getMostProbable();
    return new TreeLeaf<>(probable, classSet.get(probable).getWeight() / classSet.getTotalWeight());
  }

  public double getRelativeFrequency() {
    return relativeFrequency;
  }

  public String getLabel() {
    return label;
  }

  @Override
  public final Vector visit(TreeVisitor<T> visitor, Vector example) {
    return visitor.visitLeaf(this, example);
  }
}
