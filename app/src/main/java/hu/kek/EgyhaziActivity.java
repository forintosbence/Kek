package hu.kek;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.SearchView;

import android.util.Base64;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.TextView;

import hu.kek.Adatok.AdatEgyhazi;
import hu.kek.Adatok.AdatSzentiras;

public class EgyhaziActivity extends AppCompatActivity {

    private WebView mWebView;
    private float sortav = BeallitasokActivity.sortav;
    private float alapBetuMeret = BeallitasokActivity.alapBetuMeret;
    private SearchView searchView;
    private SearchManager searchManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Statikus.temaBeallit(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_egyhazi);
        getSupportActionBar().setTitle("Egyházi források");

        mWebView = (WebView) findViewById(R.id.egyhaziForrasokWebView);
        mWebView.setBackgroundColor(Color.TRANSPARENT);
        WebSettings settings = mWebView.getSettings();
        settings.setDefaultTextEncodingName("utf-8");
        settings.setDefaultFontSize(18);
        settings.setBuiltInZoomControls(true);
        settings.setDisplayZoomControls(false);
        mWebView.setWebChromeClient(new WebChromeClient());
        settings.setJavaScriptEnabled(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                kiir(url);
                return true;
            }

           // @Override
           // public void onPageFinished(WebView view, String url) {
           //     if(dialog.isShowing()) {
           //         dialog.dismiss();
           //     }
           // }
        });

            String webData = "<html><body><style>body{line-height:"+sortav+"em; font-size:"+alapBetuMeret+"em;text-align: center; color:"+Statikus.szovegSzin+";} h1,h2,h3{margin: 0;padding: 0;border: 0; font-size: 100%; font: inherit;vertical-align: baseline;} h1{padding: 15px 0px 15px 0px; font-size: 1.3em;font-weight: bold;} h2{padding: 15px 0px 15px 0px; font-size: 1em;font-weight: bold;} h3{padding: 15px 0px 15px 0px; font-size: 1.1em;} a{display: block; color:"+Statikus.szovegSzin+"; text-decoration:none; border-bottom: 1px solid #ababab;} a:nth-of-type(1) {border-top: 1px solid #ababab;}</style>"+ AdatEgyhazi.egyhaziTartalom+"</body></html>";

            webData = Base64.encodeToString(webData.getBytes(), Base64.NO_PADDING);
            mWebView.loadData(webData, "text/html", "base64");

    }

    public void kiir (String paragrafus) {
        if(paragrafus.contains("#e")) {
            searchView.setVisibility(View.VISIBLE);
            int konyv = Integer.parseInt(paragrafus.split("#e")[1]);
            String konyvSzoveg = "";
            for (int i = 0; i< AdatEgyhazi.egyhaziForrasok[konyv].length; ++i){
                konyvSzoveg += AdatEgyhazi.egyhaziForrasok[konyv][i];
            }


            String webData = "<html><body><style>body, table{line-height:"+sortav+"em; font-size:"+alapBetuMeret+"em; color:"+Statikus.szovegSzin+";} h2,span{margin: 0; padding: 0; border: 0; font-size: 100%; font: inherit;vertical-align: baseline; padding: 15px 0px 2px 0px;} h2{font-size: 1.2em; font-weight: bold; text-align:center; margin-bottom: 1em;}h3{font-size: 1em;font-weight: bold;margin-bottom: 0.3em;}h4{font-size: 0.85em;font-weight: bold;}h5{font-weight: normal;font-size: 0.85em;} a{padding: 8px 0px 8px 14px; white-space:nowrap; display: inline-block; text-decoration: none; color:"+Statikus.linkSzin+";} table {margin: 0 auto; padding-bottom:20px;} td{border-bottom: 1px solid #ababab;/*word-break: break-word;*/} td:nth-child(1){text-align: right; white-space: nowrap; padding-right: 5px; padding-top:12px; vertical-align: middle;}</style>"+konyvSzoveg+"</body></html>";
            webData = Base64.encodeToString(webData.getBytes(), Base64.NO_PADDING);
            mWebView.loadData(webData, "text/html", "base64");

        } else {
            Intent intent = new Intent(EgyhaziActivity.this, KekSzovegActivity.class);
            intent.putExtra("par", paragrafus);
            intent.putExtra("keres", "");
            startActivity(intent);
        }
    }

    @Override
    public void onBackPressed() {
        if (mWebView.canGoBack()) {
            mWebView.goBack();
        } else  {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu, menu);
        searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setSubmitButtonEnabled(true);

        final TextView talalatSzoveg = new TextView(EgyhaziActivity.this);
        if(BeallitasokActivity.sotetTema) {
            talalatSzoveg.setBackgroundColor(getResources().getColor(R.color.sotetKeresoHatter));
        } else {
            talalatSzoveg.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        }
        talalatSzoveg.setTextColor(getResources().getColor(R.color.colorAccent));
        talalatSzoveg.setPadding(25, 5, 5, 5);
        talalatSzoveg.setTextSize(17);
        talalatSzoveg.setText("Nincs találat");
        final LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.RIGHT;
        talalatSzoveg.setLayoutParams(params);
        talalatSzoveg.setGravity(Gravity.LEFT);

        final ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) mWebView.getLayoutParams();

        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EgyhaziActivity.this.addContentView(talalatSzoveg, params);
                p.topMargin = 50;
                mWebView.setLayoutParams(p);
            }
        });
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                ((ViewGroup) talalatSzoveg.getParent()).removeView(talalatSzoveg);
                p.topMargin = 0;
                mWebView.setLayoutParams(p);
                return false;
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                View view = EgyhaziActivity.this.getCurrentFocus();
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
                mWebView.findNext(true);
                return true;
            }
            @Override
            public boolean onQueryTextChange(String query) {
                mWebView.findAllAsync(query.toString());
                return true;
            }
        });
        mWebView.setFindListener(new WebView.FindListener() {
            @Override
            public void onFindResultReceived(int activeMatchOrdinal, int numberOfMatches, boolean isDoneCounting) {
                int hely = 0;
                if(numberOfMatches > 0) {
                    hely = activeMatchOrdinal + 1;
                }
                if(numberOfMatches == 0) {
                    talalatSzoveg.setText("Nincs találat");
                } else {
                    talalatSzoveg.setText(hely +"/"+numberOfMatches + " találat");
                }
            }
        });
        return true;
    }
}
