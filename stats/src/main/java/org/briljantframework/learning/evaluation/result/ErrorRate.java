package org.briljantframework.learning.evaluation.result;

import com.google.common.base.Preconditions;
import org.briljantframework.learning.Predictions;
import org.briljantframework.vector.Vector;

/**
 * Created by isak on 02/10/14.
 */
public class ErrorRate extends AbstractMetric {


    /**
     * Instantiates a new Error.
     *
     * @param producer the producer
     */
    public ErrorRate(AbstractMetric.Producer producer) {
        super(producer);
    }

    /**
     * The constant FACTORY.
     */
    public static Factory getFactory() {
        return Producer::new;
    }

    @Override
    public String getName() {
        return "Error";
    }

    @Override
    public int compareTo(Metric other) {
        return Double.compare(getAverage(), other.getAverage());
    }

    private static class Producer extends AbstractMetric.Producer {

        @Override
        public Producer add(Sample sample, Predictions predictions, Vector column) {
            Preconditions.checkArgument(predictions.size() == column.size());

            double accuracy = 0.0;
            for (int i = 0; i < predictions.size(); i++) {
                if (predictions.get(i).getValue().equals(column.getAsString(i))) {
                    accuracy++;
                }
            }

            add(sample, 1 - accuracy / predictions.size());
            return this;
        }

        @Override
        public Metric produce() {
            return new ErrorRate(this);
        }
    }
}
