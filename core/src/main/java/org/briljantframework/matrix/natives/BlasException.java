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

package org.briljantframework.matrix.natives;

/**
 * Created by isak on 02/07/14.
 */
public class BlasException extends RuntimeException {

    private final int errorCode;
    private final String nativeMethod;

    /**
     * Exception message: <pre>{nativeMethod} exited unexpectedly with error code {errorCode}. {reason}.</pre>
     *
     * @param nativeMethod the native method that failed
     * @param errorCode    the error code it emitted
     * @param reason       is the reason
     */
    public BlasException(String nativeMethod, int errorCode, String reason) {
        super(String.format("%s exited unexpectedly with error %d. %s", nativeMethod, errorCode, reason));
        this.nativeMethod = nativeMethod;
        this.errorCode = errorCode;
    }

    /**
     * Error code.
     *
     * @return the int
     */
    public int getErrorCode() {
        return errorCode;
    }

    /**
     * Native method.
     *
     * @return the string
     */
    public String getNativeMethod() {
        return nativeMethod;
    }
}
