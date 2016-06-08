package org.briljantframework.data.series;

/**
 * Created by isak on 08/06/16.
 */
final class InferenceType extends Type {

  @Override
  public Series.Builder newBuilder() {
    return new TypeInferenceBuilder();
  }

  @Override
  public Series.Builder newBuilderWithCapacity(int capacity) {
    return new TypeInferenceBuilder();
  }

  @Override
  public Series.Builder newBuilder(int size) {
    TypeInferenceBuilder builder = new TypeInferenceBuilder();
    for (int i = 0; i < size; i++) {
      builder.addNA();
    }
    return builder;
  }

  @Override
  public Class<?> getDataClass() {
    return Object.class;
  }
}
