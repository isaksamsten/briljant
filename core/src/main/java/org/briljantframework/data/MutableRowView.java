package org.briljantframework.data;

import com.google.common.base.Preconditions;
import org.briljantframework.data.values.Value;

import java.util.HashMap;

/**
 * Created by Isak Karlsson on 19/11/14.
 */
public class MutableRowView extends RowView implements MutableRow {

    private HashMap<Integer, Value> changed = new HashMap<>();
    private int added = 0;

    /**
     * Instantiates a new Entry cursor.
     *
     * @param dataFrame the dataset
     * @param index     the index
     */
    protected MutableRowView(DataFrame dataFrame, int index) {
        super(dataFrame, index);
    }

    @Override
    public void put(int index, Value value) {
        Preconditions.checkArgument(index >= 0 && index < size());
        changed.put(index, value);
    }

    @Override
    public void add(Value value) {
        changed.put(size(), value);
        added += 1;
    }

    @Override
    public int size() {
        return super.size() + added;
    }

    @Override
    public Value getValue(int col) {
        Value value = changed.get(col);
        return value != null ? value : super.getValue(col);
    }

    @Override
    public MutableRow asMutable() {
        return this;
    }
}
