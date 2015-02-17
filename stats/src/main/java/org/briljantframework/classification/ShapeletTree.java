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

import org.briljantframework.Utils;
import org.briljantframework.classification.tree.*;
import org.briljantframework.dataframe.DataFrame;
import org.briljantframework.dataseries.Aggregator;
import org.briljantframework.dataseries.MeanAggregator;
import org.briljantframework.distance.Distance;
import org.briljantframework.matrix.DoubleMatrix;
import org.briljantframework.matrix.Matrices;
import org.briljantframework.matrix.Range;
import org.briljantframework.shapelet.Shapelet;
import org.briljantframework.vector.Vector;
import org.briljantframework.vector.Vectors;

/**
 * Created by Isak Karlsson on 16/09/14.
 */
public class ShapeletTree implements Classifier {

  private final ShapeletSplitter splitter;
  private final ClassSet classSet;
  private Vector classes;

  protected ShapeletTree(ShapeletSplitter splitter) {
    this(splitter, null, null);
  }

  protected ShapeletTree(ShapeletSplitter splitter, ClassSet classSet, Vector classes) {
    this.splitter = splitter;
    this.classSet = classSet;
    this.classes = classes;
  }

  public static Builder withSplitter(ShapeletSplitter splitter) {
    return new Builder(splitter);
  }

  @Override
  public Predictor fit(DataFrame x, Vector y) {
    ClassSet classSet = this.classSet;
    Vector classes = this.classes != null ? this.classes : Vectors.unique(y);
    if (classSet == null) {
      classSet = new ClassSet(y, classes);
    }

    Params params = new Params();
    params.noExamples = classSet.getTotalWeight();
    params.lengthImportance = Matrices.newDoubleVector(x.columns());
    params.positionImportance = Matrices.newDoubleVector(x.columns());
    int size = Utils.randInt(10, x.columns() - 1);
    // x = Approximations.paa(x, size);
    // System.out.println(size);
    TreeNode<ShapeletThreshold> node = build(x, y, classSet, params);
    return new Predictor(classes, node, new ShapletTreeVisitor(size, splitter.getDistanceMetric()),
        params.lengthImportance, params.positionImportance);
  }

  protected TreeNode<ShapeletThreshold> build(DataFrame x, Vector y, ClassSet classSet,
      Params params) {
    /*
     * STEP 0: pre-prune some useless branches
     */
    if (classSet.getTotalWeight() <= 3 || classSet.getTargetCount() == 1) {
      return TreeLeaf.fromExamples(classSet);
    }

    params.depth += 1;
    /*
     * STEP 1: Find a good separating feature
     */
    TreeSplit<ShapeletThreshold> maxSplit = splitter.find(classSet, x, y);

    /*
     * STEP 2a: if no split could be found create a leaf
     */
    if (maxSplit == null) {
      return TreeLeaf.fromExamples(classSet);
    } else if (maxSplit.getLeft().isEmpty()) {
      return TreeLeaf.fromExamples(maxSplit.getRight());
    } else if (maxSplit.getRight().isEmpty()) {
      return TreeLeaf.fromExamples(maxSplit.getLeft());
    } else {
      Shapelet shapelet = maxSplit.getThreshold().getShapelet();
      Impurity impurity = splitter.getGain().getImpurity();

      double imp = impurity.impurity(classSet);
      double weight = (maxSplit.size() / params.noExamples) * (imp - maxSplit.getImpurity());

      params.lengthImportance.addTo(shapelet.size(), weight);
      int length = shapelet.size();
      int start = shapelet.start();
      int end = start + length;
      params.positionImportance.slice(Range.range(start, end)).update(
          i -> i + (weight / (double) length));

      TreeNode<ShapeletThreshold> leftNode = build(x, y, maxSplit.getLeft(), params);
      TreeNode<ShapeletThreshold> rightNode = build(x, y, maxSplit.getRight(), params);
      return new TreeBranch<>(leftNode, rightNode, maxSplit.getThreshold());
    }
  }

  private static class Params {
    public double noExamples;
    private DoubleMatrix lengthImportance;
    private DoubleMatrix positionImportance;
    private int depth = 0;
  }

  public static class Predictor extends TreePredictor<ShapeletThreshold> {

    private final DoubleMatrix lengthImportance;
    private final DoubleMatrix positionImportance;

    protected Predictor(Vector classes, TreeNode<ShapeletThreshold> node,
        ShapletTreeVisitor predictionVisitor, DoubleMatrix lengthImportance,
        DoubleMatrix positionImportance) {
      super(classes, node, predictionVisitor);
      this.lengthImportance = lengthImportance;
      this.positionImportance = positionImportance;
    }

    /**
     * Gets position importance.
     *
     * @return the position importance
     */
    public DoubleMatrix getPositionImportance() {
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

  private static class ShapletTreeVisitor implements TreeVisitor<ShapeletThreshold> {

    private final Distance metric;
    private final Aggregator aggregator;

    private ShapletTreeVisitor(int size, Distance metric) {
      this.metric = metric;
      this.aggregator = new MeanAggregator(size);
    }

    @Override
    public DoubleMatrix visitLeaf(TreeLeaf<ShapeletThreshold> leaf, Vector example) {
      return leaf.getProbabilities();
    }

    @Override
    public DoubleMatrix visitBranch(TreeBranch<ShapeletThreshold> node, Vector example) {
      // aggregator.aggregate(example)
      if (metric.compute(example, node.getThreshold().getShapelet()) < node.getThreshold()
          .getDistance()) {
        return visit(node.getLeft(), example);
      } else {
        return visit(node.getRight(), example);
      }
    }
  }

  public static class Builder implements Classifier.Builder<ShapeletTree> {

    private final ShapeletSplitter splitter;

    public Builder(ShapeletSplitter splitter) {
      this.splitter = splitter;
    }

    public ShapeletTree build() {
      return new ShapeletTree(splitter);
    }

    public ShapeletTree create(ClassSet sample) {
      return new ShapeletTree(splitter, sample, null);
    }
  }

}
