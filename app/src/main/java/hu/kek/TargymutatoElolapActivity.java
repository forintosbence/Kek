package hu.kek;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;

import hu.kek.Adatok.AdatTematikus;

public class TargymutatoElolapActivity extends AppCompatActivity {

    private WebView mWebView;
    private float sortav = BeallitasokActivity.sortav;
    private float alapBetuMeret = BeallitasokActivity.alapBetuMeret;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Statikus.temaBeallit(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_targymutato_elolap);
        getSupportActionBar().setTitle("Tárgymutató");
        ((EditText) findViewById(R.id.tematikusKeresesMezo)).setTextColor(Color.parseColor(Statikus.szovegSzin.toString()));
        ((EditText) findViewById(R.id.tematikusKeresesMezo)).setHintTextColor(Color.parseColor(Statikus.mezoHintSzovegSzin.toString()));
        //((EditText) findViewById(R.id.tematikusKeresesMezo))

        mWebView = (WebView) findViewById(R.id.targymutatoAbcView);
        mWebView.setBackgroundColor(Color.TRANSPARENT);
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
                Intent intent = new Intent(TargymutatoElolapActivity.this, TargymutatoActivity.class);
                intent.putExtra("melyik", url);
                startActivity(intent);
                return true;
            }

        });

        mWebView.loadData("<html><body><style>body{line-height:"+sortav+"em; font-size:"+alapBetuMeret+"em;text-align: center; /*background-color: #fafafa;*/} a{width: 25%; margin: -1px 0px 0px -1px; padding: 16px 12px; display: inline-block; color:"+Statikus.szovegSzin+"; text-decoration:none; border: 1px solid #ababab; font-size: 1.2em; } </style>"+ AdatTematikus.tematikusAbc+"</body></html>", "text/html; charset=utf-8", "UTF-8");

        final Button tematikusKereses = (Button) findViewById(R.id.tematikusKereses);
        tematikusKereses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText tematikusKeresesMezo = (EditText) findViewById(R.id.tematikusKeresesMezo);
                String keresendo = tematikusKeresesMezo.getText().toString();
                if(keresendo.equals("") || keresendo.equals(" ")) {
                    AlertDialog alertDialog;
                    if(BeallitasokActivity.sotetTema) {
                        alertDialog = new AlertDialog.Builder(new ContextThemeWrapper(TargymutatoElolapActivity.this, R.style.AlertDialogSotet)).create();
                    } else {
                        alertDialog = new AlertDialog.Builder(new ContextThemeWrapper(TargymutatoElolapActivity.this, R.style.AlertDialogVilagos)).create();
                    }
                    alertDialog.setTitle("Nincs keresőszó");
                    alertDialog.setMessage("Kérem, írja be, mit keres!");
                    alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Bezárás",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();
                } else {
                    if(keresendo.matches("(.*) $")) {
                        keresendo = keresendo.substring(0, keresendo.length()-1);
                    }
                    Intent intent = new Intent(TargymutatoElolapActivity.this, TargymutatoActivity.class);
                    intent.putExtra("melyik", keresendo);
                    startActivity(intent);
                }
            }
        });

    }
}
