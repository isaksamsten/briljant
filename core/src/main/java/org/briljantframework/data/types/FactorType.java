package org.briljantframework.data.types;

import org.briljantframework.data.column.Column;
import org.briljantframework.data.column.DefaultFactorColumn;
import org.briljantframework.data.values.Factor;
import org.briljantframework.data.values.Missing;
import org.briljantframework.data.values.Value;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by Isak Karlsson on 11/11/14.
 */
public class FactorType extends Type {

    private final Map<Integer, Factor> values;

    /**
     * Instantiates a new Header.
     *
     * @param name the name
     */
    public FactorType(String name) {
        this(name, new HashMap<>());
    }

    @Override
    public Column.CopyTo<? extends Column> getColumnFactory() {
        return DefaultFactorColumn.copyTo();
    }

    /**
     * Instantiates a new Factor type.
     *
     * @param name   the name
     * @param values the integer factor hash map
     */
    protected FactorType(String name, HashMap<Integer, Factor> values) {
        super(name);
        this.values = values;
    }


    @Override
    public DataType getDataType() {
        return DataType.FACTOR;
    }

    @Override
    public Set<Value> getDomain() {
        return new HashSet<>(values.values());
    }

    @Override
    protected Value makeValueOf(Object o) {
        if (o instanceof Number) {
            Factor old = values.get(o);
            if (old == null) {
                int value = ((Number) o).intValue();
                Factor factor = Factor.valueOf(value);
                values.put(value, factor);
                return factor;
            } else {
                return old;
            }
        } else {
            return Missing.valueOf();
        }
    }

    @Override
    protected Value makeConversion(Value value) {
        if (value instanceof Factor) {
            Integer val = (Integer) value.value();
            Factor old = values.get(val);
            if (old == null) {
                values.put(val, (Factor) value);
            }
            return value;
        } else {
            return makeValueOf(value.asDouble());
        }
    }

    @Override
    public FactorType clone() {
        return new FactorType(getName(), new HashMap<>(values));
    }
}
