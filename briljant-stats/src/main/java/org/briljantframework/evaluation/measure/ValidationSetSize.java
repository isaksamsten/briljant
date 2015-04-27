package org.briljantframework.evaluation.measure;

/**
 * @author Isak Karlsson
 */
public class ValidationSetSize extends AbstractMeasure {
    protected ValidationSetSize(Builder builder) {
        super(builder);
    }

    @Override
    public String getName() {
        return "Validation-set size";
    }

    public static class Builder extends AbstractMeasure.Builder<ValidationSetSize> {

        @Override
        public ValidationSetSize build() {
            return new ValidationSetSize(this);
        }
    }
}