/**
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
package org.briljantframework.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.briljantframework.Check;
import org.briljantframework.data.index.ObjectComparator;

/**
 * @author Isak Karlsson <isak-kar@dsv.su.se>
 */
public class LevelComparator implements Comparator<Object> {

  private final List<Comparator<Object>> orders = new ArrayList<>();

  public LevelComparator(List<SortOrder> orders) {
    // for (SortOrder order : orders) {
    // this.orders.add(order.orderComparator(ObjectComparator.getInstance()));
    // }
  }

  public static LevelComparator of(SortOrder... orders) {
    return new LevelComparator(Arrays.asList(orders));
  }

  @Override
  public int compare(Object a, Object b) {
    if (a instanceof List && b instanceof List) {
      List<?> al = (List<?>) a;
      List<?> bl = (List<?>) b;
      Check.argument(al.size() == bl.size(), "different levels");
      for (int i = 0; i < al.size() - 1; i++) {
        int c = ObjectComparator.getInstance().compare(al.get(i), bl.get(i));
        if (c != 0) {
          return c;
        }
      }
      return ObjectComparator.getInstance().compare(al.get(al.size() - 1), bl.get(bl.size() - 1));
    } else {
      return ObjectComparator.getInstance().compare(a, b);
    }
  }
}
