package test.gm.android.google.com.chatanalyzer;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;

public class WebViewActivity extends AppCompatActivity implements View.OnClickListener {

    final static String TAG = "WebViewActivity";

    String url = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_web_view);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);


        WebView webView = (WebView) findViewById(R.id.webview);
        webView.getSettings().setJavaScriptEnabled(true);

        Intent i = getIntent();

        if(i.hasExtra("URL")) {
            url = i.getStringExtra("URL");
            Log.d(TAG, url);
            webView.loadUrl(url);
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Intent share = new Intent(Intent.ACTION_SEND);
        share.putExtra(Intent.EXTRA_TEXT, url);
        //share.putExtra(Intent.EXTRA_SUBJECT, "Look at these stats about our group chat: ");

        share.setType("text/html");

        Intent chooser = Intent.createChooser(share, "Share these stats with your friends:");
        startActivity(chooser);
    }
}
