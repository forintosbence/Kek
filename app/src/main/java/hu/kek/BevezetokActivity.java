package hu.kek;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import android.os.Bundle;
import android.util.Base64;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.TextView;

import hu.kek.Adatok.AdatApostoli;
import hu.kek.Adatok.AdatLabjegyzet;
import hu.kek.Adatok.Adatok;

public class BevezetokActivity extends AppCompatActivity {

    private WebView mWebView;
    private SearchManager searchManager;
    private SearchView searchView;
    private float sortav = BeallitasokActivity.sortav;
    private float alapBetuMeret = BeallitasokActivity.alapBetuMeret;
    public static int tartalom = 0;
    private String kereses = "";
    private int eltuntetSzamol = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Statikus.temaBeallit(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apostoli);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mWebView = (WebView) findViewById(R.id.ApostoliText);
        Statikus.webViewSzinBeallit(mWebView);
        WebSettings settings = mWebView.getSettings();
        settings.setDefaultTextEncodingName("utf-8");
        settings.setDefaultFontSize(18);
        settings.setBuiltInZoomControls(true);
        settings.setDisplayZoomControls(false);
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                labjegyzet(url);
                return true;
            }

        });

        if(getIntent().getStringExtra("keres") != null) {
            kereses = getIntent().getStringExtra("keres");
        }

        String text = "";
        if (tartalom == 1) {
            getSupportActionBar().setTitle("Kiadói impresszum");
            text = "<html><body><style>p {line-height:"+sortav+"em; font-size:"+alapBetuMeret+"em; color:"+Statikus.szovegSzin+"} p{text-align: justify;}</style>" + Adatok.kiadoImpesszum+ "</body></html>";
        } else if (tartalom == 2) {
            getSupportActionBar().setTitle("Szentírási könyvek");
            text = "<html><body><style>body, table{line-height:"+sortav+"em; font-size:"+alapBetuMeret+"em; color:"+Statikus.szovegSzin+";} table{margin: 0 auto; /*border-spacing:0px;*/} h1{font-size:1.5em; text-align: center; margin-top: 15px;} tr td:first-child{text-align: right;} td{border-bottom: 1px solid #ababab; margin:0px;} td{padding: 7px 7px;} a{text-decoration: none;}</style>" + Adatok.szentIrasRov + "</body></html>";
        } else if(tartalom == 3) {
            getSupportActionBar().setTitle("Rövidítések jegyzéke");
            text = "<html><body><style>body {line-height:"+sortav+"em; font-size:"+alapBetuMeret+"em; color:"+Statikus.szovegSzin+";} h1{font-size:1.5em; text-align: center; margin-top: 15px;} h2{font-size:1.2em; margin-bottom: 0px;} h2:first-of-type{margin-top:20px;} p{margin: 0px;} span{display:block; margin-top: 40px; text-align:center;} a{text-decoration: none;}</style>" + Adatok.roviditesek+ "</body></html>";
        } else if (tartalom == 4) {
            getSupportActionBar().setTitle("LAETAMUR MAGNOPERE");
            text = "<html><body><style>p {line-height:"+sortav+"em; font-size:"+alapBetuMeret+"em; color:"+Statikus.szovegSzin+"} a{text-decoration: none;}</style>" + AdatApostoli.laetamur+ "</body></html>";
        } else if (tartalom == 5) {
            getSupportActionBar().setTitle("FIDEI DEPOSITUM");
            text = "<html><body><style>p {line-height:"+sortav+"em; font-size:"+alapBetuMeret+"em; color:"+Statikus.szovegSzin+"} a{text-decoration: none; color:"+Statikus.linkSzin+";}</style>" + AdatApostoli.fidei+ "</body></html>";
        }
        text = text.replaceAll("#lj", "kekapp://#lj");

        text = Base64.encodeToString(text.getBytes(), Base64.NO_PADDING);
        mWebView.loadData(text, "text/html", "base64");

        BeallitasokActivity.kepernyoLetilt(this, BeallitasokActivity.kepernyozarTilt);
    }

    private void labjegyzet(String url) {
        int szam = Integer.parseInt(url.split("#lj")[1]);
        AlertDialog alertDialog;
        if(BeallitasokActivity.sotetTema) {
            alertDialog = new AlertDialog.Builder(new ContextThemeWrapper(BevezetokActivity.this, R.style.AlertDialogSotet)).create();
        } else {
            alertDialog = new AlertDialog.Builder(new ContextThemeWrapper(BevezetokActivity.this, R.style.AlertDialogVilagos)).create();
        }
        alertDialog.setTitle("Forrás:");
        alertDialog.setMessage(AdatLabjegyzet.getLabjegyzet(szam));
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Bezárás",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu, menu);
        searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setSubmitButtonEnabled(true);

        final TextView talalatSzoveg = new TextView(BevezetokActivity.this);
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
                BevezetokActivity.this.addContentView(talalatSzoveg, params);
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
                View view = BevezetokActivity.this.getCurrentFocus();
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
                    View view = BevezetokActivity.this.getCurrentFocus();
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

    @Override
    public Intent getSupportParentActivityIntent() {
        if(!searchView.isIconified()) {
            BeallitasokActivity.kepernyoLetilt(this, false);
            super.onBackPressed();
        } else {
            finish();
        }
        return null;
    }

    @Override
    public void onBackPressed() {
        if(!searchView.isIconified()) {
            searchView.setIconified(true);
        } else  {
            BeallitasokActivity.kepernyoLetilt(this, false);
            super.onBackPressed();
        }
    }
}
