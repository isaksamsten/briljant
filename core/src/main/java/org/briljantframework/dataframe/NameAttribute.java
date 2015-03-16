package org.briljantframework.dataframe;

import com.google.common.collect.UnmodifiableIterator;

import com.carrotsearch.hppc.IntObjectMap;
import com.carrotsearch.hppc.IntObjectOpenHashMap;
import com.carrotsearch.hppc.ObjectIntMap;
import com.carrotsearch.hppc.ObjectIntOpenHashMap;
import com.carrotsearch.hppc.cursors.IntObjectCursor;

import org.briljantframework.Utils;

import java.util.AbstractCollection;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Created by Isak Karlsson on 07/01/15.
 */
public class NameAttribute extends AbstractCollection<String> implements
                                                              AttributeCollection<String> {

  private IntObjectMap<String> names;
  private ObjectIntMap<String> reverse;

  public NameAttribute(NameAttribute names) {
    this.names = new IntObjectOpenHashMap<>(names.names);
    this.reverse = new ObjectIntOpenHashMap<>(names.reverse);
  }

  public NameAttribute(String... names) {
    this(Arrays.asList(names));
  }

  public NameAttribute(List<String> names) {
    this.names = new IntObjectOpenHashMap<>();
    this.reverse = new ObjectIntOpenHashMap<>();
    for (int i = 0; i < names.size(); i++) {
      String name = names.get(i);
      this.names.put(i, name);
      this.reverse.put(name, i);
    }
  }

  public <X extends Throwable> int getOrThrow(String name, Supplier<X> e) throws X {
    int value = reverse.getOrDefault(name, -1);
    if (value == -1) {
      throw e.get();
    } else {
      return value;
    }
  }

  public void putAll(NameAttribute other) {
    for (IntObjectCursor<String> name : other.names) {
      put(name.index, name.value);
    }
  }

  @Override
  public String get(int index) {
    return names.get(index);
  }

  @Override
  public String getOrDefault(int index, Supplier<String> defaultValue) {
    String name = names.get(index);
    return name != null ? name : defaultValue.get();
  }

  @Override
  public Set<Map.Entry<Integer, String>> entrySet() {
    return new AbstractSet<Map.Entry<Integer, String>>() {
      Iterator<IntObjectCursor<String>> it = names.iterator();


      @Override
      public Iterator<Map.Entry<Integer, String>> iterator() {
        return new UnmodifiableIterator<Map.Entry<Integer, String>>() {
          @Override
          public boolean hasNext() {
            return it.hasNext();
          }

          @Override
          public Map.Entry<Integer, String> next() {
            IntObjectCursor<String> c = it.next();
            return new AbstractMap.SimpleImmutableEntry<>(c.key, c.value);
          }
        };
      }

      @Override
      public int size() {
        return names.size();
      }
    };

  }

  @Override
  public boolean containsKey(int index) {
    return names.containsKey(index);
  }

  @Override
  public void put(int index, String value) {
    names.put(index, value);
    reverse.put(value, index);
  }

  @Override
  public void remove(int index) {
    IntObjectMap<String> names = new IntObjectOpenHashMap<>();
    ObjectIntMap<String> reverse = new ObjectIntOpenHashMap<>();
    for (IntObjectCursor<String> name : this.names) {
      if (name.key != index) {
        if (name.key > index) {
          names.put(name.key - 1, name.value);
          reverse.put(name.value, name.key - 1);
        } else {
          names.put(name.key, name.value);
          reverse.put(name.value, name.key);
        }
      }
    }
    this.names = names;
    this.reverse = reverse;
  }

  @Override
  public void swap(int a, int b) {
    reverse.put(get(a), b);
    reverse.put(get(b), a);
    Utils.swap(names, a, b);
  }

  public void ifPresent(int i, Consumer<String> consumer) {
    String v = getOrDefault(i, () -> null);
    if (v != null) {
      consumer.accept(v);
    }
  }

  public void putFromIfPresent(int in, NameAttribute columnNames, int from) {
    if (columnNames.containsKey(from)) {
      String value = columnNames.get(from);
      this.put(in, value);
    }
  }

  @Override
  public Iterator<String> iterator() {
    return new UnmodifiableIterator<String>() {
      private int current = 0;

      @Override
      public boolean hasNext() {
        return current < size();
      }

      @Override
      public String next() {
        return get(current++);
      }
    };
  }

  @Override
  public int size() {
    return names.size();
  }

  @Override
  public String toString() {
    return names.toString();
  }


}
