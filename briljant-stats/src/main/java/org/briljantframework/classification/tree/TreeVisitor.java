package org.briljantframework.classification.tree;

import org.briljantframework.matrix.DoubleMatrix;
import org.briljantframework.vector.Vector;

/**
 * @author Isak Karlsson
 */
public interface TreeVisitor<T> {

  default DoubleMatrix visit(TreeNode<T> node, Vector example) {
    return node.visit(this, example);
  }

  DoubleMatrix visitLeaf(TreeLeaf<T> leaf, Vector example);

  DoubleMatrix visitBranch(TreeBranch<T> node, Vector example);
}
