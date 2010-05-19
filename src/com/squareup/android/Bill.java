// Copyright 2010 Square, Inc.
package com.squareup.android;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * A bill that can be paid using Square.
 *
 * @see Builder
 * @see Square#squareUp
 * @author Bob Lee (bob@squareup.com)
 */
public final class Bill implements Serializable {
  private static final long serialVersionUID = 0;

  private final List<LineItem> lineItems;
  private final String defaultEmail;

  private Bill(List<LineItem> lineItems, String defaultEmail) {
    this.defaultEmail = defaultEmail;
    this.lineItems = lineItems;
  }

  /**
   * Returns the default email address for the payer or null if none was
   * specified.
   */
  public String defaultEmail() {
    return defaultEmail;
  }

  /**
   * Returns the list of items associated with this bill. Contains at least
   * one element.
   */
  public List<LineItem> lineItems() {
    return lineItems;
  }

  /*
   * Note: We can add containing(LineItem first, LineItem... others) later.
   */

  /**
   * Constructs a bill that contains a single line item.
   *
   * @throws NullPointerException is lineItem is null
   */
  public static Bill containing(LineItem lineItem) {
    // Note: Ideally, this method would be named "for".
    return new Bill.Builder().add(lineItem).build();
  }

  @Override public String toString() {
    return "Bill{" +
        "defaultEmail='" + defaultEmail + '\'' +
        ", lineItems=" + lineItems +
        '}';
  }

  /**
   * Builds a {@linkplain Bill bill}. {@linkplain #add Add} one item
   * exactly.
   */
  public final static class Builder {

    private LineItem lineItem;

    /**
     * Adds an item to the bill. Currently only supports one item. Required.
     *
     * @param lineItem for receipt
     * @throws IllegalStateException if more than one item is added
     * @throws NullPointerException if item is null
     * @return this builder
     */
    public Builder add(LineItem lineItem) {
      if (this.lineItem != null) {
        throw new IllegalStateException("> 1 line item");
      }
      if (lineItem == null) throw new NullPointerException("lineItem");
      this.lineItem = lineItem;
      return this;
    }

    private String defaultEmail;

    /*
     * Note: We play it safe and only allow the app to fill in a default email.
     * Enabling the app to further affect how Square emails receipts has
     * security implications.
     */

    /**
     * Specifies a default email address to send a receipt to. Optional. If
     * the user chooses to email themselves a receipt and Square doesn't
     * already have an email address on file, Square will pre-fill the email
     * field with this value.
     *
     * @param email address to send receipt to
     * @throws IllegalStateException if the default email is already set
     * @throws NullPointerException if email is null
     * @return this builder
     */
    public Builder defaultEmail(String email) {
      if (this.defaultEmail != null) alreadySet("default email");
      if (lineItem == null) throw new NullPointerException("item");
      this.defaultEmail = email;
      return this;
    }

    private void alreadySet(String name) {
      throw new IllegalStateException(name + " is already set.");
    }

    /**
     * Builds the bill.
     *
     * @throws IllegalStateException if an item wasn't added
     */
    public Bill build() {
      return new Bill(Collections.singletonList(lineItem), defaultEmail);
    }
  }

  private void readObject(ObjectInputStream in) throws IOException,
      ClassNotFoundException {
    in.defaultReadObject();
    if (lineItems == null || lineItems.size() != 1) {
      throw new AssertionError("invalid items");
    }
  }
}
