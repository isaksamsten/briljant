/*
 * ADEB - machine learning pipelines made easy
 * Copyright (C) 2014  Isak Karlsson
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package org.briljantframework.learning.tree;

import org.briljantframework.dataframe.DataFrame;
import org.briljantframework.learning.example.Example;
import org.briljantframework.learning.example.Examples;
import org.briljantframework.vector.Type;
import org.briljantframework.vector.Value;
import org.briljantframework.vector.Vector;

import java.util.Random;

/**
 * Created by Isak Karlsson on 10/09/14.
 */
public abstract class AbstractSplitter implements Splitter<ValueThreshold> {

    /**
     * The Random.
     */
    protected final Random random = new Random();

    /**
     * Basic implementation of the splitting procedure
     *
     * @param examples  the examples
     * @param axis      the axis
     * @param threshold the threshold
     * @return the examples . split
     */
    protected Tree.Split<ValueThreshold> split(DataFrame dataset, Examples examples, int axis, Value threshold) {
        Examples left = Examples.create();
        Examples right = Examples.create();
        Vector axisVector = dataset.getColumn(axis);
        Type axisType = axisVector.getType();

        /*
         * Partition every class separately
         */
        for (Examples.Sample sample : examples.samples()) {
            String target = sample.getTarget();

            Examples.Sample leftSample = Examples.Sample.create(target);
            Examples.Sample rightSample = Examples.Sample.create(target);
            Examples.Sample missingSample = Examples.Sample.create(target);

            /*
             * STEP 1: Partition the examples according to threshold
             */
            for (Example example : sample) {
                Value value = axisVector.getAsValue(example.getIndex());
                if (value.isNA()) {
                    missingSample.add(example);
                } else if (threshold.compareTo(value) <= 0) {
                    leftSample.add(example);
                } else {
                    rightSample.add(example);
                }
            }

            /*
             * STEP 2: Distribute examples with missing values
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

        return new Tree.Split<>(left, right, ValueThreshold.create(axis, threshold));
    }

    /**
     * Distribute missing values (this should be an injected dependency)
     *
     * @param left    the left
     * @param right   the right
     * @param missing the missing
     */
    protected void distributeMissing(Examples.Sample left, Examples.Sample right, Examples.Sample missing) {
        for (Example example : missing) {
            if (random.nextDouble() > 0.5)
                left.add(example);
            else
                right.add(example);
        }
    }


}
