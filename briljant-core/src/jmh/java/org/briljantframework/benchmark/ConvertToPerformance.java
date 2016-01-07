/**
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

import org.briljantframework.data.resolver.Resolve;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.OperationsPerInvocation;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

/**
 * @author Isak Karlsson <isak-kar@dsv.su.se>
 */
@State(Scope.Benchmark)
@OperationsPerInvocation(ConvertToPerformance.OPERATIONS_PER_INVOCATION)
public class ConvertToPerformance {


  public static final int OPERATIONS_PER_INVOCATION = 1000_000;

  //
  // @Benchmark
  // public Object convertToDoublePerformance_IfChain() {
  // Object object = null;
  // for (int i = 0; i < OPERATIONS_PER_INVOCATION; i++) {
  // object = Convert.to(Double.class, i);
  // }
  // return object;
  // }

  @Benchmark
  public Object convertToDoublePerformance_Resolver() {
    Object object = null;
    for (int i = 0; i < OPERATIONS_PER_INVOCATION; i++) {
      object = Resolve.to(Double.class, i);
    }
    return object;
  }

}
