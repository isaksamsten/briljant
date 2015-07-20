package org.briljantframework.classification.tree;

import org.briljantframework.array.DoubleArray;
import org.briljantframework.vector.Vector;

/**
 * Created by isak on 2/11/15.
 */
public interface TreeNode<T> {
  DoubleArray visit(TreeVisitor<T> visitor, Vector example);
}
