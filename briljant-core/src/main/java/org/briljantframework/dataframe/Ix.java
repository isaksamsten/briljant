/*
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

package org.briljantframework.dataframe;

/**
 * @author Isak Karlsson
 */
public interface Ix {

  <T> T get(Class<T> cls, Object r, int c);

  <T> T get(Class<T> cls, int r, Object c);

  <T> T get(Class<T> cls, Object r, Object c);

  <T> T get(Class<T> cls, int r, int c);

  int getAsInt(Object r, int c);

  int getAsInt(int r, Object c);

  int getAsInt(Object r, Object c);

  int getAsInt(int r, int c);

  double getAsDouble(Object r, int c);

  double getAsDouble(int r, Object c);

  double getAsDouble(Object r, Object c);

  double getAsDouble(int r, int c);
}
