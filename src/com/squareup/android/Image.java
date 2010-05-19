// Copyright 2010 Square, Inc.
package com.squareup.android;

import android.content.Context;
import android.net.Uri;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;

/**
 * An image locatable via a URL.
 *
 * @author Bob Lee (bob@squareup.com)
 */
public final class Image implements Serializable {
  private static final long serialVersionUID = 0;

  /** Supported image types. */
  public enum Type {

    JPEG("image/jpeg"), PNG("image/png");

    private final String mimeType;

    Type(String mimeType) {
      this.mimeType = mimeType;
    }

    /**
     * Returns the MIME type string.
     */
    public String mimeType() {
      return mimeType;
    }
  }

  private final String url;
  private final Type type;

  /**
   * Constructs a new image.
   *
   * @param url of the image
   * @param type of image
   * @throws NullPointerException is url or type is null
   */
  public Image(Uri url, Type type) {
    if (url == null || type == null) throw new NullPointerException();
    this.type = type;
    this.url = url.toString();
  }

  /**
   * Constructs an Image for an application resource.
   *
   * @param context of the application containing the resource
   * @param resourceId for the image resource
   * @param type of the image resource
   */
  public static Image forResource(Context context, int resourceId, Type type) {
    return new Image(Uri.parse("android.resource://"
          + context.getPackageName() + "/" + resourceId), type);
  }

  /**
   * Returns the image type.
   */
  public Type type() {
    return type;
  }

  /**
   * Returns a URL that can be used to retrieve the image.
   */
  public Uri url() {
    return Uri.parse(url);
  }

  @Override public String toString() {
    return "Image{" +
        "type=" + type +
        ", url=" + url +
        '}';
  }

  private void readObject(ObjectInputStream in) throws IOException,
      ClassNotFoundException {
    in.defaultReadObject();
    if (url == null) throw new AssertionError("missing url");
    if (type == null) throw new AssertionError("missing type");
  }
}
