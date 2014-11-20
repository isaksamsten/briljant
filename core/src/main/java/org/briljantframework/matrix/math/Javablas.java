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

package org.briljantframework.matrix.math;

import org.briljantframework.matrix.MismatchException;

/**
 * Created by Isak on 7/4/2014.
 */
public class Javablas {

    /**
     * Abs void.
     *
     * @param in  the in
     * @param out the out
     */
    public static void abs(double[] in, double[] out) {
        if (in.length != out.length) {
            throw new MismatchException("abs", String.format("%d != %d", in.length, out.length));
        }
        for (int i = 0; i < in.length; i++) {
            out[i] = Math.abs(in[i]);
        }
    }

    /**
     * Divide a_i with b_i scaling with alpha and beta respectively, storing the result in c
     * <p>
     * <pre>
     *     Javablas.multiply(a, 10, b, 10, c);
     * </pre>
     *
     * @param a     array
     * @param alpha scale of a
     * @param b     array
     * @param beta  scale of b
     * @param c     resulting array
     * @throws ArithmeticException the arithmetic exception
     */
    public static void div(double[] a, double alpha, double[] b, double beta, double[] c) throws ArithmeticException {
        if (a.length != b.length && c.length != a.length) {
            throw new IllegalArgumentException();
        }

        if (alpha == 1 && beta == 1) {
            for (int i = 0; i < a.length; i++) {
                c[i] = a[i] / b[i];
            }
        } else {
            if (alpha == 1) {
                for (int i = 0; i < a.length; i++) {
                    c[i] = a[i] / beta * b[i];
                }
            } else if (beta == 1) {
                for (int i = 0; i < a.length; i++) {
                    c[i] = alpha / a[i] * b[i];
                }
            } else {
                for (int i = 0; i < a.length; i++) {
                    c[i] = alpha / a[i] * beta * b[i];
                }
            }
        }
    }

    /**
     * Multiply every element in a with alpha, storing the result in c
     * <p>
     * <pre>
     *     Javablas.multiply(array, 10, array);
     * </pre>
     *
     * @param a     array
     * @param alpha scale
     * @param c     array scaled with alpha
     */
    public static void mul(double[] a, double alpha, double[] c) {
        for (int i = 0; i < a.length; i++) {
            c[i] = a[i] * alpha;
        }
    }

    /**
     * Divide every in a with alpha, storing the result in c
     * <p>
     * <pre>
     *     Javablas.divide(array, 10, array);
     * </pre>
     *
     * @param a     array
     * @param alpha term
     * @param c     array with term added
     */
    public static void div(double[] a, double alpha, double[] c) {
        if (alpha == 0.0) {
            throw new ArithmeticException("division by zero");
        }
        for (int i = 0; i < a.length; i++) {
            c[i] = a[i] / alpha;
        }
    }

    /**
     * Divide alpha by every element in a, storing the result in c
     * <p>
     * <pre>
     *     Javablas.divide(10, array, array);
     * </pre>
     *
     * @param alpha term
     * @param a     array
     * @param c     array with term added
     */
    public static void div(double alpha, double[] a, double[] c) {
        for (int i = 0; i < a.length; i++) {
            c[i] = alpha / a[i];
        }
    }

    /**
     * Add alpha to every element in a, storing the result in c
     * <p>
     * <pre>
     *     Javablas.add(array, 10, array);
     * </pre>
     *
     * @param a     array
     * @param alpha term
     * @param c     array with term added
     */
    public static void add(double[] a, double alpha, double[] c) {
        for (int i = 0; i < a.length; i++) {
            c[i] = a[i] + alpha;
        }
    }

    /**
     * Subtract alpha from every element in a, storing the result in c
     * <p>
     * <pre>
     *     Javablas.subtract(array, 10, array);
     * </pre>
     *
     * @param a     array
     * @param alpha term
     * @param c     array with term added
     */
    public static void sub(double[] a, double alpha, double[] c) {
        for (int i = 0; i < a.length; i++) {
            c[i] = a[i] - alpha;
        }
    }

    /**
     * Subtract void.
     *
     * @param alpha the alpha
     * @param a     the a
     * @param c     the c
     */
    public static void sub(double alpha, double[] a, double[] c) {
        for (int i = 0; i < a.length; i++) {
            c[i] = alpha - a[i];
        }
    }

    /**
     * Multiply a_i,j with b_i,j scaling with alpha and beta respectively, storing the result in c
     * <p>
     * <pre>
     *     Javablas.multiply(a, 10, b, 10, c);
     * </pre>
     *
     * @param a     array
     * @param alpha scale of a
     * @param b     array
     * @param beta  scale of b
     * @param c     resulting array
     */
    public static void mul(double[] a, double alpha, double[] b, double beta, double[] c) {
        if (a.length != b.length && c.length != a.length) {
            throw new IllegalArgumentException();
        }

        if (alpha == 1 && beta == 1) {
            for (int i = 0; i < a.length; i++) {
                c[i] = a[i] * b[i];
            }
        } else {
            if (alpha == 1) {
                for (int i = 0; i < a.length; i++) {
                    c[i] = a[i] * beta * b[i];
                }
            } else if (beta == 1) {
                for (int i = 0; i < a.length; i++) {
                    c[i] = alpha * a[i] * b[i];
                }
            } else {
                for (int i = 0; i < a.length; i++) {
                    c[i] = alpha * a[i] * beta * b[i];
                }
            }
        }
    }

    /**
     * Add a_i to b_i scaling with alpha and beta respectively, storing the result in c
     *
     * @param a     array
     * @param alpha scale of a
     * @param b     array
     * @param beta  scale of b
     * @param c     resulting array
     */
    public static void add(double[] a, double alpha, double[] b, double beta, double[] c) {
        if (a.length != b.length && c.length != a.length) {
            throw new IllegalArgumentException();
        }

        if (alpha == 1 && beta == 1) {
            for (int i = 0; i < a.length; i++) {
                c[i] = a[i] + b[i];
            }
        } else {
            if (alpha == 1) {
                for (int i = 0; i < a.length; i++) {
                    c[i] = a[i] + (beta * b[i]);
                }
            } else if (beta == 1) {
                for (int i = 0; i < a.length; i++) {
                    c[i] = (alpha * a[i]) + b[i];
                }
            } else {
                for (int i = 0; i < a.length; i++) {
                    c[i] = (alpha * a[i]) + (beta * b[i]);
                }
            }
        }
    }

    /**
     * Subtract b_i from a_i scaling with alpha and beta respectively, storing the result in c
     *
     * @param a     array
     * @param alpha scale of a
     * @param b     array
     * @param beta  scale of b
     * @param c     resulting array
     */
    public static void sub(double[] a, double alpha, double[] b, double beta, double[] c) {
        if (a.length != b.length && c.length != a.length) {
            throw new IllegalArgumentException();
        }

        if (alpha == 1 && beta == 1) {
            for (int i = 0; i < a.length; i++) {
                c[i] = a[i] - b[i];
            }
        } else {
            if (alpha == 1) {
                for (int i = 0; i < a.length; i++) {
                    c[i] = a[i] - (beta * b[i]);
                }
            } else if (beta == 1) {
                for (int i = 0; i < a.length; i++) {
                    c[i] = (alpha * a[i]) - b[i];
                }
            } else {
                for (int i = 0; i < a.length; i++) {
                    c[i] = (alpha * a[i]) - (beta * b[i]);
                }
            }
        }
    }
}
