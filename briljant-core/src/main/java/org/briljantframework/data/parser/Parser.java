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

package org.briljantframework.data.parser;

import java.util.function.Supplier;

import org.briljantframework.data.dataframe.DataFrame;

/**
 * A parser parsers some data source and produces a data frame
 * 
 * @author Isak Karlsson
 */
public interface Parser {

  /**
   * Parse the data source and produce a data frame (specified by the {@link DataFrame#builder()}).
   * 
   * @return a data frame
   */
  default DataFrame parse() {
    return parse(DataFrame::builder);
  }

  /**
   * Parse the data source and produce a data frame using the supplied builder.
   * 
   * @param builder the builder
   * @return a data frame
   */
  DataFrame parse(Supplier<? extends DataFrame.Builder> builder);
}
