/*
 * ADEB - machine learning pipelines made easy Copyright (C) 2014 Isak Karlsson
 * 
 * This program is free software; you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation; either version 2 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with this program; if
 * not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301 USA.
 */

package org.briljantframework.classification.tree;

import org.briljantframework.classification.Classifier;
import org.briljantframework.classification.Prediction;
import org.briljantframework.dataframe.DataFrame;
import org.briljantframework.vector.Vector;

import com.google.common.base.Preconditions;

/**
 * Created by Isak Karlsson on 08/09/14.
 *
 * @param <T> the type parameter
 */
public abstract class Tree<T> implements Classifier {

  /**
   * The Mininum weight.
   */
  protected final double mininumWeight = 2;
  /**
   * The Splitter.
   */
  protected final Splitter<T> splitter;
  /**
   * The Examples.
   */
  protected Examples examples;

  /**
   * Instantiates a new Random tree.
   *
   * @param splitter the splitter
   */
  protected Tree(Splitter<T> splitter) {
    this(splitter, null);
  }

  /**
   * Instantiates a new Random tree.
   *
   * @param splitter the splitter
   * @param examples the examples
   */
  protected Tree(Splitter<T> splitter, Examples examples) {
    this.splitter = splitter;
    this.examples = examples;
  }

  /**
   * Build node.
   *
   * @param frame the frame
   * @param target the target
   * @param examples the examples
   * @return the node
   */
  protected Node<T> build(DataFrame frame, Vector target, Examples examples) {
    return build(frame, target, examples, 0);
  }

  /**
   * Build node.
   *
   * @param frame the frame
   * @param target the target
   * @param examples the examples
   * @param depth the depth
   * @return the node
   */
  protected Node<T> build(DataFrame frame, Vector target, Examples examples, int depth) {
    /*
     * STEP 0: pre-prune some useless branches
     */
    if (examples.getTotalWeight() <= mininumWeight || examples.getTargetCount() == 1) {
      return Leaf.fromExamples(examples);
    }

    /*
     * STEP 1: Find a good separating feature
     */
    Split<T> maxSplit = splitter.find(examples, frame, target);

    /*
     * STEP 2a: if no split could be found create a leaf
     */
    if (maxSplit == null) {
      return Leaf.fromExamples(examples);
    }

    /*
     * STEP 2b: [if] the split result in only one partition, create a leaf STEP 2c: [else]
     * recursively build new sub-trees
     */
    if (maxSplit.getLeft().isEmpty()) {
      return Leaf.fromExamples(maxSplit.getRight());
    } else if (maxSplit.getRight().isEmpty()) {
      return Leaf.fromExamples(maxSplit.getLeft());
    } else {
      Node<T> leftNode = build(frame, target, maxSplit.getLeft(), depth + 1);
      Node<T> rightNode = build(frame, target, maxSplit.getRight(), depth + 1);
      return new Branch<>(leftNode, rightNode, maxSplit.getThreshold());
    }
  }

  /**
   * The interface Node.
   *
   * @param <T> the type parameter
   */
  public static interface Node<T> {

    /**
     * Visit prediction.
     *
     * @param visitor the visitor
     * @param example the example
     * @return the prediction
     */
    Prediction visit(Visitor<T> visitor, Vector example);
  }

  /**
   * The interface Visitor.
   *
   * @param <T> the type parameter
   */
  public static interface Visitor<T> {

    /**
     * Visit prediction.
     *
     * @param node the node
     * @param example the example
     * @return the prediction
     */
    default Prediction visit(Node<T> node, Vector example) {
      return node.visit(this, example);
    }

    /**
     * Visit leaf.
     *
     * @param leaf the leaf
     * @param example the example
     * @return the prediction
     */
    Prediction visitLeaf(Leaf<T> leaf, Vector example);

    /**
     * Visit node.
     *
     * @param node the node
     * @param example the example
     * @return the prediction
     */
    Prediction visitBranch(Branch<T> node, Vector example);
  }


  /**
   * The type Leaf.
   *
   * @param <T> the type parameter
   */
  // TODO - store more than the most probable class, store all alternatives
  public static final class Leaf<T> implements Node<T> {

    /**
     * The Label.
     */
    private String label;
    /**
     * The Total.
     */
    private double relativeFrequency;

