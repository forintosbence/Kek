package hu.kek;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.util.ArrayList;
import java.util.Arrays;

import hu.kek.Adatok.AdatCimek;
import hu.kek.Adatok.AdatSzamozatlan;
import hu.kek.Adatok.AdatTartalom;
import hu.kek.Adatok.AdatTartalomTiszta;
import hu.kek.Adatok.Adatok;

public class KedvencekActivity extends AppCompatActivity {

    public static String kedvencek = "";
    private static ArrayList<String> kedvencekTomb = new ArrayList<>();
    private WebView mWebView;
    private float sortav = BeallitasokActivity.sortav;
    private float alapBetuMeret = BeallitasokActivity.alapBetuMeret;
    private boolean ujra = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Statikus.temaBeallit(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kedvencek);
        getSupportActionBar().setTitle("Kedvencek");

        mWebView = (WebView) findViewById(R.id.kedvencekView);
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
                megnyit(url);
                return true;
            }
        });

        kiir();
    }

    private void kiir() {
        String html = "<h3>A kedvencek üres!</h3>";
        if(kedvencekTomb.size() > 0) {
            html = "";
        }
        for(int i = 0; i<kedvencekTomb.size(); ++i) {
            if(!kedvencekTomb.get(i).equals("")) {
                String url = kedvencekTomb.get(i);
                String cim = "";
                String szoveg = "";
                if(kedvencekTomb.get(i).contains("kekapp")) {
                    cim = url.split("#p")[1]+". szakasz";
                    if(kedvencekTomb.get(i).contains("–")){
                        szoveg = AdatTartalomTiszta.paragrafusokTiszta[Integer.parseInt(kedvencekTomb.get(i).split("#p")[1].split("–")[0])-1];
                    } else {
                        szoveg = AdatTartalomTiszta.paragrafusokTiszta[Integer.parseInt(kedvencekTomb.get(i).split("#p")[1])-1];
                    }
                } else {
                    cim = AdatCimek.getCim(Integer.parseInt(url)+1);
                    if(Adatok.kapcsolo[Integer.parseInt(url)][0].contains("p")) {
                        szoveg = AdatTartalomTiszta.getTisztaParagrafus(Integer.parseInt(Adatok.kapcsolo[Integer.parseInt(url)][0].substring(1)));
                    } else {
                        szoveg = AdatSzamozatlan.getSzamozatlanTiszta(Integer.parseInt(Adatok.kapcsolo[Integer.parseInt(url)][0].substring(1)));
                    }
                    url = "kekapp://#t"+url;
                }

                html += "<a href=\""+url+"\"><b>"+cim+"</b><p>" + szoveg+ "</p></a>";
            }
        }
        mWebView.loadData("<html><body><style>body{line-height:"+sortav+"em; font-size:"+alapBetuMeret+"em;color:"+Statikus.szovegSzin+";} h1,h2,h3,h4,h5,h6,h7{margin: 0; padding: 0; border: 0; font-size: 100%; font: inherit;vertical-align: baseline;} h3{padding: 10px 0px;font-size: 1.2em; font-weight: bold; text-align:center;} p{ white-space: nowrap; margin: 0px; margin-top: 3px; height: 1.25em; overflow: hidden; /*margin-right: 40px;*/ } a{color:"+Statikus.szovegSzin+"; text-decoration:none; padding: 15px 0px; display: block; border-bottom: 1px solid #ababab;} .kiemel{color:red;}</style>" + html + "</body></html>", "text/html; charset=utf-8", "UTF-8");
    }

    private void megnyit(String url) {
        Intent intent;
        intent = new Intent(KedvencekActivity.this, KekSzovegActivity.class);
        if(url.contains("#t")) {
            url = url.split("#t")[1];
            intent.putExtra("fejlec", AdatCimek.getCim(Integer.parseInt(url)+1));
        }
        intent.putExtra("par", url);
        startActivity(intent);
        ujra = true;
    }

    public static void hozzaad(String kedvenc) {
        kedvencekTomb.add(kedvenc);
        listaToString();
    }

    public static void torol(String kedvenc) {
        kedvencekTomb.remove(kedvenc);
        listaToString();
    }

    public static boolean ellenorzes(String kedvenc) {
        if(kedvencekTomb.contains(kedvenc)) {
            return true;
        } else {
            return false;
        }
    }

    private static void listaToString() {
        kedvencek = "";
        for(int i = 0; i<kedvencekTomb.size(); ++i) {
            kedvencek += kedvencekTomb.get(i)+";";
        }
    }

    public static void szetvagdos () {
        if(!kedvencek.equals("")) {
            if(kedvencek.contains("http")) {
                kedvencek = kedvencek.replaceAll("http", "kekapp");
            }
            String[] kedvencekVagdosott = kedvencek.split(";");
            kedvencekTomb.clear();
            for (int i = 0; i < kedvencekVagdosott.length; ++i) {
                kedvencekTomb.add(kedvencekVagdosott[i]);
            }
        }
    }

    @Override
    public void onResume() {
        if(ujra) {
            recreate();
        }
        ujra = false;
        super.onResume();
    }
}
