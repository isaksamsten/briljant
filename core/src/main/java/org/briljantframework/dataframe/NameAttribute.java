package org.briljantframework.dataframe;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

import org.briljantframework.Utils;

import com.carrotsearch.hppc.IntObjectMap;
import com.carrotsearch.hppc.IntObjectOpenHashMap;
import com.carrotsearch.hppc.cursors.IntObjectCursor;

/**
 * Created by Isak Karlsson on 07/01/15.
 */
public class NameAttribute implements AttributeCollection<String> {

  private IntObjectMap<String> names;

  public NameAttribute(NameAttribute names) {
    this.names = new IntObjectOpenHashMap<>(names.names);
  }

  public NameAttribute(String... names) {
    this(Arrays.asList(names));
  }

  public NameAttribute(List<String> names) {
    this.names = new IntObjectOpenHashMap<>();
    for (int i = 0; i < names.size(); i++) {
      this.names.put(i, names.get(i));
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
  public void put(int index, String value) {
    names.put(index, value);
  }

  @Override
  public void remove(int index) {
    IntObjectMap<String> names = new IntObjectOpenHashMap<>();
    for (IntObjectCursor<String> name : this.names) {
      if (name.key != index) {
        if (name.key > index) {
          names.put(name.key - 1, name.value);
        } else {
          names.put(name.key, name.value);
        }
      }
    }
    this.names = names;
  }

  @Override
  public void swap(int a, int b) {
    Utils.swap(names, a, b);
  }

  @Override
  public String toString() {
    return names.toString();
  }
}
