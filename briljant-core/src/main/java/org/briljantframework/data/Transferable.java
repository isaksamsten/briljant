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
package org.briljantframework.data;

/**
 * A marker-interface for {@linkplain org.briljantframework.data.vector.Vector vectors} that are
 * <em>transferable</em> between data frames and/or other data-structures. For example, a vector
 * that keeps it's data in an {@code array} is generally <em>transferable</em> but a vector that is
 * simply a view (e.g {@link org.briljantframework.data.vector.VectorView}) of another vector or
 * (heavy) data-structure such as a data frame is not. Vectors not marked will be copied by, e.g.,
 * {@link org.briljantframework.data.vector.Vectors#transferableBuilder(org.briljantframework.data.vector.Vector)}
 * to allow larger objects to be garbage collected.
 *
 * @author Isak Karlsson
 */
public interface Transferable {
}
