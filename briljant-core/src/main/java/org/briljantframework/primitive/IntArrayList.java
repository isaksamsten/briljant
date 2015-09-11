/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Isak Karlsson
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.briljantframework.primitive;

import net.mintern.primitive.Primitive;

import java.util.AbstractList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * ArrayList backed by a primitive List
 */
public class IntArrayList extends AbstractList<Integer> {

  /**
   * Default initial capacity.
   */
  private static final int DEFAULT_CAPACITY = 10;

  /**
   * Shared empty array instance used for empty instances.
   */
  private static final int[] EMPTY_ELEMENTDATA = {};

  /**
   * Shared empty array instance used for default sized empty instances. We
   * distinguish this from EMPTY_ELEMENTDATA to know how much to inflate when
   * first element is added.
   */
  private static final int[] DEFAULTCAPACITY_EMPTY_ELEMENTDATA = {};

  public int[] elementData = new int[10];
  private int size = 0;

  public IntArrayList(IntArrayList list) {
    this.elementData = Arrays.copyOf(list.elementData, list.size());
    this.size = elementData.length;
  }

  public IntArrayList() {

  }

  @Override
  public boolean add(Integer e) {
    return add((int) e);
  }

  public boolean add(int e) {
    ensureCapacityInternal(size + 1);
    elementData[size++] = e;
    return true;
  }

  /**
   * Removes the element at the specified position in this list.
   * Shifts any subsequent elements to the left (subtracts one from their
   * indices).
   *
   * @param index the index of the element to be removed
   * @return the element that was removed from the list
   * @throws IndexOutOfBoundsException {@inheritDoc}
   */
  public Integer remove(int index) {

    modCount++;
    int oldValue = elementData[index];

    int numMoved = size - index - 1;
    if (numMoved > 0) {
      System.arraycopy(elementData, index + 1, elementData, index,
                       numMoved);
    }
    elementData[--size] = -1; // clear to let GC do its work

    return oldValue;
  }

  private void ensureCapacityInternal(int minCapacity) {
    if (elementData == DEFAULTCAPACITY_EMPTY_ELEMENTDATA) {
      minCapacity = Math.max(DEFAULT_CAPACITY, minCapacity);
    }

    ensureExplicitCapacity(minCapacity);
  }

  private void ensureExplicitCapacity(int minCapacity) {
    modCount++;

    // overflow-conscious code
    if (minCapacity - elementData.length > 0) {
      grow(minCapacity);
    }
  }

  /**
   * The maximum size of array to allocate.
   * Some VMs reserve some header words in an array.
   * Attempts to allocate larger arrays may result in
   * OutOfMemoryError: Requested array size exceeds VM limit
   */
  private static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;

  /**
   * Increases the capacity to ensure that it can hold at least the
   * number of elements specified by the minimum capacity argument.
   *
   * @param minCapacity the desired minimum capacity
   */
  private void grow(int minCapacity) {
    // overflow-conscious code
    int oldCapacity = elementData.length;
    int newCapacity = oldCapacity + (oldCapacity >> 1);
    if (newCapacity - minCapacity < 0) {
      newCapacity = minCapacity;
    }
    if (newCapacity - MAX_ARRAY_SIZE > 0) {
      newCapacity = hugeCapacity(minCapacity);
    }
    // minCapacity is usually close to size, so this is a win:
    elementData = Arrays.copyOf(elementData, newCapacity);
  }

  private static int hugeCapacity(int minCapacity) {
    if (minCapacity < 0) // overflow
    {
      throw new OutOfMemoryError();
    }
    return (minCapacity > MAX_ARRAY_SIZE) ?
           Integer.MAX_VALUE :
           MAX_ARRAY_SIZE;
  }

  @Override
  public Integer set(int index, Integer element) {
    int oldValue = elementData[index];
    elementData[index] = element;
    return oldValue;
  }

  public Integer set(int index, int element) {
    int oldValue = elementData[index];
    elementData[index] = element;
    return oldValue;
  }

  @Override
  public Integer get(int index) {
    return elementData[index];
  }

  @Override
  public int size() {
    return size;
  }

