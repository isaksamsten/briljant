package org.briljantframework.classification.tree;

/**
 * Created by isak on 2/11/15.
 */
public final class TreeSplit<E> {

  private final ClassSet right;
  private final ClassSet left;
  private final E threshold;
  private double impurity = 0;

  public TreeSplit(ClassSet left, ClassSet right, E threshold) {
    this.left = left;
    this.right = right;
    this.threshold = threshold;
  }

  public ClassSet getRight() {
    return right;
  }

  public ClassSet getLeft() {
    return left;
  }

  public double getImpurity() {
    return impurity;
  }

  public void setImpurity(double impurity) {
    this.impurity = impurity;
  }

  public E getThreshold() {
    return threshold;
  }

  public double size() {
    return right.getTotalWeight() + left.getTotalWeight();
  }

  @Override
  public String toString() {
    return String.format("Binary(left=%s, right=%s)", left, right);
  }
}
