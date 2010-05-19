// Copyright 2010 Square, Inc.
package com.squareup.android;

import android.content.ContentResolver;
import android.net.Uri;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;

/**
 * A line item in a {@linkplain Bill bill}.
 *
 * @see Builder
 * @author Bob Lee (bob@squareup.com)
 */
public final class LineItem implements Serializable {
  private static final long serialVersionUID = 0;

  /*
   * Note: This is designed to allow for the clean addition of a "quantity"
   * field down the road.
   */

  private static final int MAX_DESCRIPTION_LENGTH = 140;

  private final String description;
  private final Money price;
  private final Image image;

  private LineItem(String description, Money price, Image image) {
    this.description = description;
    this.price = price;
    this.image = image;
  }

  /**
   * Returns the description of this item or null if no description was
   * provided.
   */
  public String description() {
    return description;
  }

  /**
   * Returns the price of this line item. Never returns null.
   */
  public Money price() {
    return price;
  }

  /**
   * Returns the image associated with this item or null if no image was
   * provided.
   */
  public Image image() {
    return image;
  }

  @Override public String toString() {
    return "LineItem{" +
        "description='" + description + '\'' +
        ", price=" + price +
        ", image=" + image +
        '}';
  }

  /**
   * Builds a {@linkplain LineItem line item}. A {@linkplain #price(Money)
   * price} is required. Other attributes are optional.
   */
  public static final class Builder {

    private String description;

    /**
     * Describes the item in 140 characters or less. Optional.
     *
     * @param description of the item, 140 characters max
     * @throws IllegalStateException if the description is already set
     * @throws IllegalArgumentException if description is greater than 140
     *  characters
     * @throws NullPointerException if description is null
     * @return this builder
     */
    public Builder description(String description) {
      if (this.description != null) alreadySet("description");
      if (description == null) throw new NullPointerException("description");
      if (description.length() > MAX_DESCRIPTION_LENGTH) {
        throw new IllegalArgumentException("description > 140 chars");
      }
      this.description = description;
      return this;
    }

    private Money price;

    /**
     * Convenience method, equivalent to
     * {@code price(new Price(amount, currency))}.
     *
     * @param amount atomic units of the specified currency, {@code >= 0 && <=
     *  999,999,999}
     * @throws IllegalArgumentException if {@code amount > 999,999,999}
     * @see Money#Money
     */
    public Builder price(int amount, Currency currency) {
      return price(new Money(amount, currency));
    }

    /**
     * Specifies the price of the line item. Required.
     *
     * @param price of the line item, {@code price.amount() <=
     *  Integer.MAX_VALUE}
     * @throws IllegalStateException if the price is already set
     * @throws IllegalArgumentException if {@code price.amount() >
     *  Integer.MAX_VALUE}
     * @return this builder
     */
    public Builder price(Money price) {
      if (this.price != null) alreadySet("price");
      if (price.amount() > Integer.MAX_VALUE) {
        throw new IllegalArgumentException(
            "price.amount() > Integer.MAX_VALUE");
      }
      this.price = price;
      return this;
    }

    private Image image;

    /**
     * Convenience method, equivalent to
     * {@code image(new Image(url, imageType))}.
     *
     * @see Image#Image
     */
    public Builder image(Uri url, Image.Type imageType) {
      return image(new Image(url, imageType));
    }

    /**
     * Associates an image with the item. Optional. Supports the following URL
     * schemes:
     *
     * <ol>
     *   <li><tt>content://</tt> - Retrieves the image from a content provider
     *      using {@link ContentResolver}.</li>
     *
     *   <li><tt>file://</tt> - Reads the file from the file system. The
     *      file can be on the SD card or in the application's private data
     *      directory.
     *
     *  <li><tt>android.resource://</tt> - Reads the image from a resource in
     *      the APK. See {@link ContentResolver#openAssetFileDescriptor} for
     *      a definition of the URI scheme.
     * </ol>
     *
     * @throws IllegalStateException if the image is already set
     * @throws IllegalArgumentException if the URL uses an unsupported scheme
     *  or is otherwise malformed
     * @throws NullPointerException if image is null
     * @return this builder
     */
    public Builder image(Image image) {
      // TODO: Specify maximum image size in bytes and enforce it.
      if (this.image != null) alreadySet("image");
      if (image == null) throw new NullPointerException("image");
      this.image = image;
      return this;
    }

    private void alreadySet(String name) {
      throw new IllegalStateException(name + " is already set.");
    }

    /**
     * Builds an item.
     *
     * @throws IllegalStateException if the price wasn't set
     */
    public LineItem build() {
      if (price == null) throw new IllegalStateException("price not set.");
      return new LineItem(description, price, image);
    }
  }

  private void readObject(ObjectInputStream in) throws IOException,
      ClassNotFoundException {
    in.defaultReadObject();
    if (price == null) throw new AssertionError("missing price");
    if (price.amount() > Integer.MAX_VALUE) {
      throw new AssertionError("price.amount() > Integer.MAX_VALUE");
    }
    if (description != null && description.length() > MAX_DESCRIPTION_LENGTH) {
      throw new AssertionError("description.length() > 140");
    }
  }
}
