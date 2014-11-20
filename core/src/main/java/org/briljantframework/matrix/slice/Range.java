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

package org.briljantframework.matrix.slice;

/**
 * Created by isak on 21/06/14.
 */
public class Range implements Slicer {

    private final int min, max, step, length;

    /**
     * Instantiates a new Range.
     *
     * @param min  the min
     * @param max  the max
     * @param step the step
     */
    public Range(int min, int max, int step) {
        if (min >= max && min < 0 || step < min && step > max) {
            throw new IllegalArgumentException("min < max, step < min or min < 0");
        }
        this.min = min;
        this.step = step;
        this.max = max;
        this.length = (max - min) / step;
    }

    /**
     * Exclusive range.
     *
     * @param min the min
     * @param max the max
     * @return the range
     */
    public static Range exclusive(int min, int max) {
        return new Range(min, max, 1);
    }

    /**
     * Exclusive range.
     *
     * @param min  the min
     * @param max  the max
     * @param step the step
     * @return the range
     */
    public static Range exclusive(int min, int max, int step) {
        return new Range(min, max, step);
    }

    /**
     * Inclusive range.
     *
     * @param min the min
     * @param max the max
     * @return the range
     */
    public static Range inclusive(int min, int max) {
        return new Range(min, max + 1, 1);
    }

    @Override
    public int length() {
        return length;
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder("Range(");
        int j = 0;
        for (int i = min; i < max; i += step) {
            if (j++ > 10) {
                b.append("...,");
                break;
            }
            b.append(i);
            b.append(',');
        }
        b.replace(b.length() - 1, b.length(), "");
        b.append(")");

        return b.toString();
    }

    @Override
    public Slice getSlice() {
        return new Slice() {
            private int current = Range.this.min;

            @Override
            public void rewind() {
                this.current = Range.this.min;
            }

            @Override
            public boolean hasNext(int max) {
                return current < Range.this.max && current < max;
            }

            @Override
            public int current() {
                return current;
            }

            @Override
            public int next() {
                int temp = current;
                current += Range.this.step;
                return temp;
            }
        };
    }
}
