/*
 * The MIT License (MIT)
 * 
 * Copyright (c) 2015 Isak Karlsson
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package org.briljantframework.array.base;

import org.briljantframework.array.api.ArrayBackend;
import org.briljantframework.array.api.ArrayFactory;
import org.briljantframework.array.api.ArrayRoutines;
import org.briljantframework.linalg.api.LinearAlgebraRoutines;

/**
 * @author Isak Karlsson
 */
public class BaseArrayBackend implements ArrayBackend {

  private ArrayFactory arrayFactory;
  private ArrayRoutines arrayRoutines;
  private LinearAlgebraRoutines linearAlgebraRoutines;

  @Override
  public boolean isAvailable() {
    return true;
  }

  @Override
  public int getPriority() {
    return Integer.MIN_VALUE;
  }

  @Override
  public ArrayFactory getArrayFactory() {
    if (arrayFactory == null) {
      arrayFactory = new BaseArrayFactory();
    }
    return arrayFactory;
  }

  @Override
  public ArrayRoutines getArrayRoutines() {
    if (arrayRoutines == null) {
      arrayRoutines = new BaseArrayRoutines();
    }
    return arrayRoutines;
  }

  @Override
  public LinearAlgebraRoutines getLinearAlgebraRoutines() {
    if (linearAlgebraRoutines == null) {
      linearAlgebraRoutines = new BaseLinearAlgebraRoutines(this);
    }
    return linearAlgebraRoutines;
  }
}
