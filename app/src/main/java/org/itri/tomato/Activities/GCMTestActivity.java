package org.itri.tomato.Activities;

import android.content.Intent;
import android.provider.SyncStateContract;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import org.itri.tomato.R;

public class GCMTestActivity extends AppCompatActivity {
    private static final String TAG = "GCMTestActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gcmtest);
        Intent intent = getIntent();
        if (intent.hasExtra("from") && intent.hasExtra("message")) {
            String from = intent.getStringExtra("from");
            String message = intent.getStringExtra("message");
            String collapse_key;
            Log.d(TAG, "from " + from + ": " + message);
        }
        TextView button = (TextView) findViewById(R.id.gcmMsg);
        button.setText(button.getText().toString()+"\n: " + intent.getStringExtra("message"));
    }

}
