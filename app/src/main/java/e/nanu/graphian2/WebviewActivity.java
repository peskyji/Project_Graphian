package e.nanu.graphian2;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.Toolbar;

public class WebviewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);


        final ProgressBar Pbar=(ProgressBar) findViewById(R.id.webviewPbar);
        final WebView myWebView = (WebView) findViewById(R.id.webview);

        // extracting  input from previous activity
        Intent intent=getIntent();
        String url=intent.getStringExtra("visiturl");

        // implementing webview
        WebSettings webSettings = myWebView.getSettings();
        // enabling JS
        webSettings.setJavaScriptEnabled(true);
        //loading url
        myWebView.loadUrl(url);

        myWebView.setVisibility(View.INVISIBLE);

        myWebView.setWebChromeClient(new WebChromeClient());

        // to track the events of the page being loaded we use webViewClient
        myWebView.setWebViewClient(new WebViewClient(){

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                Toast.makeText(getApplicationContext(),"Page Loading",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                Toast.makeText(getApplicationContext(),"Page Loaded",Toast.LENGTH_SHORT).show();
               // Pbar.setVisibility(View.GONE);
                myWebView.setVisibility(View.VISIBLE);
            }
        });

        myWebView.loadUrl(url);
    }
}
