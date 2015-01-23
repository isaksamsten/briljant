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

package org.briljantframework.classification;

import static org.briljantframework.classification.tree.Tree.Leaf;
import static org.briljantframework.classification.tree.Tree.Node;

import org.briljantframework.Utils;
import org.briljantframework.classification.tree.*;
import org.briljantframework.dataframe.DataFrame;
import org.briljantframework.dataseries.Aggregator;
import org.briljantframework.dataseries.Approximations;
import org.briljantframework.dataseries.MeanAggregator;
import org.briljantframework.distance.Distance;
import org.briljantframework.matrix.ArrayDoubleMatrix;
import org.briljantframework.matrix.DoubleMatrix;
import org.briljantframework.matrix.Ints;
import org.briljantframework.shapelet.Shapelet;
import org.briljantframework.vector.Vector;

/**
 * Created by Isak Karlsson on 16/09/14.
 */
public class ShapeletTree implements Classifier {

  private final ShapeletSplitter splitter;
  private final Examples examples;

  /**
   * Instantiates a new Time series tree.
   *
   * @param splitter the splitter
   */
  protected ShapeletTree(ShapeletSplitter splitter) {
    this(splitter, null);
  }

  /**
   * Instantiates a new Time series tree.
   *
   * @param splitter the splitter
   * @param examples the examples
   */
  protected ShapeletTree(ShapeletSplitter splitter, Examples examples) {
    this.splitter = splitter;
    this.examples = examples;

  }

  /**
   * With splitter.
   *
   * @param splitter the splitter
   * @return the builder
   */
  public static Builder withSplitter(Splitter.Builder<? extends ShapeletSplitter> splitter) {
    return new Builder(splitter);
  }

  @Override
  public Model fit(DataFrame x, Vector y) {
    Examples examples = this.examples;

    // Initialize the examples, if not already initialized
    if (examples == null) {
      examples = Examples.fromVector(y);
    }

    Params params = new Params();
    params.noExamples = examples.getTotalWeight();
    params.lengthImportance = new double[x.columns()];
    params.positionImportance = new double[x.columns()];
    int size = Ints.randInt(10, x.columns() - 1);
//    x = Approximations.paa(x, size);
//    System.out.println(size);
    Node<ShapeletThreshold> node = build(x, y, examples, params);
    return new Model(node, new ShapletTreeVisitor(size, splitter.getDistanceMetric()),
        new ArrayDoubleMatrix(1, params.lengthImportance.length, params.lengthImportance),
        new ArrayDoubleMatrix(1, params.positionImportance.length, params.positionImportance));
  }

  /**
   * Build node.
   *
   * @param x the frame
   * @param y the target
   * @param examples the examples
   * @param params the depth
   * @return the node
   */
  protected Node<ShapeletThreshold> build(DataFrame x, Vector y, Examples examples, Params params) {
    /*
     * STEP 0: pre-prune some useless branches
     */
    if (examples.getTotalWeight() <= 2 || examples.getTargetCount() == 1) {
      return Leaf.fromExamples(examples);
    }

    params.depth += 1;
    /*
     * STEP 1: Find a good separating feature
     */
    Tree.Split<ShapeletThreshold> maxSplit = splitter.find(examples, x, y);

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
      Shapelet shapelet = maxSplit.getThreshold().getShapelet();
      Impurity impurity = splitter.getGain().getImpurity();

      double imp = impurity.impurity(examples);
      double weight = (maxSplit.size() / params.noExamples) * (imp - maxSplit.getImpurity());

      params.lengthImportance[shapelet.size()] += weight;
      double length = shapelet.size();
      for (int i = shapelet.start(); i < length + shapelet.start(); i++) {
        params.positionImportance[i] = params.positionImportance[i] + (weight / length);
      }

      Node<ShapeletThreshold> leftNode = build(x, y, maxSplit.getLeft(), params);
      Node<ShapeletThreshold> rightNode = build(x, y, maxSplit.getRight(), params);
      return new Tree.Branch<>(leftNode, rightNode, maxSplit.getThreshold());
    }
  }

  private static class Params {
    public double noExamples;
    private double[] lengthImportance;
    private double[] positionImportance;
    private int depth = 0;
  }

  /**
   * The type Model.
   */
  public static class Model extends Tree.Model<ShapeletThreshold> {

    private final ArrayDoubleMatrix lengthImportance;
    private final ArrayDoubleMatrix positionImportance;


    /**
     * Instantiates a new Model.
     * 
     * @param node the node
     * @param predictionVisitor the prediction visitor
     * @param lengthImportance the dense matrix
     * @param positionImportance the position importance
     */
    protected Model(Node<ShapeletThreshold> node, ShapletTreeVisitor predictionVisitor,
        ArrayDoubleMatrix lengthImportance, ArrayDoubleMatrix positionImportance) {
      super(node, predictionVisitor);
      this.lengthImportance = lengthImportance;
      this.positionImportance = positionImportance;
    }

    /**
     * Gets position importance.
     *
     * @return the position importance
     */
    public ArrayDoubleMatrix getPositionImportance() {
      return positionImportance;
    }

    /**
     * Gets length importance.
     *
     * @return the length importance
     */
    public DoubleMatrix getLengthImportance() {
      return lengthImportance;
    }
  }

  private static class ShapletTreeVisitor implements Tree.Visitor<ShapeletThreshold> {

    private final Distance metric;
    private final Aggregator aggregator;

    private ShapletTreeVisitor(int size, Distance metric) {
      this.metric = metric;
      this.aggregator = new MeanAggregator(size);
    }

    @Override
    public Label visitLeaf(Leaf<ShapeletThreshold> leaf, Vector example) {
      return Label.unary(leaf.getLabel());// , leaf.getRelativeFrequency());
    }

    @Override
    public Label visitBranch(Tree.Branch<ShapeletThreshold> node, Vector example) {
      //aggregator.aggregate(example)
      if (metric.distance(example, node.getThreshold().getShapelet()) < node
          .getThreshold().getDistance()) {
        return visit(node.getLeft(), example);
      } else {
        return visit(node.getRight(), example);
      }
    }
  }

  /**
   * The type Builder.
   */
  public static class Builder implements Classifier.Builder<ShapeletTree> {

    private final Splitter.Builder<? extends ShapeletSplitter> splitter;

    /**
     * Instantiates a new Builder.
     *
     * @param splitter the splitter
     */
    public Builder(Splitter.Builder<? extends ShapeletSplitter> splitter) {
      this.splitter = splitter;
    }

    /**
     * Create time series tree.
     *
     * @return the time series tree
     */
    public ShapeletTree build() {
      return new ShapeletTree(splitter.create());
    }

    public ShapeletTree create(Examples sample) {
      return new ShapeletTree(splitter.create(), sample);
    }
  }

}