  /**
   * Returns a list iterator over the elements in this list (in proper
   * sequence), starting at the specified position in the list.
   * The specified index indicates the first element that would be
   * returned by an initial call to {@link java.util.ListIterator#next next}.
   * An initial call to {@link java.util.ListIterator#previous previous} would
   * return the element with the specified index minus one.
   *
   * <p>The returned list iterator is <a href="#fail-fast"><i>fail-fast</i></a>.
   *
   * @throws IndexOutOfBoundsException {@inheritDoc}
   */
  public ListIterator<Integer> listIterator(int index) {
    if (index < 0 || index > size) {
      throw new IndexOutOfBoundsException("Index: " + index);
    }
    return new ListItr(index);
  }

  /**
   * Returns a list iterator over the elements in this list (in proper
   * sequence).
   *
   * <p>The returned list iterator is <a href="#fail-fast"><i>fail-fast</i></a>.
   *
   * @see #listIterator(int)
   */
  public ListIterator<Integer> listIterator() {
    return new ListItr(0);
  }

  /**
   * Returns an iterator over the elements in this list in proper sequence.
   *
   * <p>The returned iterator is <a href="#fail-fast"><i>fail-fast</i></a>.
   *
   * @return an iterator over the elements in this list in proper sequence
   */
  public Iterator<Integer> iterator() {
    return new Itr();
  }

  @Override
  public void sort(Comparator<? super Integer> c) {
    Primitive.sort(elementData, 0, size, c::compare);
  }

  /**
   * An optimized version of AbstractList.Itr
   */
  private class Itr implements Iterator<Integer> {

    int cursor;       // index of next element to return
    int lastRet = -1; // index of last element returned; -1 if no such
    int expectedModCount = modCount;

    public boolean hasNext() {
      return cursor != size;
    }

    @SuppressWarnings("unchecked")
    public Integer next() {
      checkForComodification();
      int i = cursor;
      if (i >= size) {
        throw new NoSuchElementException();
      }
      int[] elementData = IntArrayList.this.elementData;
      if (i >= elementData.length) {
        throw new ConcurrentModificationException();
      }
      cursor = i + 1;
      return elementData[lastRet = i];
    }

    public void remove() {
      if (lastRet < 0) {
        throw new IllegalStateException();
      }
      checkForComodification();

      try {
        IntArrayList.this.remove(lastRet);
        cursor = lastRet;
        lastRet = -1;
        expectedModCount = modCount;
      } catch (IndexOutOfBoundsException ex) {
        throw new ConcurrentModificationException();
      }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void forEachRemaining(Consumer<? super Integer> consumer) {
      Objects.requireNonNull(consumer);
      final int size = IntArrayList.this.size;
      int i = cursor;
      if (i >= size) {
        return;
      }
      final int[] elementData = IntArrayList.this.elementData;
      if (i >= elementData.length) {
        throw new ConcurrentModificationException();
      }
      while (i != size && modCount == expectedModCount) {
        consumer.accept(elementData[i++]);
      }
      // update once at end of iteration to reduce heap write traffic
      cursor = i;
      lastRet = i - 1;
      checkForComodification();
    }

    final void checkForComodification() {
      if (modCount != expectedModCount) {
        throw new ConcurrentModificationException();
      }
    }
  }

  /**
   * An optimized version of AbstractList.ListItr
   */
  private class ListItr extends Itr implements ListIterator<Integer> {

    ListItr(int index) {
      super();
      cursor = index;
    }

    public boolean hasPrevious() {
      return cursor != 0;
    }

    public int nextIndex() {
      return cursor;
    }

    public int previousIndex() {
      return cursor - 1;
    }

    @SuppressWarnings("unchecked")
    public Integer previous() {
      checkForComodification();
      int i = cursor - 1;
      if (i < 0) {
        throw new NoSuchElementException();
      }
      int[] elementData = IntArrayList.this.elementData;
      if (i >= elementData.length) {
        throw new ConcurrentModificationException();
      }
      cursor = i;
      return elementData[lastRet = i];
    }

    public void set(Integer e) {
      if (lastRet < 0) {
        throw new IllegalStateException();
      }
      checkForComodification();

      try {
        IntArrayList.this.set(lastRet, e);
      } catch (IndexOutOfBoundsException ex) {
        throw new ConcurrentModificationException();
      }
    }

    public void add(Integer e) {
      checkForComodification();

      try {
        int i = cursor;
        IntArrayList.this.add(i, e);
        cursor = i + 1;
        lastRet = -1;
        expectedModCount = modCount;
      } catch (IndexOutOfBoundsException ex) {
        throw new ConcurrentModificationException();
      }
    }
  }
}
