/*
 * The MIT License (MIT)
 * 
 * Copyright (c) 2015 Isak Karlsson
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package org.briljantframework.benchmark;

import java.util.ArrayList;
import java.util.List;

import org.briljantframework.data.vector.Vector;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

/**
 * Created by isak on 09/09/15.
 */
@State(Scope.Benchmark)
public class VectorBuilderAddPerformance {

  private static final int iterations = 10000;

  @Benchmark
  public Object intBuilderAdd() {
    return builderAddInt(Vector.Builder.of(Integer.class));
  }

  @Benchmark
  public Object doubleBuilderAdd() {
    return builderAddInt(Vector.Builder.of(Double.class));
  }

  @Benchmark
  public Object genericBuilderAddDouble() {
    return builderAddDouble(Vector.Builder.of(String.class));
  }

  @Benchmark
  public Object genericBuilderAddInt() {
    return builderAddInt(Vector.Builder.of(String.class));
  }

  @Benchmark
  public Object listAddDouble() {
    return listAddDouble(new ArrayList<>());
  }

  @Benchmark
  public Object listAddInteger() {
    return listAddInteger(new ArrayList<>());
  }

  private static Object listAddInteger(List<Integer> list) {
    for (int i = 0; i < iterations; i++) {
      list.add(i + 100);
    }
    return list;
  }

  private static Object listAddDouble(List<Double> list) {
    for (int i = 0; i < iterations; i++) {
      list.add(i + 100.0);
    }
    return list;
  }

  private static Object builderAddDouble(Vector.Builder builder) {
    for (int i = 0; i < iterations; i++) {
      builder.add(i + 100.0);
    }
    return builder;
  }

  private static Object builderAddInt(Vector.Builder builder) {
    for (int i = 0; i < iterations; i++) {
      builder.add(i + 100);
    }
    return builder;
  }
}
