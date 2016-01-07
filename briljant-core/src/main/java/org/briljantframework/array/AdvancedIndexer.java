package org.briljantframework.array;

import java.util.ArrayList;
import java.util.List;

import org.briljantframework.Check;
import org.briljantframework.primitive.IntList;

/**
 * An advanced indexer holds {@code n} int arrays of the same {@link #getShape() shape} used for
 * indexing index an array with {@code n} dimensions.
 *
 * For example, {@code IntArray.zeros(2, 2, 2).get(i.getIndex(0).get(0), i.getIndex(1).get(0),
 * i.getIndex(2).get(0))} .
 *
 * @author Isak Karlsson <isak-kar@dsv.su.se>
 */
public class AdvancedIndexer {

  private final IntArray[] index;
  private final int[] shape;

  private AdvancedIndexer(IntArray[] index, int[] shape) {
    this.index = index;
    this.shape = shape;
  }

  /**
   * Returns an advanced indexer using the given arrays
   *
   * @param array the array to index
   * @param arrays the arrays used for indexing
   * @return a new indexer if advanced indexing is required; null otherwise (if all arrays are basic
   *         indexers)
   */
  public static AdvancedIndexer getIndexer(BaseArray<?> array, List<? extends IntArray> arrays) {
    if (arrays.stream().allMatch(AdvancedIndexer::isBasicIndexer)) {
      return null;
    } else {
      int[] shape = array.getShape();
      List<IntArray> advancedIndexes = new ArrayList<>();
      boolean hasBasicIndexGap = false;
      int ndims = array.dims();
      IntArray[] indexers = new IntArray[ndims];
      int firstAdvancedIndex = -1;
      for (int i = 0; i < ndims; i++) {
        if (i < arrays.size()) {
          IntArray index = arrays.get(i);
          Check.argument(index != null, "indexer is required.");
          if (!isBasicIndexer(index) && firstAdvancedIndex > 0 && i > 0) {
            hasBasicIndexGap = true;
          }

          if (!isBasicIndexer(index)) {
            advancedIndexes.add(index);
            if (firstAdvancedIndex == -1) {
              firstAdvancedIndex = i;
            }
          }
          indexers[i] = index == BasicIndex.ALL ? Arrays.range(shape[i]) : index;
        } else {
          // include everything from additional dimensions not covered by the index
          indexers[i] = Arrays.range(shape[i]);
        }
      }

      // Broadcast all advanced indicies to the same shape
      advancedIndexes = Arrays.broadcastArrays(advancedIndexes);
      int[] broadcastShape = advancedIndexes.get(0).getShape();
      IntList dims = new IntList();
      if (hasBasicIndexGap) {
        // if we cannot correctly place the sub-space defined by the advanced
        // indexer, we place it first.
        dims.addAll(broadcastShape);
        for (IntArray index : indexers) {
          if (isBasicIndexer(index)) {
            dims.add(index.size());
          }
        }
      } else {
        // if we can place the index correctly, we place it at the position of
        // the first advanced index
        for (int i = 0; i < ndims; i++) {
          // place the advanced index at the appropriate position
          if (firstAdvancedIndex == i) {
            dims.addAll(broadcastShape);
          } else {
            IntArray index = indexers[i];
            if (isBasicIndexer(index)) {
              dims.add(index.size());
            }
          }
        }
      }

      int[] newShape = dims.toPrimitiveArray();

      // index arrays broadcast to the shape of the
      IntArray[] indexArrays = new IntArray[ndims];

      if (hasBasicIndexGap) {
        // if we have a gap, insert the advanced indexer shape first and then the
        // basic indexers
        int[] compatibleShape = new int[newShape.length];
        int shapeLocation = broadcastShape.length;
        for (int i = 0; i < ndims; i++) {
          java.util.Arrays.fill(compatibleShape, 1);
          IntArray index = indexers[i];
          if (isBasicIndexer(index)) {
            compatibleShape[shapeLocation] = index.size();
            shapeLocation++;
          } else {
            System.arraycopy(broadcastShape, 0, compatibleShape, 0, broadcastShape.length);
          }
          indexArrays[i] = broadcastCompatible(index, compatibleShape, newShape);
        }
      } else {
        int[] compatibleShape = new int[newShape.length];
        int noSeenAdvanced = 0;
        for (int i = 0; i < ndims; i++) {
          java.util.Arrays.fill(compatibleShape, 1);
          IntArray index = indexers[i];
          if (isBasicIndexer(index)) {
            if (i < firstAdvancedIndex) {
              compatibleShape[i] = index.size();
            } else {
              compatibleShape[i + broadcastShape.length - noSeenAdvanced] = index.size();
            }
          } else {
            for (int k = 0; k < index.dims(); k++) {
              compatibleShape[firstAdvancedIndex + k] = index.size(k);
            }
            noSeenAdvanced++;
          }
          indexArrays[i] = broadcastCompatible(index, compatibleShape, newShape);
        }
      }

      return new AdvancedIndexer(indexArrays, newShape);
    }
  }

  private static IntArray broadcastCompatible(IntArray i, int[] compatibleShape, int[] newShape) {
    return Arrays.broadcastTo(i.reshape(compatibleShape), newShape);
  }

  private static boolean isBasicIndexer(IntArray indexer) {
    return (indexer instanceof Range && indexer.dims() == 1) || indexer == BasicIndex.ALL;
  }

  public IntArray getIndex(int i) {
    return index[i];
  }

  public IntArray[] getIndex() {
    return index;
  }

  public int[] getShape() {
    return shape;
  }
}
