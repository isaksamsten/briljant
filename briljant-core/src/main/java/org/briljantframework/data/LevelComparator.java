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
