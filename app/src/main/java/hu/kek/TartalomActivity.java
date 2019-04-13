package hu.kek;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.text.Html;
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

import hu.kek.Adatok.AdatCimek;
import hu.kek.Adatok.Adatok;

public class TartalomActivity extends AppCompatActivity {

    private WebView mWebView;
    private static String szoveg = "";
    private ProgressDialog dialog;
    public static boolean szovegOsszerakva = false;
    private float sortav = BeallitasokActivity.sortav;
    private float alapBetuMeret = BeallitasokActivity.alapBetuMeret;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Statikus.temaBeallit(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tartalom);

        getSupportActionBar().setTitle("Tartalom");
        if(BeallitasokActivity.sotetTema) {
            dialog = new ProgressDialog(this, R.style.DialogSotet);
        } else {
            dialog = new ProgressDialog(this, R.style.Dialog);
        }
        dialog.setMessage(Statikus.szovegFormaz("Betöltés..."));
        dialog.setCancelable(true);
        dialog.show();

        mWebView = (WebView) findViewById(R.id.TartalomView);
        WebSettings settings = mWebView.getSettings();
        settings.setDefaultTextEncodingName("utf-8");
        settings.setDefaultFontSize(18);
        settings.setBuiltInZoomControls(true);
        settings.setDisplayZoomControls(false);

        mWebView.setWebChromeClient(new WebChromeClient());
        Statikus.webViewSzinBeallit(mWebView);
        settings.setJavaScriptEnabled(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);

        mWebView.setWebViewClient(new WebViewClient()
        {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                kiir(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                if(dialog!= null && dialog.isShowing()) {
                    try {
                        dialog.dismiss();
                    } catch (Exception e) {}
                }
            }



        });
        mWebView.loadData("<html><body><style>body{ /*background-color:#fde9ac;*/line-height:"+sortav+"em; font-size:"+alapBetuMeret+"em;} h1,h2,h3,h4,h5,h6,h7,span{margin: 0; padding: 0; border: 0; font-size: 100%; font: inherit;vertical-align: baseline; display:inline-block; max-width:82%;} a{text-decoration: none; color:"+Statikus.szovegSzin+"; padding: 12px 0px; display: inline-block; border-bottom: 1px solid #ababab; width:100%;} h1{font-size: 1.4em; font-weight: bold;} h2{padding-left: 10px; font-size: 1.3em; font-weight: bold;} h3{padding-left: 15px; font-size: 1.2em; font-weight: bold;} h4{padding-left: 20px; font-size: 1.1em; font-weight: bold;} h5{padding-left: 25px; font-size: 1em; font-weight: bold;} h6{padding-left: 30px; font-size: 0.9em; font-weight: bold;} h7{padding-left: 40px; font-size: 0.9em; text-transform: uppercase;} .osszefoglalas{padding-left: 40px; font-size: 1em; font-weight: bold;} .kiemeltKetto{padding-left: 10px; font-size: 1.2em; font-weight: bold; text-transform: uppercase;} .eloszo{padding-left: 25px; font-size: 1.1em;} .tov{float:right; font-size: 1.9em; margin-right:2%; color: #ababab;}</style>"+szoveg+"</body></html>", "text/html; charset=utf-8", "UTF-8");

    }

    public void kiir (String paragrafus) {
        if(paragrafus.contains("#e")) {
            BevezetokActivity.tartalom = Integer.parseInt(paragrafus.split("#e")[1]);
            Intent intent = new Intent(TartalomActivity.this, BevezetokActivity.class);
            startActivity(intent);
        } else {
            int kapcsoloElem = Integer.parseInt(paragrafus.split("#t")[1])-1;
            if(Adatok.kapcsolo[kapcsoloElem].length > 0) {
                Intent intent = new Intent(TartalomActivity.this, KekSzovegActivity.class);
                intent.putExtra("par", kapcsoloElem+"");
                intent.putExtra("fejlec", AdatCimek.getCim(kapcsoloElem+1));
                startActivity(intent);
            }
        }

    }

    public static Thread szovegOsszerak = new Thread() {
        @Override
        public void run() {
            for (int i = 0; i < AdatCimek.tartalom.length; ++i){
                szoveg += AdatCimek.tartalom[i];
            }
            szovegOsszerakva = true;
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        final SearchView searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setSubmitButtonEnabled(true);

        final TextView talalatSzoveg = new TextView(TartalomActivity.this);
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
                TartalomActivity.this.addContentView(talalatSzoveg, params);
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
                View view = TartalomActivity.this.getCurrentFocus();
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



