package org.briljantframework.classification.tree;

import org.briljantframework.classification.AbstractPredictor;
import org.briljantframework.matrix.DoubleArray;
import org.briljantframework.vector.Vector;

/**
 * Represents a Tree based predictor. Uses a
 * {@link org.briljantframework.classification.tree.TreeVisitor} to make predictions.
 * 
 * @author Isak Karlsson
 */
public class TreePredictor<T> extends AbstractPredictor {

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
  public DoubleArray estimate(Vector record) {
    return predictionVisitor.visit(node, record);
  }
}
