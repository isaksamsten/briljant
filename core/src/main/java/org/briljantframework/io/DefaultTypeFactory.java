package org.briljantframework.io;

import org.briljantframework.vector.*;

/**
 * Created by Isak Karlsson on 27/11/14.
 */
public class DefaultTypeFactory implements TypeFactory {

    public static final String INVALID_NAME = "Can't understand the type %s";

    @Override
    public Type getTypeForName(String name) {
        switch (name.toLowerCase().trim()) {
            case "numeric":
            case "double":
                return DoubleVector.TYPE;
            case "class":
            case "categoric":
                return StringVector.TYPE;
            case "integer":
                return IntVector.TYPE;
            case "complex":
                return ComplexVector.TYPE;
            case "binary":
                return BinaryVector.TYPE;
            default:
                throw new IllegalArgumentException(String.format(INVALID_NAME, name.toLowerCase().trim()));
        }
    }
}
