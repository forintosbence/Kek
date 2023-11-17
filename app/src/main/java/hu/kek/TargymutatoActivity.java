package hu.kek;

import android.app.ProgressDialog;
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

import java.util.ArrayList;

import hu.kek.Adatok.AdatSzentiras;
import hu.kek.Adatok.AdatTematikus;


public class TargymutatoActivity extends AppCompatActivity {

    private WebView mWebView;
    private static String szoveg = "";
    public static boolean szovegOsszerakva = false;
    private ProgressDialog dialog;
    private float sortav = BeallitasokActivity.sortav;
    private float alapBetuMeret = BeallitasokActivity.alapBetuMeret;
    private SearchView searchView;
    private SearchManager searchManager;
    private int eltuntetSzamol = 0;
    private String kereses = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Statikus.temaBeallit(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_targymutato);
        getSupportActionBar().setTitle("Tárgymutató");

        if(BeallitasokActivity.sotetTema) {
            dialog = new ProgressDialog(this, R.style.DialogSotet);
        } else {
            dialog = new ProgressDialog(this, R.style.Dialog);
        }
        dialog.setMessage(Statikus.szovegFormaz("Betöltés..."));
        dialog.setCancelable(true);
        dialog.show();


        mWebView = (WebView) findViewById(R.id.targymutatoView);
        Statikus.webViewSzinBeallit(mWebView);
        WebSettings settings = mWebView.getSettings();
        settings.setDefaultTextEncodingName("utf-8");
        settings.setDefaultFontSize(18);
        settings.setBuiltInZoomControls(true);
        settings.setDisplayZoomControls(false);
        mWebView.setWebChromeClient(new WebChromeClient());
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
                if(dialog != null && dialog.isShowing()) {
                    try {
                        dialog.dismiss();
                    } catch (Exception e) {}
                }
            }
        });

        String szoveg = "";
        String melyik = getIntent().getStringExtra("melyik");
        if(melyik == null) {
            finish();
        }else if(melyik.equals("")) {
            szoveg = "<h1 style=\"text-align:center;\">Nincs találat!</h1>";
        } else if(melyik.contains("kekapp:/")) {
            int tomb = Integer.parseInt(melyik.split("#v")[1]);
            for(int i = 0; i< AdatTematikus.tematikus[tomb].length; ++i) {
                szoveg += AdatTematikus.tematikus[tomb][i];
            }
        } else {
            //keresés
            ArrayList<Integer[]> talaltok = new ArrayList<>();
            for (int i = 0; i < AdatTematikus.tematikusTiszta.length; ++i) {
                for (int j = 0; j < AdatTematikus.tematikusTiszta[i].length; ++j) {
                    if(AdatTematikus.tematikusTiszta[i][j].toLowerCase().contains(melyik.toLowerCase())) {
                        talaltok.add(new Integer[] {i, j});
                    }
                }
            }
            for(int i =0; i < talaltok.size(); ++i) {
                szoveg += AdatTematikus.tematikus[talaltok.get(i)[0]][talaltok.get(i)[1]];
            }
            if(szoveg.equals("")) {
                szoveg = "<h1 style=\"text-align:center;\">Nincs találat!</h1>";
            }
            kereses = melyik;
        }
        String text = "<html><body><style>body{line-height:"+sortav+"em; font-size:"+alapBetuMeret+"em; color:"+Statikus.szovegSzin+";} h1,h2,span{margin: 0; padding: 0; border: 0; font-size: 100%; font: inherit;vertical-align: baseline; padding: 15px 0px 2px 0px;} h1{font-size: 1.05em; font-weight: bold;} h2{padding-left: 10px; font-size: 1.05em; font-weight: bold;} span{display:block; padding: 6px 0px; font-size: 1.05em; line-height: 1.55em; margin-left: 1em;} a{padding: 6px 0px 6px 12px; white-space:nowrap; text-decoration: none; color:"+Statikus.linkSzin+";} #elso{padding-top: 0px}</style>"+szoveg+"</body></html>";
        text = Base64.encodeToString(text.getBytes(), Base64.NO_PADDING);
        mWebView.loadData(text, "text/html", "base64");
    }

    public void kiir (String paragrafus) {
        if(paragrafus.contains("#b")) {
            int konyv = Integer.parseInt(paragrafus.split("#b")[1]);
            String konyvSzoveg = "";
            for (int i = 0; i< AdatSzentiras.szentiras[konyv].length; ++i){
                konyvSzoveg += AdatSzentiras.szentiras[konyv][i];
            }
            String text = "<html><body><style>body, table{line-height:"+sortav+"em; font-size:"+alapBetuMeret+"em; color:"+Statikus.szovegSzin+";} h2,span{margin: 0; padding: 0; border: 0; font-size: 100%; font: inherit;vertical-align: baseline; padding: 15px 0px 2px 0px;} h2{font-size: 1.05em; font-weight: bold; text-align:center;} a{padding: 8px 0px 8px 14px; white-space:nowrap; display: inline-block; text-decoration: none; color:"+Statikus.linkSzin+";} td:nth-child(1){white-space: nowrap; padding-right: 5px; padding-top:12px; vertical-align: text-top;}</style>"+konyvSzoveg+"</body></html>";
            text = Base64.encodeToString(text.getBytes(), Base64.NO_PADDING);
            mWebView.loadData(text, "text/html", "base64");
        } else {
            Intent intent = new Intent(TargymutatoActivity.this, KekSzovegActivity.class);
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
    public boolean onCreateOptionsMenu(final Menu menu) {

        getMenuInflater().inflate(R.menu.menu, menu);
        searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setSubmitButtonEnabled(true);

        final TextView talalatSzoveg = new TextView(TargymutatoActivity.this);
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
                TargymutatoActivity.this.addContentView(talalatSzoveg, params);
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
                View view = TargymutatoActivity.this.getCurrentFocus();
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
                if(!kereses.equals("") && eltuntetSzamol < kereses.length()) {
                    ++eltuntetSzamol;
                    View view = TargymutatoActivity.this.getCurrentFocus();
                    if (view != null) {
                        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                }
            }
        });
        if(!kereses.equals("")) {
            keres(kereses);
        }
        return true;
    }

    private void keres(String keresendo) {
        searchView.onActionViewExpanded();
        searchView.setQuery(keresendo, false);
    }

}
