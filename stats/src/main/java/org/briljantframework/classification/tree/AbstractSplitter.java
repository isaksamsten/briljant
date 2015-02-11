/*
 * ADEB - machine learning pipelines made easy Copyright (C) 2014 Isak Karlsson
 * 
 * This program is free software; you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation; either version 2 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with this program; if
 * not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301 USA.
 */

package org.briljantframework.classification.tree;

import java.util.Random;

import org.briljantframework.dataframe.DataFrame;
import org.briljantframework.vector.Value;
import org.briljantframework.vector.Vector;
import org.briljantframework.vector.VectorType;

/**
 * Created by Isak Karlsson on 10/09/14.
 */
public abstract class AbstractSplitter implements Splitter {

  public static final int MISSING = 0;
  public static final int LEFT = -1;
  public static final int RIGHT = 1;
  /**
   * The Random.
   */
  protected final Random random = new Random();

  /**
   * Basic implementation of the splitting procedure
   *
   * @param classSet the examples
   * @param axis the axis
   * @param threshold the threshold
   * @return the examples . split
   */
  protected TreeSplit<ValueThreshold> split(DataFrame dataset, ClassSet classSet, int axis,
      Value threshold) {
    ClassSet left = ClassSet.create();
    ClassSet right = ClassSet.create();
    Vector axisVector = dataset.getColumn(axis);
    VectorType axisType = axisVector.getType();

    /*
     * Partition every class separately
     */
    for (ClassSet.Sample sample : classSet.samples()) {
      String target = sample.getTarget();

      ClassSet.Sample leftSample = ClassSet.Sample.create(target);
      ClassSet.Sample rightSample = ClassSet.Sample.create(target);
      ClassSet.Sample missingSample = ClassSet.Sample.create(target);

      /*
       * STEP 1: Partition the examples according to threshold
       */
      for (Example example : sample) {
        Value value = axisVector.getAsValue(example.getIndex());
        int direction = MISSING;
        switch (axisType.getScale()) {
          case CATEGORICAL:
            direction = axisType.equals(threshold, value) ? LEFT : RIGHT;
            break;
          case NUMERICAL:
            direction = axisType.compare(threshold, value) <= 0 ? LEFT : RIGHT;
            break;
        }

        switch (direction) {
          case LEFT:
            leftSample.add(example);
            break;
          case RIGHT:
            rightSample.add(example);
            break;
          case MISSING:
          default:
            missingSample.add(example);
        }
        // if (value.isNA()) {
        // missingSample.add(example);
        // } else if (threshold.compareTo(value) <= 0) {
        // leftSample.add(example);
        // } else {
        // rightSample.add(example);
        // }
      }

      /*
       * STEP 2: Distribute examples with missing getPosteriorProbabilities
       */
      distributeMissing(leftSample, rightSample, missingSample);

      /*
       * STEP 3: Ignore classes with no examples in the partition
       */
      if (!leftSample.isEmpty()) {
        left.add(leftSample);
      }
      if (!rightSample.isEmpty()) {
        right.add(rightSample);
      }
    }

    return new TreeSplit<>(left, right, ValueThreshold.create(axis, threshold));
  }

  /**
   * Distribute missing getPosteriorProbabilities (this should be an injected dependency)
   *
   * @param left the left
   * @param right the right
   * @param missing the missing
   */
  protected void distributeMissing(ClassSet.Sample left, ClassSet.Sample right,
      ClassSet.Sample missing) {
    for (Example example : missing) {
      if (random.nextDouble() > 0.5)
        left.add(example);
      else
        right.add(example);
    }
  }


}
