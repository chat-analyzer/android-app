package test.gm.android.google.com.chatanalyzer;

import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
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

            ContentValues val = new ContentValues();
            val.put("action", "parseChat");
            val.put("debug", true);


        }


    }

    @Override
    public void onClick(View view) {
        pgb.setVisibility(ProgressBar.VISIBLE);

        SendChatsAsync async = new SendChatsAsync();
        async.execute("action", "parseChat");

        Intent i = new Intent(this, WebViewActivity.class);
        i.putExtra("URL", "http://example.com");
        startActivity(i);
    }

    private class SendChatsAsync extends AsyncTask<String, Void, String> {

        /**
         *
         * @param input First value:
         * @return
         */
        @Override
        protected String doInBackground(String... input) {
            try {
                URL url = new URL("http://chat-analyzer-server.azurewebsites.net/api");
                ContentValues val = new ContentValues();

                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("POST");
                con.setDoOutput(true);
                con.setDoInput(true);

                String reqBody = input[0] + "=" + input[1] + "&debug=true";

                DataOutputStream s = new DataOutputStream(con.getOutputStream());
                s.writeBytes(reqBody);
                s.flush();
                s.close();

                con.connect();

                int responseCode = con.getResponseCode();
                Log.d(TAG, "POST: " + responseCode);

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


            return null;
        }
    }
}
