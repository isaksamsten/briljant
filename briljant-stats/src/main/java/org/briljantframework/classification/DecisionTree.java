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

import org.briljantframework.classification.tree.ClassSet;
import org.briljantframework.classification.tree.Splitter;
import org.briljantframework.classification.tree.TreeBranch;
import org.briljantframework.classification.tree.TreeLeaf;
import org.briljantframework.classification.tree.TreeNode;
import org.briljantframework.classification.tree.TreePredictor;
import org.briljantframework.classification.tree.TreeSplit;
import org.briljantframework.classification.tree.TreeVisitor;
import org.briljantframework.classification.tree.ValueThreshold;
import org.briljantframework.dataframe.DataFrame;
import org.briljantframework.matrix.DoubleMatrix;
import org.briljantframework.vector.Value;
import org.briljantframework.vector.Vector;
import org.briljantframework.vector.VectorType;
import org.briljantframework.vector.Vectors;

/**
 * @author Isak Karlsson
 */
public class DecisionTree implements Classifier {

  protected final double mininumWeight = 1;
  protected final Splitter splitter;

  protected ClassSet classSet;
  protected Vector classes = null;


  public DecisionTree(Splitter splitter) {
    this(splitter, null, null);
  }

  protected DecisionTree(Splitter splitter, ClassSet classSet, Vector classes) {
    this.splitter = splitter;
    this.classSet = classSet;
    this.classes = classes;
  }

  @Override
  public Predictor fit(DataFrame x, Vector y) {
    ClassSet classSet = this.classSet;
    Vector classes = this.classes != null ? this.classes : Vectors.unique(y);
    if (classSet == null) {
      classSet = new ClassSet(y, classes);
    }

    TreeNode<ValueThreshold> node = build(x, y, classSet);
    return new Predictor(classes, node, new SimplePredictionVisitor());
  }

  protected TreeNode<ValueThreshold> build(DataFrame frame, Vector target, ClassSet classSet) {
    return build(frame, target, classSet, 0);
  }

  protected TreeNode<ValueThreshold> build(DataFrame frame, Vector target, ClassSet classSet,
      int depth) {
    if (classSet.getTotalWeight() <= mininumWeight || classSet.getTargetCount() == 1) {
      return TreeLeaf.fromExamples(classSet);
    }
    System.out.println(depth);
    TreeSplit<ValueThreshold> maxSplit = splitter.find(classSet, frame, target);
    if (maxSplit == null) {
      return TreeLeaf.fromExamples(classSet);
    } else {
      TreeNode<ValueThreshold> leftNode = build(frame, target, maxSplit.getLeft(), depth + 1);
      TreeNode<ValueThreshold> rightNode = build(frame, target, maxSplit.getRight(), depth + 1);
      return new TreeBranch<>(leftNode, rightNode, maxSplit.getThreshold());
    }
  }


  private static final class SimplePredictionVisitor implements TreeVisitor<ValueThreshold> {

    private static final int MISSING = 0, LEFT = -1, RIGHT = 1;

    @Override
    public DoubleMatrix visitLeaf(TreeLeaf<ValueThreshold> leaf, Vector example) {
      return leaf.getProbabilities();
    }

    @Override
    public DoubleMatrix visitBranch(TreeBranch<ValueThreshold> node, Vector example) {
      Value threshold = node.getThreshold().getValue();
      int axis = node.getThreshold().getAxis();
      VectorType type = threshold.getType();


      int direction = MISSING;
      if (!example.isNA(axis)) {
        switch (threshold.getType().getScale()) {
          case NOMINAL:
            direction = type.equals(threshold, axis, example) ? LEFT : RIGHT;
            break;
          case NUMERICAL:
            direction = type.compare(threshold, axis, example) <= 0 ? LEFT : RIGHT;
            break;
        }
      }

      switch (direction) {
        case LEFT:
          return visit(node.getLeft(), example);
        case RIGHT:
          return visit(node.getRight(), example);
        case MISSING:
        default:
          return visit(node.getLeft(), example); // TODO: what to do with missing values?
      }
    }
  }

  public static class Predictor extends TreePredictor<ValueThreshold> {

    private Predictor(Vector classes, TreeNode<ValueThreshold> node,
        TreeVisitor<ValueThreshold> predictionVisitor) {
      super(classes, node, predictionVisitor);
    }

    // @Override
    // public DoubleMatrix predictProba(DataFrame x) {
    // DoubleMatrix probas = Matrices.newMatrix(x.rows(), getClasses().size());
    // for (int i = 0; i < x.rows(); i++) {
    // probas.setRow(i, predictProba(x.getRecord(i)));
    // }
    // return probas;
    // }
    //
    // @Override
    // public DoubleMatrix predictProba(Vector row) {
    // Vector prediction = predict(row);
    // Vector classes = getClasses();
    // DoubleMatrix probas = Matrices.of(classes.size());
    // for (int i = 0; i < classes.size(); i++) {
    // if (prediction.compare(0, i, classes) == 0) {
    // probas.set(i, 1);
    // break;
    // }
    // }
    // return probas;
    // }
  }
}