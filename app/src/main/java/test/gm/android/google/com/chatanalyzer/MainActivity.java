package test.gm.android.google.com.chatanalyzer;

import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    final static String TAG = "MainActivity";

    Button btn;
    ProgressBar pgb;

    Uri fileUri;

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
            fileUri = ((ArrayList<Uri>) b.get(Intent.EXTRA_STREAM)).get(0);
        }


    }

    @Override
    public void onClick(View view) {
        pgb.setVisibility(ProgressBar.VISIBLE);

        SendChatsAsync async = new SendChatsAsync();

        String fullChat = "";
        StringBuffer buf = new StringBuffer();

        // convert URI file to String

        try {
            InputStream is = getContentResolver().openInputStream(fileUri);
            BufferedReader r = new BufferedReader(new InputStreamReader(is));
            if(is != null) {
                while((fullChat = r.readLine()) != null) {
                    buf.append(fullChat+"\n");
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        fullChat = buf.toString();

        //Log.d(TAG, fullChat);
        //Log.d(TAG, URLEncoder.encode(fullChat));

        Log.d(TAG, "async " + async.execute(fullChat));
    }

    private class SendChatsAsync extends AsyncTask<String, Void, String> {

        String response = "";

        /**
         *
         * @param input
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



                String reqBody = "action=registerChat&chat=" + URLEncoder.encode(input[0]);

                DataOutputStream s = new DataOutputStream(con.getOutputStream());
                s.writeBytes(reqBody);
                s.flush();
                s.close();

                con.connect();

                int responseCode = con.getResponseCode();
                if(responseCode >= 200 && responseCode < 300) { // 2XX: success
                    BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    String inputLine;

                    StringBuffer buf = new StringBuffer();

                    while((inputLine = in.readLine()) != null) {
                        buf.append(inputLine);
                    }
                    response = buf.toString();
                    Log.d(TAG, response);
                }
                Log.d(TAG, "POST: " + responseCode);

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


            return response;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Intent i = new Intent(getApplicationContext(), WebViewActivity.class);
            i.putExtra("URL", response);
            startActivity(i);
        }
    }
}
