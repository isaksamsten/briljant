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

package org.briljantframework.matrix;

import com.google.common.base.Preconditions;

import java.util.stream.DoubleStream;

/**
 * Created by Isak Karlsson on 01/09/14.
 */
public class Scalars {

    /**
     * Min double.
     *
     * @param a the a
     * @param b the b
     * @return the double
     */
    public static double min(double a, double b) {
        return Math.min(a, b);
    }

    /**
     * Min double.
     *
     * @param a the a
     * @param b the b
     * @param c the c
     * @return the double
     */
    public static double min(double a, double b, double c) {
        return Math.min(a, Math.min(b, c));
    }

    /**
     * Min double.
     *
     * @param args the args
     * @return the double
     */
    public static double min(double... args) {
        Preconditions.checkArgument(args.length > 0);
        return DoubleStream.of(args).min().getAsDouble();
    }

}
