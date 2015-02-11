package org.briljantframework.classification.tree;

import org.briljantframework.classification.Label;
import org.briljantframework.vector.Vector;

/**
 * Created by isak on 2/11/15.
 */
public interface TreeVisitor<T> {

  default Label visit(TreeNode<T> node, Vector example) {
    return node.visit(this, example);
  }

  Label visitLeaf(TreeLeaf<T> leaf, Vector example);

  Label visitBranch(TreeBranch<T> node, Vector example);
}
