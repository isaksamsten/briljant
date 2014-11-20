package org.briljantframework.data.column;

import org.briljantframework.data.types.FactorType;
import org.briljantframework.data.types.Type;
import org.briljantframework.data.values.Value;

import java.util.List;

/**
 * Created by Isak Karlsson on 11/11/14.
 */
public class DefaultFactorColumn extends AbstractColumn implements FactorColumn {


    /**
     * Instantiates a new Basic target.
     *
     * @param type   the types
     * @param values the values
     */
    public DefaultFactorColumn(Type type, List<Value> values) {
        super(type, values);
    }

    /**
     * Gets factory.
     *
     * @return the factory
     */
    public static CopyTo<FactorColumn> copyTo() {
        return old -> new DefaultFactorColumnBuilder(new FactorType(old.getName()));
    }

    @Override
    public double get(int id) {
        return getValue(id).asDouble();
    }

    private static class DefaultFactorColumnBuilder extends AbstractColumnBuilder<FactorColumn> {

        /**
         * Instantiates a new Builder.
         *
         * @param type the types
         */
        public DefaultFactorColumnBuilder(Type type) {
            super(type);
        }

        @Override
        public FactorColumn create() {
            return new DefaultFactorColumn(type, values);
        }
    }
}
