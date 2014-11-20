package org.briljantframework.learning.tree;

import org.briljantframework.data.DataFrame;
import org.briljantframework.data.Row;
import org.briljantframework.data.column.CategoricColumn;
import org.briljantframework.learning.Classifier;
import org.briljantframework.learning.Model;
import org.briljantframework.learning.ensemble.Ensemble;

/**
 * Created by Isak Karlsson on 19/11/14.
 */
public class ClassificationForest implements Classifier<Row, DataFrame<? extends Row>, CategoricColumn> {
    private Ensemble<Row, DataFrame<? extends Row>, CategoricColumn> ensemble;

    protected ClassificationForest(Builder builder) {
        ensemble = builder.ensemble.create();
    }

    public static Builder withSize(int size) {
        return new Builder();
    }

    @Override
    public Model<Row, DataFrame<? extends Row>> fit(DataFrame<? extends Row> dataset, CategoricColumn target) {
        return ensemble.fit(dataset, target);
    }

    @Override
    public String toString() {
        return String.format("Random Classification Forest");
    }

    public static class Builder implements Classifier.Builder<ClassificationForest> {

        private RandomSplitter.Builder splitter = RandomSplitter.withMaximumFeatures(-1);
        private ClassificationTree.Builder tree = ClassificationTree.withSplitter(splitter);
        private Ensemble.Builder<Row, DataFrame<? extends Row>, CategoricColumn> ensemble = Ensemble.withMember(tree);

        public Builder withSize(int size) {
            ensemble.withSize(size);
            return this;
        }

        public Builder withMaximumFeatures(int size) {
            splitter.withMaximumFeatures(size);
            return this;
        }

        @Override
        public ClassificationForest create() {
            return new ClassificationForest(this);
        }
    }
}
