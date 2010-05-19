package com.squareup.android.examples.twocents;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import com.squareup.android.Bill;
import com.squareup.android.Currency;
import com.squareup.android.Image;
import com.squareup.android.LineItem;
import com.squareup.android.Square;

/**
 * Makes it easy to charge two cents for advice using Square.
 */
public class TwoCents extends Activity implements View.OnClickListener {
  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);
    findViewById(R.id.pay).setOnClickListener(this);
  }

  public void onClick(View v) {
    Square square = new Square(this);
    if (square.installationStatus()
        != Square.InstallationStatus.AVAILABLE) {
      square.requestInstallation();
    } else {
      Image twocents = Image.forResource(this, R.drawable.twocents,
          Image.Type.JPEG);
      LineItem advice = new LineItem.Builder()
          .price(2, Currency.USD) // 2 cents
          .description("Advice")
          .image(twocents)
          .build();
      square.squareUp(Bill.containing(advice));
    }
  }

  @Override protected void onActivityResult(int requestCode, int resultCode,
      Intent data) {
    if (resultCode == RESULT_CANCELED) {
      Log.i(getClass().getSimpleName(), "Payment canceled.");
    } else if (resultCode == RESULT_OK) {
      Toast.makeText(this, "Payment succeeded!", Toast.LENGTH_SHORT).show();
    }
  }
}
