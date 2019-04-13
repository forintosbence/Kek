package hu.kek;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.text.Html;
import android.view.ContextThemeWrapper;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.Menu;
import android.view.MotionEvent;
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
import hu.kek.Adatok.AdatCimek;
import hu.kek.Adatok.AdatLabjegyzet;
import hu.kek.Adatok.AdatSzamozatlan;
import hu.kek.Adatok.AdatTartalom;
import hu.kek.Adatok.Adatok;

public class KekSzovegActivity extends AppCompatActivity {

    private WebView mWebView;
    private ProgressDialog betoltDialog;
    private SearchManager searchManager;
    private SearchView searchView;
    private GestureDetector gestureDetector;
    private String kereses = "";
    private int eltuntetSzamol = 0;
    private String paragrafusok;
    private float sortav = BeallitasokActivity.sortav;
    private float alapBetuMeret = BeallitasokActivity.alapBetuMeret;
    private int szakaszCimek = -1;
    private static String nagySzoveg = "Valami hiba van....";
    private static boolean nagy = false;
    public static boolean nagyKesz = false;
    private static ArrayList<Activity> history = new ArrayList<>();
    private String megjelenitettParagrafus = "";
    private  FloatingActionButton kedvencekGomb;

    protected void onCreate(Bundle savedInstanceState) {
        Statikus.temaBeallit(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kek_szoveg);
        history.add(this);
        final Intent intent = getIntent();
        if (intent.getStringExtra("fejlec") != null) {
            getSupportActionBar().setTitle(Html.fromHtml(intent.getStringExtra("fejlec").replaceAll("<a(.*)a>", "")).toString());
        } else {
            getSupportActionBar().setTitle("Katekizmus");
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mWebView = (WebView) findViewById(R.id.KekTartalomSzoveg);
        Statikus.webViewSzinBeallit(mWebView);
        WebSettings settings = mWebView.getSettings();
        settings.setDefaultTextEncodingName("utf-8");
        settings.setDefaultFontSize(18);
        settings.setBuiltInZoomControls(true);
        settings.setDisplayZoomControls(false);
        mWebView.setWebChromeClient(new WebChromeClient());

        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if(url.contains("#lj")) {
                    labjegyzet(url);
                } else {
                    Intent intent = new Intent(KekSzovegActivity.this, KekSzovegActivity.class);
                    intent.putExtra("par", url);
                    intent.putExtra("cim", szakaszCimek);
                    startActivity(intent);
                }
                return true;
            }
            @Override
            public void onPageFinished(WebView view, String url) {
                if(betoltDialog != null && betoltDialog.isShowing()) {
                    try {
                        betoltDialog.dismiss();
                    } catch (Exception e) {}
                }
            }
        });

        mWebView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
            }
        });


        kedvencekGomb = (FloatingActionButton) findViewById(R.id.kedvencekGomb);
        if(BeallitasokActivity.sotetTema) {
            kedvencekGomb.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.dialogSotetHatter)));
        }
        kedvencekGomb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(KedvencekActivity.ellenorzes(megjelenitettParagrafus)) {
                    KedvencekActivity.torol(megjelenitettParagrafus);
                    kedvencMentes();
                    Drawable myFabSrc = getResources().getDrawable(android.R.drawable.btn_star_big_off);
                    Drawable willBeWhite = myFabSrc.getConstantState().newDrawable();
                    willBeWhite.mutate().setColorFilter(Color.WHITE, PorterDuff.Mode.MULTIPLY);
                    kedvencekGomb.setImageDrawable(willBeWhite);
                } else {
                    KedvencekActivity.hozzaad(megjelenitettParagrafus);
                    kedvencMentes();
                    Drawable myFabSrc = getResources().getDrawable(android.R.drawable.btn_star_big_on);
                    Drawable willBeWhite = myFabSrc.getConstantState().newDrawable();
                    willBeWhite.mutate().setColorFilter(Color.WHITE, PorterDuff.Mode.MULTIPLY);
                    kedvencekGomb.setImageDrawable(willBeWhite);
                }
            }
        });

        if(BeallitasokActivity.kedvencekTiltva) {
            kedvencekGomb.setVisibility(View.GONE);
        }


        GestureDetector.SimpleOnGestureListener simpleOnGestureListener = new GestureDetector.SimpleOnGestureListener() {
            public boolean onDown(MotionEvent event) {
                return false;
            }

            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                String elem = paragrafusok;
                if(elem != null && !elem.contains("kekapp")) {
                    int kapcsoloElem = Integer.parseInt(elem);
                    if(e1.getX() - e2.getX() > 220 && Math.abs(velocityX) > 300) {
                        int betoltendo = kapcsoloElem + 1;
                        while (betoltendo < Adatok.kapcsolo.length && Adatok.kapcsolo[betoltendo].length == 0) {
                            ++betoltendo;
                        }
                        if(betoltendo < Adatok.kapcsolo.length) {
                            getSupportActionBar().setTitle(Html.fromHtml((String) AdatCimek.getCim(betoltendo+1).replaceAll("<a(.*)a>", "")).toString());
                            paragrafusok = betoltendo+"";
                            kiir(betoltendo+"");
                            kedvencekGombAllit();
                        }
                    } else if (e2.getX() - e1.getX() > 220 && Math.abs(velocityX) > 300) {
                        int betoltendo = kapcsoloElem - 1;
                        while (betoltendo > 0 && Adatok.kapcsolo[betoltendo].length == 0) {
                            --betoltendo;
                        }
                        if(betoltendo >= 0) {
                            getSupportActionBar().setTitle(Html.fromHtml((String) AdatCimek.getCim(betoltendo+1).replaceAll("<a(.*)a>", "")).toString());
                            paragrafusok = betoltendo+"";
                            kiir(betoltendo+"");
                            kedvencekGombAllit();
                        }
                    }
                }
                return false;
            }
        };

        gestureDetector = new GestureDetector(this, simpleOnGestureListener);
        paragrafusok = intent.getStringExtra("par");
        if(paragrafusok.contains("–")) {
            paragrafusok = paragrafusok.replace("–", "-");
        } else if (paragrafusok.contains("%E2%80%93")) {
            paragrafusok = paragrafusok.replace("%E2%80%93", "-");
        }
        //szakaszCimek = intent.getIntExtra("szakaszCimek", -1);
        if(intent.getStringExtra("keres") != null) {
            kereses = intent.getStringExtra("keres");
        }
        nagy = intent.getBooleanExtra("nagy", false);
        kedvencekGombAllit();
        kiir(paragrafusok);
        BeallitasokActivity.kepernyoLetilt(this, BeallitasokActivity.kepernyozarTilt);
    }

    private void kedvencekGombAllit() {
        if(KedvencekActivity.ellenorzes(paragrafusok)) {
            Drawable myFabSrc = getResources().getDrawable(android.R.drawable.btn_star_big_on);
            Drawable willBeWhite = myFabSrc.getConstantState().newDrawable();
            willBeWhite.mutate().setColorFilter(Color.WHITE, PorterDuff.Mode.MULTIPLY);
            kedvencekGomb.setImageDrawable(willBeWhite);
        } else {
            Drawable myFabSrc = getResources().getDrawable(android.R.drawable.btn_star_big_off);
            Drawable willBeWhite = myFabSrc.getConstantState().newDrawable();
            willBeWhite.mutate().setColorFilter(Color.WHITE, PorterDuff.Mode.MULTIPLY);
            kedvencekGomb.setImageDrawable(willBeWhite);
        }
    }

    private void kedvencMentes () {
        SharedPreferences sharedPref = getSharedPreferences("beallitasok", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("kedvencek", KedvencekActivity.kedvencek);
        editor.commit();
    }

    public void kiir (String kapcsoloElemString) {
        megjelenitettParagrafus = kapcsoloElemString;
        String szoveg = "Valami hiba van....";
        if(nagy) {
            if(BeallitasokActivity.sotetTema) {
                betoltDialog = new ProgressDialog(KekSzovegActivity.this, R.style.DialogSotet);
            } else {
                betoltDialog = new ProgressDialog(KekSzovegActivity.this, R.style.Dialog);
            }
            betoltDialog.setMessage(Statikus.szovegFormaz("Betöltés..."));
            betoltDialog.setCancelable(true);
            betoltDialog.show();
            szoveg = nagySzoveg;
        } else {
            try {
                szoveg = "";
                if(kapcsoloElemString.contains("kekapp")) {
                    String kapcsoloElemTomb[] = new String[2];
                    if(kapcsoloElemString.contains("#p")) {
                        kapcsoloElemTomb[0] = "p";
                        kapcsoloElemTomb[1] = kapcsoloElemString.split("#p")[1];
                    } else if (kapcsoloElemString.contains("#s")) {
                        kapcsoloElemTomb[0] = "s";
                        kapcsoloElemTomb[1] = kapcsoloElemString.split("#s")[1];
                    } else if (kapcsoloElemString.contains("#k")) {
                        kapcsoloElemTomb[0] = "k";
                        kapcsoloElemTomb[1] = kapcsoloElemString.split("#k")[1];
                    }
                    int kezdo = 0;
                    int vege = 0;
                    if(kapcsoloElemTomb[1].contains("-")) {
                        kezdo = Integer.parseInt(kapcsoloElemTomb[1].split("-")[0]);
                        vege = Integer.parseInt(kapcsoloElemTomb[1].split("-")[1]);
                        if(vege - kezdo > 200) {
                            if(BeallitasokActivity.sotetTema) {
                                betoltDialog = new ProgressDialog(KekSzovegActivity.this, R.style.DialogSotet);
                            } else {
                                betoltDialog = new ProgressDialog(KekSzovegActivity.this, R.style.Dialog);
                            }
                            betoltDialog.setMessage(Statikus.szovegFormaz("Betöltés..."));
                            betoltDialog.setCancelable(true);
                            betoltDialog.show();
                        }
                    } else {
                        kezdo = Integer.parseInt(kapcsoloElemTomb[1]);
                        vege = kezdo;
                    }
                    if(BeallitasokActivity.hivatkozasCimek){
                        int kezdoElem = 0;
                        int vegeElem = 0;
                        //Az adatok.sorrend tömbben megkeresi hogy hányadik elemek a kezdő és vége paragrafusok
                        boolean talaltCim = false;
                        for (int i = 0; i< Adatok.sorrend.length; ++i) {
                            if(Adatok.sorrend[i].equals(kapcsoloElemTomb[0]+kezdo)) {
                                int vissza = 1;
                                while (i-vissza > -1 && (Adatok.sorrend[i-vissza].contains("c") || Adatok.sorrend[i-vissza].contains("s"))) {
                                    ++vissza;
                                    talaltCim = true;
                                }
                                kezdoElem = i-(vissza-1);
                            }
                            if(Adatok.sorrend[i].equals(kapcsoloElemTomb[0]+vege)) {
                                vegeElem = i;
                            }
                        }
                        if(!talaltCim && !BeallitasokActivity.hivatkozasCimStruktura) {
                            for (int i = kezdoElem; i >= 0; --i) {
                                if(Adatok.sorrend[i].contains("c")) {
                                    szoveg += AdatCimek.getCim(Integer.parseInt(Adatok.sorrend[i].substring(1)));
                                    break;
                                }
                            }
                        }
                        if(BeallitasokActivity.hivatkozasCimStruktura) {
                            szoveg += Statikus.cimKeres(kapcsoloElemTomb[0]+kezdo, true);
                        }
                        for(int i = kezdoElem; i<= vegeElem; ++i) {
                            if(Adatok.sorrend[i].contains("p")) {
                                szoveg += AdatTartalom.getParagrafus(Integer.parseInt(Adatok.sorrend[i].substring(1)));
                            } else if (Adatok.sorrend[i].contains("c")) {
                                szoveg += AdatCimek.getCim(Integer.parseInt(Adatok.sorrend[i].substring(1)));
                            } else if (Adatok.sorrend[i].contains("s")) {
                                szoveg += AdatSzamozatlan.getSzamozatlan(Integer.parseInt(Adatok.sorrend[i].substring(1)));
                            } else if (Adatok.sorrend[i].contains("k")) {
                                szoveg += AdatSzamozatlan.getSzamozatlan(Integer.parseInt(Adatok.sorrend[i].substring(1)));
                            }
                        }
                    } else {
                        if (kapcsoloElemTomb[0].contains("k")) {
                            szoveg += AdatSzamozatlan.getSzamozatlan(kezdo);
                        } else if(kapcsoloElemTomb[0].contains("s")) {
                            szoveg += AdatSzamozatlan.getSzamozatlan(kezdo);
                        } else {
                            for (int i = kezdo; i <= vege; ++i ) {
                                szoveg += AdatTartalom.getParagrafus(i);
                            }
                        }
                    }
                } else {
                    //itt a kapcsoló tábla alpján állítja össsze, nem a sorrend alapján
                    int kapcsoloElem = Integer.parseInt(kapcsoloElemString);
                    if(BeallitasokActivity.tartalomCimStruktura) {
                        szoveg += Statikus.cimKeres(Adatok.kapcsolo[kapcsoloElem][0]);
                    } else if(BeallitasokActivity.tartalomCimek) {
                        int vizsgalt = kapcsoloElem;
                        while (vizsgalt > 0 && Adatok.kapcsolo[vizsgalt-1].length == 0) {
                            --vizsgalt;
                        }
                        for (int i = vizsgalt; i<=kapcsoloElem; ++i) {
                            szoveg += AdatCimek.getCim(i+1);
                        }
                    }
                    for (int i = 0; i < Adatok.kapcsolo[kapcsoloElem].length; ++i) {
                        if(Adatok.kapcsolo[kapcsoloElem][i].contains("p")) {
                            szoveg += AdatTartalom.getParagrafus(Integer.parseInt(Adatok.kapcsolo[kapcsoloElem][i].substring(1)));
                        } else {
                            szoveg += AdatSzamozatlan.getSzamozatlan(Integer.parseInt(Adatok.kapcsolo[kapcsoloElem][i].substring(1)));
                        }
                    }
                }

            } catch (Exception e) {}
        }
        szoveg = szoveg.replaceAll("#p", "kekapp://#p");
        szoveg = szoveg.replaceAll("#lj", "kekapp://#lj");
        String magasit = "";
        if(!BeallitasokActivity.kedvencekTiltva) {
            magasit = "<br /><br />";
        }
        mWebView.loadDataWithBaseURL(null, "<html><body><style>body, table{line-height:"+sortav+"em; font-size:"+alapBetuMeret+"em; color:"+Statikus.szovegSzin+";} h1,h2,h3,h4,h5,h6,h7{margin: 0; padding: 0; border: 0; font-size: 100%; font: inherit;vertical-align: baseline;} h1{padding: 10px 0px; font-size: 1.4em; font-weight: bold; text-align:center;} h2{padding: 10px 0px;font-size: 1.3em; font-weight: bold; text-align:center;} h3{padding: 10px 0px;font-size: 1.2em; font-weight: bold; text-align:center;} h4{padding: 10px 0px;font-size: 1.1em; font-weight: bold;text-align:center;} h5{padding: 7px 0px;; font-size: 1em; font-weight: bold;text-align:center;} h6{padding: 7px 0px;font-size: 0.9em; font-weight: bold;} h7{padding: 7px 0px;display: block; font-size: 0.9em; text-transform: uppercase;} p{padding: 0px; margin: 7px 0px 17px 0px;text-align: justify; } a{font-style:normal; text-decoration: none;} .labjegylink{margin:5px 0px; color:"+Statikus.linkSzin+";} .parlink{margin-left:10px; color:"+Statikus.linkSzin+";} .osszefoglalas{padding: 10px 0px; font-weight: bold; display:block;} .kiemeltKetto, .eloszo{padding:10px 0px; font-size: 1.2em; font-weight: bold; text-transform: uppercase; display:block; text-align:center;} .behuzas{margin-left: 2em;} .idezet, .szidezet{font-style: italic; display:block; margin: 1em 0em 1em 1em;} .szidezet{text-align: justify;} .kulonallo{margin: 1em 0em;} .szkulonallo{display: inline-block; margin: 1em 0em;} </style>"+szoveg+magasit+"</body></html>", "text/html", "UTF-8", null);
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {

        getMenuInflater().inflate(R.menu.menu, menu);
        searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setSubmitButtonEnabled(true);

        final TextView talalatSzoveg = new TextView(KekSzovegActivity.this);
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
                KekSzovegActivity.this.addContentView(talalatSzoveg, params);
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
                View view = KekSzovegActivity.this.getCurrentFocus();
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
                    View view = KekSzovegActivity.this.getCurrentFocus();
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
        searchView.setQuery(keresendo, true);
    }

    private void labjegyzet(String url) {
        int szam = Integer.parseInt(url.split("#lj")[1]);
        AlertDialog alertDialog;
        if(BeallitasokActivity.sotetTema) {
            alertDialog = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AlertDialogSotet)).create();
        } else {
            alertDialog = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AlertDialogVilagos)).create();
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
    public Intent getSupportParentActivityIntent() {
        if(!searchView.isIconified()) {
            BeallitasokActivity.kepernyoLetilt(this, false);
            super.onBackPressed();
        } else {
            for (int i = 0; i< history.size(); ++i) {
                history.get(i).finish();
            }
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

    public static void osszerak (String kapcsoloElemString) {
        nagyKesz = false;
        nagySzoveg = "";
        if(kapcsoloElemString.contains("kekapp")) {
            String kapcsoloElemTomb[] = new String[2];
            if (kapcsoloElemString.contains("#p")) {
                kapcsoloElemTomb[0] = "p";
                kapcsoloElemTomb[1] = kapcsoloElemString.split("#p")[1];
            } else if (kapcsoloElemString.contains("#s")) {
                kapcsoloElemTomb[0] = "s";
                kapcsoloElemTomb[1] = kapcsoloElemString.split("#s")[1];
            } else if (kapcsoloElemString.contains("#k")) {
                kapcsoloElemTomb[0] = "k";
                kapcsoloElemTomb[1] = kapcsoloElemString.split("#k")[1];
            }
            int kezdo = 0;
            int vege = 0;
            if (kapcsoloElemTomb[1].contains("-")) {
                kezdo = Integer.parseInt(kapcsoloElemTomb[1].split("-")[0]);
                vege = Integer.parseInt(kapcsoloElemTomb[1].split("-")[1]);
            } else {
                kezdo = Integer.parseInt(kapcsoloElemTomb[1]);
                vege = kezdo;
            }
            if (BeallitasokActivity.hivatkozasCimek) {
                int kezdoElem = 0;
                int vegeElem = 0;
                boolean talaltCim = false;
                for (int i = 0; i< Adatok.sorrend.length; ++i) {
                    if(Adatok.sorrend[i].equals(kapcsoloElemTomb[0]+kezdo)) {
                        int vissza = 1;
                        while (i-vissza > -1 && (Adatok.sorrend[i-vissza].contains("c") || Adatok.sorrend[i-vissza].contains("s"))) {
                            ++vissza;
                            talaltCim = true;
                        }
                        kezdoElem = i-(vissza-1);
                    }
                    if(Adatok.sorrend[i].equals(kapcsoloElemTomb[0]+vege)) {
                        vegeElem = i;
                    }
                }
                if(!talaltCim && !BeallitasokActivity.hivatkozasCimStruktura) {
                    for (int i = kezdoElem; i >= 0; --i) {
                        if(Adatok.sorrend[i].contains("c")) {
                            nagySzoveg += AdatCimek.getCim(Integer.parseInt(Adatok.sorrend[i].substring(1)));
                            break;
                        }
                    }
                }
                if(BeallitasokActivity.hivatkozasCimStruktura) {
                    nagySzoveg += Statikus.cimKeres(kapcsoloElemTomb[0]+kezdo, true);
                }
                for (int i = kezdoElem; i <= vegeElem; ++i) {
                    if (Adatok.sorrend[i].contains("p")) {
                        nagySzoveg += AdatTartalom.getParagrafus(Integer.parseInt(Adatok.sorrend[i].substring(1)));
                    } else if (Adatok.sorrend[i].contains("c")) {
                        nagySzoveg += AdatCimek.getCim(Integer.parseInt(Adatok.sorrend[i].substring(1)));
                    } else if (Adatok.sorrend[i].contains("s")) {
                        nagySzoveg += AdatSzamozatlan.getSzamozatlan(Integer.parseInt(Adatok.sorrend[i].substring(1)));
                    } else if (Adatok.sorrend[i].contains("k")) {
                        nagySzoveg += AdatSzamozatlan.getSzamozatlan(Integer.parseInt(Adatok.sorrend[i].substring(1)));
                    }
                }
            } else {
                for (int i = kezdo; i <= vege; ++i) {
                    nagySzoveg += AdatTartalom.getParagrafus(i);
                }
            }
            nagyKesz = true;
        }
    }
}
