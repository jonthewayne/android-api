// Copyright 2010 Square, Inc.
package com.squareup.android;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import static com.squareup.android.Square.InstallationStatus.AVAILABLE;
import static com.squareup.android.Square.InstallationStatus.MISSING;
import static com.squareup.android.Square.InstallationStatus.OUTDATED;

/**
 * Entry point of the Square for Android API. Interacts with the Square
 * application installed on this device. The user of this API can:
 *
 * <ol>
 *   <li>{@linkplain #installationStatus Query the installation status} of
 *      Square.</li>
 *   <li>{@linkplain #requestInstallation Request installation} of Square by
 *      navigating to the Android Market.</li>
 *   <li>{@linkplain #squareUp(Bill , int) Request a payment} through
 *      Square.</li>
 * </ol>
 *
 * For example:
 *
 * <pre>
 *  import android.app.Activity;
 *  import android.os.Bundle;
 *
 *  public class My2Cents extends Activity {
 *    &#64;Override public void onCreate(Bundle state) {
 *      super.onCreate(state);
 *
 *      Square square = new Square(this);
 *      if (square.installationStatus()
 *          != Square.InstallationStatus.AVAILABLE) {
 *        square.requestInstallation();
 *      } else {
 *        LineItem advice = new LineItem.Builder()
 *            .price(2, Currency.USD) // 2 cents
 *            .description("Advice")
 *            .build();
 *        square.squareUp(Bill.containing(advice));
 *      }
 *    }
 *  }
 * </pre>
 *
 * @author Bob Lee (bob@squareup.com)
 */
public final class Square {

  /** Minimum client version that supports this version of the API. */
  private static final int MINIMUM_VERSION = 2;

  /** Square package name. */
  private static final String PACKAGE = "com.squareup";

  /** Request payment action. */
  private static final String REQUEST_PAYMENT = PACKAGE + ".REQUEST_PAYMENT";

  private final Activity activity;

  /**
   * Constructs a new instance of this API.
   *
   * @param activity that requests the payment and receives the response
   */
  public Square(Activity activity) {
    this.activity = activity;
  }

  /**
   * Checks the status of the Square installation, if any, on this device.
   */
  public InstallationStatus installationStatus() {
    try {
      PackageInfo info = activity.getPackageManager().getPackageInfo(
          PACKAGE, 0);
      return info.versionCode >= MINIMUM_VERSION ? AVAILABLE : OUTDATED;
    } catch (PackageManager.NameNotFoundException e) {
      return MISSING;
    }
  }

  /**
   * Navigates to Square in the Android Market.
   */
  public void requestInstallation() {
    activity.startActivity(new Intent(Intent.ACTION_VIEW,
        Uri.parse("market://search?q=pname:" + PACKAGE)));
  }

  /**
   * Convenience method, equivalent to {@code squareUp(bill, 0)}. Useful
   * if this is the only usage of {@link Activity#startActivityForResult} in
   * the activity.
   */
  public void squareUp(Bill bill) {
    squareUp(bill, 0);
  }

  private static final String BILL_KEY = "bill";

  /**
   * Requests a payment through Square. Starts Square and fills in the price,
   * description, image and payer email address.
   *
   * <p>After Square finishes, Android invokes {@link
   * Activity#onActivityResult Activity.onActivityResult()} on the activity
   * passed to the constructor. The request code passed to this method will be
   * passed to {@code onActivityResult()}. The result code is {@link
   * Activity#RESULT_CANCELED} if the payment was canceled or {@link
   * Activity#RESULT_OK} if the payment succeeded.
   *
   * @param requestCode to pass to {@link Activity#onActivityResult}, >= 0
   * @throws IllegalArgumentException if requestCode < 0
   * @throws NullPointerException if bill is null
   * @throws android.content.ActivityNotFoundException if Square is not
   *  installed or doesn't support this version of the API
   * @throws ImageNotFoundException if the image can't be found
   */
  public void squareUp(Bill bill, int requestCode) {
    if (requestCode < 0) throw new IllegalArgumentException("requestCode < 0");
    if (bill == null) throw new NullPointerException("bill");

    Intent intent = new Intent(REQUEST_PAYMENT);
    // The calling app should show up in "recents", not Square.
    intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
    intent.putExtra(BILL_KEY, bill);
    activity.startActivityForResult(intent, requestCode);
  }

  /**
   * Status of the Square application on this device.
   */
  public enum InstallationStatus {

    /** Square is not installed. */
    MISSING,

    /** Square is installed, but it doesn't support this version of the API. */
    OUTDATED,

    /** Square is available, and it supports this API. */
    AVAILABLE
  }

  /**
   * Extracts the bill from the given intent. Used internally by Square.
   */
  static Bill billFrom(Intent intent) {
    Bundle extras = intent.getExtras();
    return extras == null ? null : (Bill) extras.getSerializable(BILL_KEY);
  }
}
