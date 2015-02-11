package org.briljantframework.classification.tree;

import org.briljantframework.classification.ClassifierModel;
import org.briljantframework.classification.Label;
import org.briljantframework.vector.Vector;

/**
 * Created by isak on 2/11/15.
 */
public abstract class TreeModel<T> implements ClassifierModel {

  private final TreeVisitor<T> predictionVisitor;

  private final TreeNode<T> node;

  /**
   * Instantiates a new Model.
   *
   * @param node the node
   * @param predictionVisitor the prediction visitor
   */
  protected TreeModel(TreeNode<T> node, TreeVisitor<T> predictionVisitor) {
    this.node = node;
    this.predictionVisitor = predictionVisitor;
  }

  /**
   * Gets node.
   *
   * @return the node
   */
  public TreeNode<T> getTree() {
    return node;
  }

  @Override
  public Label predict(Vector row) {
    return predictionVisitor.visit(node, row);
  }


}
