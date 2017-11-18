package test.gm.android.google.com.chatanalyzer;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import java.net.URI;
import java.util.ArrayList;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    final static String TAG = "MainActivity";

    Button btn;
    ProgressBar pgb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn = (Button) findViewById(R.id.button);
        btn.setOnClickListener(this);
        pgb = (ProgressBar) findViewById(R.id.progress_bar);

        Intent i = getIntent();

        if(Intent.ACTION_SEND == i.getAction() || Intent.ACTION_SEND_MULTIPLE == i.getAction()) {
            Bundle b = i.getExtras();
            Set keys = b.keySet();
            String subj = b.getString(Intent.EXTRA_SUBJECT);
            String text = b.getString(Intent.EXTRA_TEXT);
            Log.d(TAG, String.valueOf(((ArrayList<Uri>) b.get(Intent.EXTRA_STREAM)).get(0)));
            Log.d(TAG, i.getExtras().keySet().toString());
        }


    }

    @Override
    public void onClick(View view) {
        pgb.setVisibility(ProgressBar.VISIBLE);
    }
}
