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

package org.briljantframework.learning.ensemble;

import org.briljantframework.learning.example.Examples;

import java.util.Random;

/**
 * Sample n examples with replacement
 * <p>
 * Created by Isak Karlsson on 19/09/14.
 */
public class Bootstrap implements Sampler {

    private final Random random;

    private Bootstrap(Random random) {
        this.random = random;
    }

    /**
     * Create bootstrap.
     *
     * @return the bootstrap
     */
    public static Bootstrap create() {
        return new Bootstrap(new Random());
    }

    /**
     * Create bootstrap.
     *
     * @param seed the seed
     * @return the bootstrap
     */
    public static Bootstrap create(long seed) {
        return new Bootstrap(new Random(seed));
    }

    @Override
    public Examples sample(Examples examples) {
        Examples inBag = Examples.create();
        for (Examples.Sample sample : examples.samples()) {
            Examples.Sample inSample = Examples.Sample.create(sample.getTarget());
            int[] bootstrap = bootstrap(sample);
            for (int i = 0; i < bootstrap.length; i++) {
                if (bootstrap[i] > 0) {
                    inSample.add(sample.get(i).updateWeight(bootstrap[i]));
                }
            }
            inBag.add(inSample);
        }
        return inBag;
    }

    private int[] bootstrap(Examples.Sample sample) {
        int[] bootstrap = new int[sample.size()];
        for (int i = 0; i < bootstrap.length; i++) {
            int index = random.nextInt(bootstrap.length);
            bootstrap[index] += 1;
        }

        return bootstrap;
    }


}
