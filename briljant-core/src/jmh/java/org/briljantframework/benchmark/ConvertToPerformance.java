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
