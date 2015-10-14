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

package org.briljantframework.evaluation;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * @author Isak Karlsson
 */
public class Result {

  private final Map<Class<?>, Measure> measures;

  public Result(EvaluationContext ctx) {
    List<Measure> measures = ctx.getMeasures();
    this.measures = new HashMap<>();

    int length = 0;
    if (measures.size() > 0) {
      length = measures.get(0).size();
    }

    for (Measure measure : measures) {
      if (measure.size() != length) {
        throw new IllegalArgumentException(String.format("Invalid number of metrics for %s",
            measure.getName()));
      }
      this.measures.put(measure.getClass(), measure);
    }
  }

  public <T extends Measure> T get(Class<T> key) {
    Measure measure = measures.get(key);
    if (measure != null) {
      return key.cast(measure);
    } else {
      throw new NoSuchElementException(String.format("%s can't be found", key.getSimpleName()));
    }
  }

  public Collection<Measure> getMeasures() {
    return Collections.unmodifiableCollection(measures.values());
  }

  @Override
  public String toString() {
    return getMeasures().toString();
  }
}
