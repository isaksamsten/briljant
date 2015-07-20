package org.briljantframework.array;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * @author Isak Karlsson
 */
public interface BitArray extends Array<BitArray>, Iterable<Boolean> {

  BitArray assign(Supplier<Boolean> supplier);

  BitArray assign(boolean value);

  void set(int i, int j, boolean value);

  void set(int index, boolean value);

  boolean get(int i, int j);

  boolean get(int index);

  BitArray xor(BitArray other);

  BitArray or(BitArray other);

  BitArray orNot(BitArray other);

  BitArray and(BitArray other);

  BitArray andNot(BitArray other);

  BitArray not();

  Stream<Boolean> stream();

  List<Boolean> asList();
}
