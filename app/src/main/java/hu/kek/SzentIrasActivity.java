package hu.kek;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
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

import hu.kek.Adatok.AdatSzentiras;

public class SzentIrasActivity extends AppCompatActivity {

    private WebView mWebView;
    private float sortav = BeallitasokActivity.sortav;
    private float alapBetuMeret = BeallitasokActivity.alapBetuMeret;
    private SearchView searchView;
    private SearchManager searchManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Statikus.temaBeallit(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_szent_iras);
        getSupportActionBar().setTitle("Szentírási források");

        mWebView = (WebView) findViewById(R.id.szentIrasView);
        Statikus.webViewSzinBeallit(mWebView);
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
            /*
            @Override
            public void onPageFinished(WebView view, String url) {
                if(dialog.isShowing()) {
                    dialog.dismiss();
                }
            }*/
        });

        String text = "<html><body><style>body{line-height:"+sortav+"em; font-size:"+alapBetuMeret+"em;text-align: center; color:"+Statikus.szovegSzin+";} h1{margin: 20px 0px 25px 0px; font-size: 1.2em;font-weight: bold;} a{padding: 12px 0px; display: block; color:"+Statikus.szovegSzin+"; text-decoration:none; border-bottom: 1px solid #ababab;} a:nth-of-type(1), a:nth-of-type(41){border-top: 1px solid #ababab;}</style>"+ AdatSzentiras.szentirasKonyvek+"</body></html>";
        text = Base64.encodeToString(text.getBytes(), Base64.NO_PADDING);
        mWebView.loadData(text, "text/html", "base64");
    }

    public void kiir (String paragrafus) {
        if(paragrafus.contains("#b")) {
            searchView.setVisibility(View.GONE);
            int konyv = Integer.parseInt(paragrafus.split("#b")[1]);
            String konyvSzoveg = "";
            for (int i = 0; i< AdatSzentiras.szentiras[konyv].length; ++i){
                konyvSzoveg += AdatSzentiras.szentiras[konyv][i];
            }
            String text = "<html><body><style>body, table{line-height:"+sortav+"em; font-size:"+alapBetuMeret+"em; color:"+Statikus.szovegSzin+";} h2,span{margin: 0; padding: 0; border: 0; font-size: 100%; font: inherit;vertical-align: baseline; padding: 15px 0px 2px 0px;} h2{font-size: 1.05em; font-weight: bold; text-align:center; margin-bottom: 1em;} a{padding: 8px 0px 8px 14px; white-space:nowrap; display: inline-block; text-decoration: none; color:"+Statikus.linkSzin+";} table {margin: 0 auto; /*border-spacing: 0px;*/} td{border-bottom: 1px solid #ababab;} td:nth-child(1){text-align: right; white-space: nowrap; padding-right: 5px; padding-top:12px; vertical-align: middle;}</style>"+konyvSzoveg+"</body></html>";
            text = Base64.encodeToString(text.getBytes(), Base64.NO_PADDING);
            mWebView.loadData(text, "text/html", "base64");
        } else {
            Intent intent = new Intent(SzentIrasActivity.this, KekSzovegActivity.class);
            intent.putExtra("par", paragrafus);
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

        final TextView talalatSzoveg = new TextView(SzentIrasActivity.this);
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
                SzentIrasActivity.this.addContentView(talalatSzoveg, params);
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
                View view = SzentIrasActivity.this.getCurrentFocus();
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
