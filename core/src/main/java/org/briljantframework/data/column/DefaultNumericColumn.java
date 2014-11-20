package org.briljantframework.data.column;

import org.briljantframework.data.types.NumericType;
import org.briljantframework.data.types.Type;
import org.briljantframework.data.values.Value;

import java.util.List;

/**
 * Created by Isak Karlsson on 10/11/14.
 */
public class DefaultNumericColumn extends AbstractColumn implements NumericColumn {

    /**
     * Instantiates a new Basic target.
     *
     * @param type   the types
     * @param values the values
     */
    private DefaultNumericColumn(Type type, List<Value> values) {
        super(type, values);
    }

    /**
     * Gets factory.
     *
     * @return the factory
     */
    public static CopyTo<NumericColumn> copyTo() {
        return type -> new ColumnBuilder(new NumericType(type.getName()));
    }

    @Override
    public double get(int id) {
        Object value = getValue(id).value();
        return value == null ? Double.NaN : (double) value;
    }

    private static final class ColumnBuilder extends AbstractColumnBuilder<NumericColumn> {

        /**
         * Instantiates a new Builder.
         *
         * @param type the types
         */
        public ColumnBuilder(Type type) {
            super(type);
        }

        @Override
        public NumericColumn create() {
            return new DefaultNumericColumn(type, values);
        }
    }
}
