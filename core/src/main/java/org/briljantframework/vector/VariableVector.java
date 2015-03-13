package org.briljantframework.vector;

/**
 * A sequence is a vector which contains elements of different type.
 * <p>
 * Created by Isak Karlsson on 26/11/14.
 */
public interface VariableVector extends Vector {

  public static final Value NA = Undefined.INSTANCE;

  public static final VectorType TYPE = new VectorType() {
    @Override
    public Builder newBuilder() {
      return new ValueVector.Builder();
    }

    @Override
    public Builder newBuilder(int size) {
      return new ValueVector.Builder(size);
    }


    @Override
    public Class<?> getDataClass() {
      return Object.class;
    }

    @Override
    public boolean isNA(Object value) {
      return value == null || value == Bit.NA;
    }

    @Override
    public int compare(int a, Vector va, int b, Vector ba) {
      throw new UnsupportedOperationException("Can't compare values for variable vectors");
    }

    @Override
    public Scale getScale() {
      throw new UnsupportedOperationException("VariableVector does not have a scale.");
    }

    @Override
    public String toString() {
      return "variable";
    }
  };

  /**
   * {@inheritDoc}
   */
  @Override
  default VectorType getType() {
    return TYPE;
  }
}
