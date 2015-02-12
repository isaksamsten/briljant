package org.briljantframework.classification.tree;

import org.briljantframework.vector.Vector;

/**
 * @author Isak Karlsson
 */
public interface TreeVisitor<T> {

  default Vector visit(TreeNode<T> node, Vector example) {
    return node.visit(this, example);
  }

  Vector visitLeaf(TreeLeaf<T> leaf, Vector example);

  Vector visitBranch(TreeBranch<T> node, Vector example);
}
