package org.briljantframework.classification.tree;

import org.briljantframework.classification.Label;
import org.briljantframework.vector.Vector;

/**
 * Created by isak on 2/11/15.
 */
public final class TreeBranch<T> implements TreeNode<T> {

  private final TreeNode<T> left;
  private final TreeNode<T> right;
  private T threshold;

  public TreeBranch(TreeNode<T> left, TreeNode<T> right, T threshold) {
    this.left = left;
    this.right = right;
    this.threshold = threshold;
  }

  public TreeNode<T> getLeft() {
    return left;
  }

  public TreeNode<T> getRight() {
    return right;
  }

  public T getThreshold() {
    return threshold;
  }

  @Override
  public Label visit(TreeVisitor<T> visitor, Vector example) {
    return visitor.visitBranch(this, example);
  }
}
