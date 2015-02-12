package org.briljantframework.classification.tree;

import org.briljantframework.vector.Vector;

/**
 * Created by isak on 2/11/15.
 */
public interface TreeNode<T> {
  Vector visit(TreeVisitor<T> visitor, Vector example);
}
