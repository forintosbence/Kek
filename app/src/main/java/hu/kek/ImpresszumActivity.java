package hu.kek;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;

public class ImpresszumActivity extends AppCompatActivity {

    private WebView mWebView;
    private float sortav = BeallitasokActivity.sortav;
    private float alapBetuMeret = BeallitasokActivity.alapBetuMeret;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Statikus.temaBeallit(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_impresszum);
        getSupportActionBar().setTitle("Impresszum");

        mWebView = (WebView) findViewById(R.id.impresszumView);
        mWebView.setBackgroundColor(Color.TRANSPARENT);
        WebSettings settings = mWebView.getSettings();
        settings.setDefaultTextEncodingName("utf-8");
        settings.setDefaultFontSize(18);
        settings.setBuiltInZoomControls(true);
        settings.setDisplayZoomControls(false);

        String text = "<html><body><style>body {line-height:"+sortav+"em; font-size:"+alapBetuMeret+"em; text-align: center; color:"+Statikus.szovegSzin+";} h1{display:inline-block; font-size: 1.4em; line-heigh: 1.2em; margin-top: 0.4em;} a{color:"+Statikus.linkSzin+";} span{display:inline-block; margin-top:-0.7em;margin-bottom:-0.3em;}</style><h1>A Katolikus Egyház Katekizmusa</h1><span>(1.3.2 verzió)</span><p>A program a Szent István Társulat által kiadott katekizmust tartalmazza a kiadó engedélyével.<br />© <a href=\"http://szit.katolikus.hu\">Szent István Társulat</a><br /><br /><b>A programot készítette:</b><br />Forintos Bence<br /><a href=\"mailto:forintosbence@gmail.com\">Üzenet küldése</a><br /><br />Ha hibát észlel, kérjük írja meg a hiba pontos leírását, hogy mielőbb javíthassuk!<br /><br />Köszönet mindenkinek, akik észrevételekkel, ötletekkel és biztatással segítenek és segítettek!<br /><br /><b>Dicsőség az Úrnak!</b></p></body></html>";

        mWebView.loadData(text, "text/html; charset=utf-8", "UTF-8");

    }


}


