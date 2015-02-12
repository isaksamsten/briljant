package org.briljantframework.classification.tree;

import org.briljantframework.classification.AbstractPredictor;
import org.briljantframework.vector.Vector;

/**
 * Created by isak on 2/11/15.
 */
public abstract class TreePredictor<T> extends AbstractPredictor {

  private final TreeVisitor<T> predictionVisitor;

  private final TreeNode<T> node;

  /**
   * Instantiates a new Model.
   *
   * @param node the node
   * @param predictionVisitor the prediction visitor
   */
  protected TreePredictor(Vector classes, TreeNode<T> node, TreeVisitor<T> predictionVisitor) {
    super(classes);
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
  public Vector predict(Vector row) {
    return predictionVisitor.visit(node, row);
  }


}
