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

package org.briljantframework.learning.tree;

import org.briljantframework.dataframe.DataFrame;
import org.briljantframework.learning.Classifier;
import org.briljantframework.learning.Prediction;
import org.briljantframework.learning.ensemble.Ensemble;
import org.briljantframework.learning.example.Examples;
import org.briljantframework.vector.Type;
import org.briljantframework.vector.Value;
import org.briljantframework.vector.Vector;

/**
 * Created by Isak Karlsson on 17/09/14.
 */
public class ClassificationTree extends Tree<ValueThreshold> {

  /**
   * Instantiates a new Decision tree.
   *
   * @param builder the splitter
   */
  protected ClassificationTree(Builder builder) {
    super(builder.splitter.create());
  }

  /**
   * Instantiates a new Decision tree.
   *
   * @param splitter the splitter
   * @param examples the examples
   */
  protected ClassificationTree(Builder splitter, Examples examples) {
    super(splitter.splitter.create(), examples);
  }

  /**
   * With splitter.
   *
   * @param splitter the splitter
   * @return the builder
   */
  public static Builder withSplitter(Splitter.Builder<? extends Splitter<ValueThreshold>> splitter) {
    return new Builder(splitter);
  }

  @Override
  public Model fit(DataFrame dataFrame, Vector column) {
    Examples examples = this.examples;

    // Initialize the examples, if not already initialized
    if (examples == null)
      examples = Examples.fromVector(column);

    Node<ValueThreshold> node = build(dataFrame, column, examples);
    return new Model(node, new SimplePredictionVisitor());
  }


  private static final class SimplePredictionVisitor implements Visitor<ValueThreshold> {

    private static final int MISSING = 0, LEFT = -1, RIGHT = 1;

    @Override
    public Prediction visitLeaf(Leaf<ValueThreshold> leaf, Vector example) {
      return Prediction.unary(leaf.getLabel());// , leaf.getRelativeFrequency());
    }

    @Override
    public Prediction visitBranch(Branch<ValueThreshold> node, Vector example) {
      Value value = node.getThreshold().getValue();
      int axis = node.getThreshold().getAxis();
      Type type = value.getType();


      int direction = MISSING;
      if (!example.isNA(axis)) {
        switch (value.getType().getScale()) {
          case CATEGORICAL:
            direction = type.equals(value, axis, example) ? LEFT : RIGHT;
            break;
          case NUMERICAL:
            direction = type.compare(value, axis, example) <= 0 ? LEFT : RIGHT;
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
          return visit(node.getLeft(), example);
          // throw new IllegalStateException("ClassificationTree cannot handle missing values");
      }
    }
  }

  /**
   * The type Model.
   */
  public static class Model extends Tree.Model<ValueThreshold> {

    private Model(Node<ValueThreshold> node, Visitor<ValueThreshold> predictionVisitor) {
      super(node, predictionVisitor);
    }
  }

  /**
   * The type Builder.
   */
  public static class Builder implements Ensemble.Member, Classifier.Builder<ClassificationTree> {

    private final Splitter.Builder<? extends Splitter<ValueThreshold>> splitter;

    /**
     * Instantiates a new Builder.
     *
     * @param splitter the splitter
     */
    public Builder(Splitter.Builder<? extends Splitter<ValueThreshold>> splitter) {
      this.splitter = splitter;
    }

    /**
     * Create decision tree.
     *
     * @return the decision tree
     */
    public ClassificationTree create() {
      return new ClassificationTree(this);
    }

    @Override
    public ClassificationTree create(Examples sample) {
      return new ClassificationTree(this, sample);
    }
  }
}
