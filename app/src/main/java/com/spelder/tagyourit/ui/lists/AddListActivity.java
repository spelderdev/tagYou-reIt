package com.spelder.tagyourit.ui.lists;

import android.os.Bundle;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import com.spelder.tagyourit.R;

/** Activity used to display and control the music player. */
public class AddListActivity extends AppCompatActivity {
  private static final String TAG = "AddListActivity";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.add_list);

    ImageView close = findViewById(R.id.add_list_close);
    close.setOnClickListener(view -> onBackPressed());
  }

  @Override
  public void onBackPressed() {
    finish();
    openActivityFromBottom();
  }

  private void openActivityFromBottom() {
    overridePendingTransition(R.anim.stay, R.anim.slide_down);
  }
}
