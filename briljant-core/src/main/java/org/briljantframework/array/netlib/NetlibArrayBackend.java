package org.briljantframework.array.netlib;

import org.briljantframework.linalg.api.LinearAlgebraRoutines;
import org.briljantframework.array.api.ArrayBackend;
import org.briljantframework.array.api.ArrayFactory;
import org.briljantframework.array.api.ArrayRoutines;

import java.util.logging.Handler;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 * @author Isak Karlsson
 */
public class NetlibArrayBackend implements ArrayBackend {

  static {
    // This should suppress the output from the JNI logger
    Logger blasLogger = LogManager.getLogManager().getLogger("");
    if (blasLogger != null) {
      for (Handler handler : blasLogger.getHandlers()) {
        handler.close();
        blasLogger.removeHandler(handler);
      }
    }
  }

  private ArrayFactory arrayFactory;
  private ArrayRoutines arrayRoutines;
  private LinearAlgebraRoutines linearAlgebraRoutines;

  @Override
  public boolean isAvailable() {
    return true;
  }

  @Override
  public int getPriority() {
    return 100;
  }

  @Override
  public ArrayFactory getArrayFactory() {
    if (arrayFactory == null) {
      arrayFactory = new NetlibArrayFactory();
    }
    return arrayFactory;
  }

  @Override
  public ArrayRoutines getArrayRoutines() {
    if (arrayRoutines == null) {
      arrayRoutines = new NetlibArrayRoutines();
    }
    return arrayRoutines;
  }

  @Override
  public LinearAlgebraRoutines getLinearAlgebraRoutines() {
    if (linearAlgebraRoutines == null) {
      linearAlgebraRoutines = new NetlibLinearAlgebraRoutines(this);
    }
    return linearAlgebraRoutines;
  }
}
