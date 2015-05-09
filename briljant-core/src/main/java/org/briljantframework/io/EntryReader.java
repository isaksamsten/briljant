package org.briljantframework.io;

import java.io.IOException;

/**
 * Created by isak on 08/05/15.
 */
public interface EntryReader {

  /**
   * Reads the next entry from this stream
   *
   * @return the next entry
   */
  DataEntry next() throws IOException;

  /**
   * Returns {@code true} if there are more values in the stream
   *
   * @return if has next
   */
  boolean hasNext() throws IOException;
}
