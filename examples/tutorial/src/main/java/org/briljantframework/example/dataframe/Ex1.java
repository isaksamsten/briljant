/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 Isak Karlsson
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
package org.briljantframework.example.dataframe;

import org.briljantframework.data.dataframe.DataFrame;
import org.briljantframework.data.vector.Vector;

/**
 * Created by Isak Karlsson on 07/01/15.
 */
public class Ex1 {

  public static void main(String[] args) {
    Vector cars = Vector.of("Volvo", "BMW", "Saab");
    DataFrame engines =
        DataFrame.of("Brand", cars, "Engine Size", Vector.of("large", "small", "tiny"),
            "Breaking Horse Powers", Vector.of(100, 22, 3));

    DataFrame sizes =
        DataFrame.of("Brand", cars, "wheel", Vector.of(21, 22, 33), "length",
            Vector.of(10, 20, 30.0));

    System.out.println(engines);
    System.out.println(sizes);

    System.out.println(engines.join(sizes, "Brand"));

    // System.out.println(DataFrames.innerJoin(engines, sizes, Arrays.asList(0)));
  }
}
