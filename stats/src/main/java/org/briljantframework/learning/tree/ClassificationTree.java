/*
 * ADEB - machine learning pipelines made easy
 * Copyright (C) 2014  Isak Karlsson
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package org.briljantframework.learning.tree;

import org.briljantframework.data.DataFrame;
import org.briljantframework.data.Row;
import org.briljantframework.data.column.CategoricColumn;
import org.briljantframework.data.values.Value;
import org.briljantframework.learning.Classifier;
import org.briljantframework.learning.Prediction;
import org.briljantframework.learning.ensemble.Ensemble;
import org.briljantframework.learning.example.Examples;

/**
 * Created by Isak Karlsson on 17/09/14.
 */
public class ClassificationTree extends Tree<Row, DataFrame<?>, CategoricColumn, ValueThreshold> {

    /**
     * The Prediction visitor.
     */
    protected final SimplePredictionVisitor predictionVisitor = new SimplePredictionVisitor();

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
    public static Builder withSplitter(Splitter.Builder<? extends Splitter<DataFrame<?>, CategoricColumn, ValueThreshold>> splitter) {
        return new Builder(splitter);
    }

    @Override
    public Model fit(DataFrame<?> dataFrame, CategoricColumn column) {
        Examples examples = this.examples;

        // Initialize the examples, if not already initialized
        if (examples == null)
            examples = Examples.fromTarget(column);

        Node<ValueThreshold> node = build(dataFrame, column, examples);
        return new Model(node, predictionVisitor);
    }


    private static final class SimplePredictionVisitor implements Visitor<ValueThreshold> {
        @Override
        public Prediction visitLeaf(Leaf<ValueThreshold> leaf, Row example) {
            return Prediction.unary(leaf.getLabel());//, leaf.getRelativeFrequency());
        }

        @Override
        public Prediction visitBranch(Branch<ValueThreshold> node, Row example) {
            Value value = example.getValue(node.getThreshold().getAxis());
            if (node.getThreshold().getValue().compareTo(value) <= 0) {
                return visit(node.getLeft(), example);
            } else {
                return visit(node.getRight(), example);
            }
        }
    }

    /**
     * The type Model.
     */
    public static class Model extends Tree.Model<Row, DataFrame<?>, ValueThreshold> {

        private Model(Node<ValueThreshold> node, Visitor<ValueThreshold> predictionVisitor) {
            super(node, predictionVisitor);
        }
    }

    /**
     * The type Builder.
     */
    public static class Builder implements Ensemble.Member<Row, DataFrame<? extends Row>, CategoricColumn>,
            Classifier.Builder<ClassificationTree> {

        private final Splitter.Builder<? extends Splitter<DataFrame<?>, CategoricColumn, ValueThreshold>> splitter;

        /**
         * Instantiates a new Builder.
         *
         * @param splitter the splitter
         */
        public Builder(Splitter.Builder<? extends Splitter<DataFrame<?>, CategoricColumn, ValueThreshold>> splitter) {
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
