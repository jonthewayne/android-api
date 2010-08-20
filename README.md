Square for Android API
======================

[Square](https://squareup.com/) enables Android users to accept cash and card payments, and email receipts. The Square for Android API enables Android-based point-of-sale applications to accept payments using Square.

Rather than settle for traditional, expensive one-size-fits-all point-of-sale solutions, the Square for Android API enables programmers to easily create custom-fit applications. Examples include:

* A taxi cab meter that tracks your location using the GPS and computes a price based on the distance travelled.
* A bar app that enables bartenders to track tabs based on seating location, quickly enter drink combinations, and square up when the customer leaves.
* A donation application that enables a charity to collect contact information from the benefactor.

The current release v0.9 is a *preview release* and is subject to incompatible changes. Your feedback will help ensure a solid 1.0 release. Square v1.1.1, available through the Android Market, supports this version of the API. 

Installation
------------

Download [`square-android-api-0.9.zip`](https://github.com/downloads/square/android-api/square-android-api-0.9.zip). Extract `square-android-api-0.9.zip` to your application's `libs` directory.

Usage
-----

See the [Square for Android API documentation](http://corner.squareup.com/android-api/javadoc/com/squareup/android/package-summary.html). Start with the [`Square`](http://corner.squareup.com/android-api/javadoc/com/squareup/android/Square.html) class.

The current version of Square supports only one line item. Future versions of Square may support more; the API is designed with this in mind.

Example
-------

The following activity check whether Square is installed. If Square is missing, the activity navigates to Square in the Market so the user can install it. Once Square is installed, the activity starts Square and fills in the price (2 cents) and description ("Advice").

    import android.app.Activity;
    import android.os.Bundle;

    public class TwoCents extends Activity {
      @Override public void onCreate(Bundle state) {
        super.onCreate(state);

        Square square = new Square(this);
        if (square.installationStatus()
            != Square.InstallationStatus.AVAILABLE) {
          square.requestInstallation();
        } else {
          LineItem advice = new LineItem.Builder()
              .price(2, Currency.USD) // 2 cents
              .description("Advice")
              .build();
          square.squareUp(Bill.containing(advice));
        }
      }
    }

For a complete example, see [TwoCents](https://github.com/square/android-api/tree/master/examples/twocents/) in the examples directory.

Mailing List
------------

To get help and otherwise discuss the Square for Android API, please join our [mailing list](http://groups.google.com/group/square-android-api), square-android-api@googlegroups.com.

License
-------

This project is licensed under the [Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0).

