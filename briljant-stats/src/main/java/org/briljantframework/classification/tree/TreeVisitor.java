package org.briljantframework.classification.tree;

import org.briljantframework.array.DoubleArray;
import org.briljantframework.vector.Vector;

/**
 * @author Isak Karlsson
 */
public interface TreeVisitor<T> {

  default DoubleArray visit(TreeNode<T> node, Vector example) {
    return node.visit(this, example);
  }

  DoubleArray visitLeaf(TreeLeaf<T> leaf, Vector example);

  DoubleArray visitBranch(TreeBranch<T> node, Vector example);
}