    /**
     * Instantiates a new Leaf.
     *
     * @param label the label
     * @param relativeFrequency the total
     */
    public Leaf(String label, double relativeFrequency) {
      this.label = label;
      this.relativeFrequency = relativeFrequency;
    }

    /**
     * From examples.
     *
     * @param <T> the type parameter
     * @param examples the examples
     * @return the leaf
     */
    public static <T> Leaf<T> fromExamples(Examples examples) {
      String probable = examples.getMostProbable();
      return new Leaf<>(probable, examples.get(probable).getWeight() / examples.getTotalWeight());
    }

    /**
     * Gets relative frequency.
     *
     * @return the relative frequency
     */
    public double getRelativeFrequency() {
      return relativeFrequency;
    }

    /**
     * Gets label.
     *
     * @return the label
     */
    public String getLabel() {
      return label;
    }

    @Override
    public Prediction visit(Visitor<T> visitor, Vector example) {
      return visitor.visitLeaf(this, example);
    }
  }

  /**
   * The type Tree.
   *
   * @param <T> the type parameter
   */
  public static final class Branch<T> implements Node<T> {

    /**
     * The Left.
     */
    private final Node<T> left;
    /**
     * The Right.
     */
    private final Node<T> right;

    /**
     * The Pivot.
     */
    private T threshold;

    /**
     * Instantiates a new Tree.
     *
     * @param left the left
     * @param right the right
     * @param threshold the pivot
     */
    public Branch(Node<T> left, Node<T> right, T threshold) {
      this.left = left;
      this.right = right;
      this.threshold = threshold;
    }

    /**
     * Gets left.
     *
     * @return the left
     */
    public Node<T> getLeft() {
      return left;
    }

    /**
     * Gets right.
     *
     * @return the right
     */
    public Node<T> getRight() {
      return right;
    }

    /**
     * Gets threshold.
     *
     * @return the threshold
     */
    public T getThreshold() {
      return threshold;
    }

    @Override
    public Prediction visit(Visitor<T> visitor, Vector example) {
      return visitor.visitBranch(this, example);
    }
  }

  /**
   * The type Model.
   *
   * @param <T> the type parameter
   */
  public abstract static class Model<T> implements Classifier.Model {

    private final Visitor<T> predictionVisitor;

    private final Node<T> node;

    /**
     * Instantiates a new Model.
     *
     * @param node the node
     * @param predictionVisitor the prediction visitor
     */
    protected Model(Node<T> node, Visitor<T> predictionVisitor) {
      this.node = node;
      this.predictionVisitor = predictionVisitor;
    }

    /**
     * Gets node.
     *
     * @return the node
     */
    public Node<T> getTree() {
      return node;
    }

    @Override
    public Prediction predict(Vector row) {
      return predictionVisitor.visit(node, row);
    }


  }


  /**
   * The type Binary.
   *
   * @param <E> the type parameter
   */
  public static final class Split<E> {

    /**
     * The Right.
     */
    private final Examples right;
    /**
     * The Left.
     */
    private final Examples left;
    /**
     * The Threshold.
     */
    private final E threshold;
    /**
     * The Impurity.
     */
    private double[] impurity = null;


    /**
     * Instantiates a new Split.
     *
     * @param left the left
     * @param right the right
     * @param threshold the threshold
     */
    public Split(Examples left, Examples right, E threshold) {
      this.left = left;
      this.right = right;
      this.threshold = threshold;
    }

    /**
     * Gets right.
     *
     * @return the right
     */
    public Examples getRight() {
      return right;
    }

    /**
     * Gets left.
     *
     * @return the left
     */
    public Examples getLeft() {
      return left;
    }

    // TODO(isak): remove
    public double getLeftImpurity() {
      return impurity != null ? impurity[1] : 0;
    }

    public double getImpurity() {
      return impurity != null ? impurity[0] : 0;
    }

    public void setImpurity(double[] impurity) {
      Preconditions.checkArgument(impurity.length == 3);
      this.impurity = impurity;
    }

    // TODO(isak): remove (cascading)
    public double getRightImpurity() {
      return impurity != null ? impurity[2] : 0;
    }

    /**
     * Gets threshold.
     *
     * @return the threshold
     */
    public E getThreshold() {
      return threshold;
    }

    /**
     * Size float.
     *
     * @return the float
     */
    public double size() {
      return right.getTotalWeight() + left.getTotalWeight();
    }

    @Override
    public String toString() {
      return String.format("Binary(left=%s, right=%s)", left, right);
    }
  }
}
