// Copyright 2010 Square, Inc.
package com.squareup.android;

/**
 * Thrown when an image can't be found.
 *
 * @author Bob Lee (bob@squareup.com)
 */
public final class ImageNotFoundException extends RuntimeException {

  public ImageNotFoundException() {}

  public ImageNotFoundException(String detailMessage) {
    super(detailMessage);
  }

  public ImageNotFoundException(String detailMessage, Throwable throwable) {
    super(detailMessage, throwable);
  }

  public ImageNotFoundException(Throwable throwable) {
    super(throwable);
  }
}
