package org.briljantframework.matrix;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * @author Isak Karlsson
 */
public interface BitMatrix extends Matrix<BitMatrix>, Iterable<Boolean> {

  BitMatrix assign(Supplier<Boolean> supplier);

  BitMatrix assign(boolean value);

  void set(int i, int j, boolean value);

  void set(int index, boolean value);

  boolean get(int i, int j);

  boolean get(int index);

  BitMatrix xor(BitMatrix other);

  BitMatrix or(BitMatrix other);

  BitMatrix orNot(BitMatrix other);

  BitMatrix and(BitMatrix other);

  BitMatrix andNot(BitMatrix other);

  BitMatrix not();

  Stream<Boolean> stream();

  List<Boolean> asList();
}
