/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Isak Karlsson
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
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
import org.briljantframework.array.DoubleArray;
import org.briljantframework.vector.Is;
import org.briljantframework.vector.Vec;
import org.briljantframework.vector.Vector;

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
    Vector classes = this.classes != null ? this.classes : Vec.unique(y);
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
    public DoubleArray visitLeaf(TreeLeaf<ValueThreshold> leaf, Vector example) {
      return leaf.getProbabilities();
    }

    @Override
    public DoubleArray visitBranch(TreeBranch<ValueThreshold> node, Vector example) {
      Object threshold = node.getThreshold().getValue();
      int axis = node.getThreshold().getAxis();
      int direction = MISSING;
      if (!example.isNA(axis)) {
        if (Is.nominal(threshold)) {
          direction = example.equals(axis, threshold) ? LEFT : RIGHT;
        } else {
          direction = example.compare(axis, (Comparable<?>) threshold) <= 0 ? LEFT : RIGHT;
        }

//        switch (threshold.getType().getScale()) {
//          case NOMINAL:
//            direction = type.equals(threshold, axis, example) ? LEFT : RIGHT;
//            break;
//          case NUMERICAL:
//            direction = type.compare(threshold, axis, example) <= 0 ? LEFT : RIGHT;
//            break;
//        }
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
