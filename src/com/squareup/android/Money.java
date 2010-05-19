// Copyright 2010 Square, Inc.
package com.squareup.android;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;

/**
 * A quantity of a specific {@linkplain Currency currency}.
 *
 * @author Bob Lee (bob@squareup.com)
 */
public final class Money implements Serializable {
  private static final long serialVersionUID = 0;

  /**
   * The maximum amount supported by Square. This is the absolute maximum
   * supported by the Square application; it's likely much greater than the
   * merchant-specific card payment limit.
   */
  public static final long MAX_AMOUNT = 999999999; // 9 digits or $9,999,999.99

  private final long amount;
  private final Currency currency;

  /**
   * Constructs a new Money. The amount is specified in
   * atomic units of the specified currency. For example, if the currency is the
   * {@linkplain Currency#USD U.S. dollar}, the amount is specified in
   * cents.
   *
   * @param amount atomic units of the specified currency, >= 0 && <=
   *  {@link #MAX_AMOUNT}
   * @param currency type
   * @throws IllegalArgumentException if amount is < 0 || >
   *  {@link #MAX_AMOUNT}
   * @throws NullPointerException if currency is null
   */
  public Money(long amount, Currency currency) {
    if (currency == null) throw new NullPointerException("currency");
    if (amount < 0) throw new IllegalArgumentException("amount < 0");
    if (amount > MAX_AMOUNT) {
      throw new IllegalArgumentException("amount > MAX_AMOUNT");
    }

    this.amount = amount;
    this.currency = currency;
  }

  /**
   * Returns the amount in atomic units of {@link #currency}.
   */
  public long amount() {
    return amount;
  }

  /**
   * Returns the currency type for this value.
   */
  public Currency currency() {
    return currency;
  }

  @Override public String toString() {
    return "Money{" +
        "amount=" + amount +
        ", currency=" + currency +
        '}';
  }

  private void readObject(ObjectInputStream in) throws IOException,
      ClassNotFoundException {
    in.defaultReadObject();
    if (currency == null) throw new AssertionError("missing currency");
    if (amount < 0) throw new AssertionError("amount < 0");
    if (amount > MAX_AMOUNT) throw new AssertionError("amount > MAX_AMOUNT");
  }
}