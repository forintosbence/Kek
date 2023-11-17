package hu.kek;

import android.app.ProgressDialog;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import java.util.ArrayList;
import hu.kek.Adatok.AdatApostoli;
import hu.kek.Adatok.AdatCimek;
import hu.kek.Adatok.AdatSzamozatlan;
import hu.kek.Adatok.AdatTartalomTiszta;
import hu.kek.Adatok.Adatok;

public class TalalatokActivity extends AppCompatActivity {

    private WebView mWebView;
    private static ArrayList<String[]> talalatok = new ArrayList<>();
    private ProgressDialog dialog;
    private static String keresett;
    private static boolean talaltKiemel;
    private float sortav = BeallitasokActivity.sortav;
    private float alapBetuMeret = BeallitasokActivity.alapBetuMeret;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Statikus.temaBeallit(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_talalatok);

        if(talalatok.size() > 250) {
            if(BeallitasokActivity.sotetTema) {
                dialog = new ProgressDialog(this, R.style.DialogSotet);
            } else {
                dialog = new ProgressDialog(this, R.style.Dialog);
            }
            dialog.setMessage(Statikus.szovegFormaz("Betöltés..."));
            dialog.setCancelable(true);
            dialog.show();
        }

        mWebView = (WebView) findViewById(R.id.talalatokWebView);
        Statikus.webViewSzinBeallit(mWebView);
        WebSettings settings = mWebView.getSettings();
        settings.setDefaultTextEncodingName("utf-8");
        settings.setDefaultFontSize(18);
        settings.setRenderPriority(WebSettings.RenderPriority.HIGH);
        settings.setLoadWithOverviewMode(true);
        settings.setEnableSmoothTransition(true);
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

            @Override
            public void onPageFinished(WebView view, String url) {
                if(dialog != null && dialog.isShowing()) {
                    try {
                        dialog.dismiss();
                    } catch (Exception e) {}
                }
            }

        });

        kiir();
    }

    private void megnyit(String url) {
        Intent intent;
        if(url.contains("#a")) {
            if(url.contains("#a0")) {
                BevezetokActivity.tartalom = 4;
            } else {
                BevezetokActivity.tartalom = 5;
            }
            intent = new Intent(TalalatokActivity.this, BevezetokActivity.class);
        } else {
            intent = new Intent(TalalatokActivity.this, KekSzovegActivity.class);
            intent.putExtra("par", url);
        }
        if(talaltKiemel) {
            intent.putExtra("keres", keresett);
        }
        startActivity(intent);
    }

    public void kiir () {
        String html = "";
        int osszesTalalat = 0;
        for (int i = 0; i < talalatok.size(); ++i) {
            boolean kiemelt = false;
            boolean vege = false;
            int belsoTalalat = 0;
            String egySor = "";
            String feldarabolt[] = talalatok.get(i)[1].split("_");
            for (int j = 0; j < feldarabolt.length; ++j) {
                if(feldarabolt[j].contains("</span>")) {
                    ++belsoTalalat;
                    ++osszesTalalat;
                }
                if(!vege) {
                    if(feldarabolt[j].length() > 60) {
                        if(kiemelt == false) {
                            egySor += "..." + feldarabolt[j].substring(feldarabolt[j].length()-60, feldarabolt[j].length());
                        } else {
                            egySor += feldarabolt[j].substring(0, 60) + "...";
                            kiemelt = false;
                            vege = true;
                        }
                    } else {
                        egySor += feldarabolt[j];
                        kiemelt = true;
                    }
                }
            }
            String cim = "";
            if(talalatok.get(i)[0].contains("s") || talalatok.get(i)[0].contains("k")) {
                for (int k = 0; k< Adatok.sorrend.length; ++k) {
                    if(Adatok.sorrend[k].equals(talalatok.get(i)[0])) {
                        cim = AdatCimek.getCim(Integer.parseInt(Adatok.sorrend[k-1].substring(1)));
                        cim = cim.replaceAll("<h\\d+>|</h\\d+>", "");
                    }
                }
            } else if(talalatok.get(i)[0].contains("a")) {
                if(talalatok.get(i)[0].equals("a0")) {
                    cim = "Laetamur magnopere apostoli levél";
                } else {
                    cim = "Fidei depositum apostoli konstitúció";
                }
            } else {
                cim = talalatok.get(i)[0].substring(1)+". pont ";
            }
            if(belsoTalalat > 1) {
                html += "<a href=\"kekapp://#"+talalatok.get(i)[0]+"\"><b>"+cim+"</b><b><i> ("+belsoTalalat+" találat)</i></b><p>" + egySor+ "</p></a>";
            } else {
                html += "<a href=\"kekapp://#"+talalatok.get(i)[0]+"\"><b>"+cim+"</b><p>" + egySor+ "</p></a>";
            }
        }
        if(talalatok.size() == 0) {
            getSupportActionBar().setTitle("Nincs találat");
        } else if (talalatok.size() == osszesTalalat) {
            getSupportActionBar().setTitle(talalatok.size() + " találat");
        } else {
            getSupportActionBar().setTitle(talalatok.size() + " helyen "+osszesTalalat+" találat");
        }

        String text = "<html><body><style>body{line-height:"+sortav+"em; font-size:"+alapBetuMeret+"em;} h1,h2,h3,h4,h5,h6,h7{margin: 0; padding: 0; border: 0; font-size: 100%; font: inherit;vertical-align: baseline;} p{text-align: justify; margin: 0px; margin-top: 3px; word-wrap:break-word;} a{color:"+Statikus.szovegSzin+"; text-decoration:none; padding: 15px 0px; display: block; border-bottom: 1px solid #ababab;} .kiemel{color:red;}</style>" + html + "</body></html>";
        text = Base64.encodeToString(text.getBytes(), Base64.NO_PADDING);
        mWebView.loadData(text, "text/html", "base64");
    }

    public static boolean kereses(String keresendo, boolean apostoli, boolean teljesSzavak, boolean talalatKiemeles) {
        talaltKiemel = talalatKiemeles;
        keresett = keresendo;
        ArrayList<String[]> rendezetlenTalalatok;
        talalatok.clear();
        if(teljesSzavak) {
            //csak teljes szavak
            if (apostoli) {
                talalatok = AdatApostoli.szavasKereses(keresendo);
            }
            rendezetlenTalalatok = AdatTartalomTiszta.szavasKereses(keresendo);
            rendezetlenTalalatok.addAll(AdatSzamozatlan.szavasKereses(keresendo));
        } else {
            //tördelékre is
            if(apostoli) {
                talalatok = AdatApostoli.kereses(keresendo);
            }
            rendezetlenTalalatok = AdatTartalomTiszta.kereses(keresendo);
            rendezetlenTalalatok.addAll(AdatSzamozatlan.kereses(keresendo));
        }
        for (int j = 0; j < Adatok.sorrend.length; ++j) {
            for (int i = 0; i< rendezetlenTalalatok.size(); ++i) {
                if (rendezetlenTalalatok.get(i)[0].equals(Adatok.sorrend[j])) {
                    talalatok.add(rendezetlenTalalatok.get(i));
                }

            }
        }
        return true;
    }
}
