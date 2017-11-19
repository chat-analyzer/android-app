package test.gm.android.google.com.chatanalyzer;

import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;

import cz.msebera.android.httpclient.Header;

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
        } else {
            btn.setFocusable(false);
            //btn.setActivated(false);
            btn.setEnabled(false);
            TextView bottom = (TextView) findViewById(R.id.text_view_bottom);
            bottom.setText("This app only works when opened via WhatsApp's \"send email\" feature.\nPlease open a chat in WhatsApp, choose menu, more, send chat via email!");
        }


    }

    @Override
    protected void onPause() {
        pgb.setVisibility(ProgressBar.INVISIBLE);
        super.onPause();
    }

    @Override
    public void onClick(View view) {
        pgb.setVisibility(ProgressBar.VISIBLE);


        String fullChat = "";
        StringBuffer buf = new StringBuffer();


        final String webviewTarget;
        webviewTarget = "";

        // prepare reading of chat file
        try {
            InputStream is = getContentResolver().openInputStream(fileUri);

            RequestParams rp = new RequestParams();
            rp.put("chat", is, "chat.txt");
            AsyncHttpClient client = new AsyncHttpClient();

            client.post("http://chat-analyzer-server.azurewebsites.net/registerChat", rp, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    String webviewTarget = new String(responseBody);

                    Intent i = new Intent(getApplicationContext(), WebViewActivity.class);
                    i.putExtra("URL", webviewTarget);
                    startActivity(i);
                    Log.d(TAG, statusCode + " - " + webviewTarget);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    Log.d(TAG, "" + statusCode);
                }
            });
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }
}
