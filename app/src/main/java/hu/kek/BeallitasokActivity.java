package hu.kek;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.PowerManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

public class BeallitasokActivity extends AppCompatActivity {

    private WebView mWebView;
    public static float alapBetuMeret = (float) 1.0;
    public static float sortav = (float) 1.4;
    public static boolean kepernyozarTilt = false;
    public static boolean sotetTema = false;
    public static boolean feketeHatter = false;
    public static boolean tartalomCimek = false;
    public static boolean tartalomCimStruktura = false;
    public static boolean hivatkozasCimek = false;
    public static boolean hivatkozasCimStruktura = false;
    public static boolean kedvencekTiltva = false;
    private static PowerManager.WakeLock mWakeLock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Statikus.temaBeallit(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beallitasok);
        getSupportActionBar().setTitle("Beállítások");

        final Switch kepernyoKapcsolo = (Switch) findViewById(R.id.kepernyozarKapcsolo);
        kepernyoKapcsolo.setChecked(kepernyozarTilt);

        final Switch temaKapcsolo = (Switch) findViewById(R.id.temaValtasKapcsolo);
        temaKapcsolo.setChecked(sotetTema);

        final Switch feketeHatterKapcsolo = (Switch) findViewById(R.id.feketeHatterKapcsolo);
        feketeHatterKapcsolo.setChecked(feketeHatter);
        feketeHatterKapcsolo.setEnabled(sotetTema);

        final Switch kedvencekKapcsolo = (Switch) findViewById(R.id.kedvencekKapcsolo);
        kedvencekKapcsolo.setChecked(kedvencekTiltva);

        final Switch tartalomCimekKapcsolo = (Switch) findViewById(R.id.tartalomCimekKapcsolo);
        tartalomCimekKapcsolo.setChecked(tartalomCimek);

        final Switch tartalomCimstrukturaKapcsolo = (Switch) findViewById(R.id.tartalomCimstrukturaKapcsolo);
        tartalomCimstrukturaKapcsolo.setChecked(tartalomCimStruktura);
        tartalomCimstrukturaKapcsolo.setEnabled(tartalomCimek);

        final Switch hivatkozasCimekKapcsolo = (Switch) findViewById(R.id.hivatkozasCimekKapcsolo);
        hivatkozasCimekKapcsolo.setChecked(hivatkozasCimek);

        final Switch hivatkozasCimstrukturaKapcsolo = (Switch) findViewById(R.id.hivatkozasCimstrukturaKapcsolo);
        hivatkozasCimstrukturaKapcsolo.setChecked(hivatkozasCimStruktura);
        hivatkozasCimstrukturaKapcsolo.setEnabled(hivatkozasCimek);

        final SeekBar sortavAllito = (SeekBar) findViewById(R.id.sortavAllito);
        sortavAllito.setProgress((int) ((sortav*10)-10));

        final SeekBar betuMeretAllito = (SeekBar) findViewById(R.id.betuMeretAllito);
        betuMeretAllito.setProgress((int) ((alapBetuMeret-0.7)*80));

        kepernyoKapcsolo.setTextColor(Color.parseColor(Statikus.szovegSzin.toString()));
        temaKapcsolo.setTextColor(Color.parseColor(Statikus.szovegSzin.toString()));
        feketeHatterKapcsolo.setTextColor(Color.parseColor(Statikus.szovegSzin.toString()));
        kedvencekKapcsolo.setTextColor(Color.parseColor(Statikus.szovegSzin.toString()));
        tartalomCimekKapcsolo.setTextColor(Color.parseColor(Statikus.szovegSzin.toString()));
        tartalomCimstrukturaKapcsolo.setTextColor(Color.parseColor(Statikus.szovegSzin.toString()));
        hivatkozasCimekKapcsolo.setTextColor(Color.parseColor(Statikus.szovegSzin.toString()));
        hivatkozasCimstrukturaKapcsolo.setTextColor(Color.parseColor(Statikus.szovegSzin.toString()));

        TextView betuMeretAllitoText = (TextView) findViewById(R.id.betuMeretAllitoText);
        betuMeretAllitoText.setTextColor(Color.parseColor(Statikus.szovegSzin.toString()));

        TextView sortavAllitoText = (TextView) findViewById(R.id.sortavAllitoText);
        sortavAllitoText.setTextColor(Color.parseColor(Statikus.szovegSzin.toString()));

        Button alapBeallitasok = (Button) findViewById(R.id.alapBeallitasok);
        Button betuAlaphelyzetButton = (Button) findViewById(R.id.betuAlaphelyzetButton);


        kepernyoKapcsolo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    kepernyozarTilt = true;
                } else {
                    kepernyozarTilt = false;
                }
                //adatok mentése;
                SharedPreferences sharedPref = getSharedPreferences("beallitasok", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putBoolean("kepernyo", kepernyozarTilt);
                editor.commit();
            }
        });

        temaKapcsolo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    sotetTema = true;
                } else {
                    sotetTema = false;
                    feketeHatter = false;
                    feketeHatterKapcsolo.setChecked(feketeHatter);
                }
                //adatok mentése;
                SharedPreferences sharedPref = getSharedPreferences("beallitasok", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putBoolean("feketeHatter", feketeHatter);
                editor.putBoolean("sotetTema", sotetTema);
                editor.commit();
                Statikus.beallitasok();
                recreate();
                FoAblak.foAlbakActivity.recreate();
            }
        });

        feketeHatterKapcsolo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    feketeHatter = true;
                } else {
                    feketeHatter = false;
                }
                Statikus.webViewSzinBeallit(mWebView);
                SharedPreferences sharedPref = getSharedPreferences("beallitasok", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putBoolean("feketeHatter", feketeHatter);
                editor.commit();
            }
        });

        kedvencekKapcsolo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    kedvencekTiltva = true;
                } else {
                    kedvencekTiltva = false;
                }
                SharedPreferences sharedPref = getSharedPreferences("beallitasok", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putBoolean("kedvencekTiltva", kedvencekTiltva);
                editor.commit();
                FoAblak.foAlbakActivity.recreate();
            }
        });

        tartalomCimekKapcsolo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    tartalomCimek = true;
                } else {
                    tartalomCimek = false;
                    tartalomCimStruktura = false;
                    tartalomCimstrukturaKapcsolo.setChecked(tartalomCimStruktura);
                }
                tartalomCimstrukturaKapcsolo.setEnabled(tartalomCimek);
                SharedPreferences sharedPref = getSharedPreferences("beallitasok", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putBoolean("tartalomCimStruktura", tartalomCimStruktura);
                editor.putBoolean("tartalomCimek", tartalomCimek);
                editor.commit();
            }
        });

        tartalomCimstrukturaKapcsolo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    tartalomCimStruktura = true;
                } else {
                    tartalomCimStruktura = false;
                }
                SharedPreferences sharedPref = getSharedPreferences("beallitasok", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putBoolean("tartalomCimStruktura", tartalomCimStruktura);
                editor.commit();
            }
        });

        hivatkozasCimekKapcsolo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    hivatkozasCimek = true;
                } else {
                    hivatkozasCimek = false;
                    hivatkozasCimStruktura = false;
                    hivatkozasCimstrukturaKapcsolo.setChecked(hivatkozasCimStruktura);
                }
                hivatkozasCimstrukturaKapcsolo.setEnabled(hivatkozasCimek);
                SharedPreferences sharedPref = getSharedPreferences("beallitasok", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putBoolean("hivatkozasCimStruktura", hivatkozasCimStruktura);
                editor.putBoolean("hivatkozasCimek", hivatkozasCimek);
                editor.commit();
            }
        });

        hivatkozasCimstrukturaKapcsolo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    hivatkozasCimStruktura = true;
                } else {
                    hivatkozasCimStruktura = false;
                }
                SharedPreferences sharedPref = getSharedPreferences("beallitasok", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putBoolean("hivatkozasCimStruktura", hivatkozasCimStruktura);
                editor.commit();
            }
        });

        sortavAllito.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                sortav = (float) 1+((float)progress/10);
                kiir();
                SharedPreferences sharedPref = getSharedPreferences("beallitasok", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putFloat("sortav", sortav);
                editor.commit();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        betuMeretAllito.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                alapBetuMeret = (float) 0.7+((float)progress/80);
                kiir();
                SharedPreferences sharedPref = getSharedPreferences("beallitasok", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putFloat("betuMeret", alapBetuMeret);
                editor.commit();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        alapBeallitasok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog alertDialog;
                if(BeallitasokActivity.sotetTema) {
                    alertDialog = new AlertDialog.Builder(new ContextThemeWrapper(BeallitasokActivity.this, R.style.AlertDialogSotet)).create();
                } else {
                    alertDialog = new AlertDialog.Builder(new ContextThemeWrapper(BeallitasokActivity.this, R.style.AlertDialogVilagos)).create();
                }
                alertDialog.setTitle("Figyelmeztetés");
                alertDialog.setMessage("Biztos hogy mindent visszaállít alaphelyzetbe?");
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Igen",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                kepernyozarTilt = false;
                                sotetTema = false;
                                feketeHatter = false;
                                kedvencekTiltva = false;
                                tartalomCimek = false;
                                tartalomCimStruktura = false;
                                hivatkozasCimek = false;
                                hivatkozasCimStruktura = false;
                                alapBetuMeret = (float) 1.0;
                                sortav = (float) 1.4;
                                kepernyoKapcsolo.setChecked(kepernyozarTilt);
                                temaKapcsolo.setChecked(sotetTema);
                                feketeHatterKapcsolo.setChecked(feketeHatter);
                                kedvencekKapcsolo.setChecked(kedvencekTiltva);
                                tartalomCimekKapcsolo.setChecked(tartalomCimek);
                                tartalomCimstrukturaKapcsolo.setChecked(tartalomCimStruktura);
                                hivatkozasCimekKapcsolo.setChecked(hivatkozasCimek);
                                hivatkozasCimstrukturaKapcsolo.setChecked(hivatkozasCimStruktura);
                                sortavAllito.setProgress((int) ((sortav*10)-10));
                                betuMeretAllito.setProgress((int) ((alapBetuMeret-0.7)*80));
                                SharedPreferences sharedPref = getSharedPreferences("beallitasok", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPref.edit();
                                editor.putBoolean("kepernyo", kepernyozarTilt);
                                editor.putBoolean("sotetTema", sotetTema);
                                editor.putBoolean("feketeHatter", feketeHatter);
                                editor.putBoolean("kedvencekTiltva", kedvencekTiltva);
                                editor.putBoolean("tartalomCimek", tartalomCimek);
                                editor.putBoolean("tartalomCimStruktura", tartalomCimStruktura);
                                editor.putBoolean("hivatkozasCimek", hivatkozasCimek);
                                editor.putBoolean("hivatkozasCimStruktura", hivatkozasCimStruktura);
                                editor.putFloat("sortav", sortav);
                                editor.putFloat("betuMeret", alapBetuMeret);
                                editor.commit();
                                Statikus.beallitasok();
                                recreate();
                                FoAblak.foAlbakActivity.recreate();
                            }
                        });
                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Mégsem",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
            }
        });

        betuAlaphelyzetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog alertDialog;
                if(BeallitasokActivity.sotetTema) {
                    alertDialog = new AlertDialog.Builder(new ContextThemeWrapper(BeallitasokActivity.this, R.style.AlertDialogSotet)).create();
                } else {
                    alertDialog = new AlertDialog.Builder(new ContextThemeWrapper(BeallitasokActivity.this, R.style.AlertDialogVilagos)).create();
                }
                alertDialog.setTitle("Figyelmeztetés");
                alertDialog.setMessage("Biztos visszaállítja a szöveg beállításokat alaphelyzetbe?");
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Igen",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                alapBetuMeret = (float) 1.0;
                                sortav = (float) 1.4;
                                sortavAllito.setProgress((int) ((sortav*10)-10));
                                betuMeretAllito.setProgress((int) ((alapBetuMeret-0.7)*80));
                                SharedPreferences sharedPref = getSharedPreferences("beallitasok", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPref.edit();
                                editor.putFloat("sortav", sortav);
                                editor.putFloat("betuMeret", alapBetuMeret);
                                editor.commit();
                            }
                        });
                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Mégsem",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
            }
        });

        mWebView = (WebView) findViewById(R.id.mintaWebView);
        Statikus.webViewSzinBeallit(mWebView);
        WebSettings settings = mWebView.getSettings();
        settings.setDefaultTextEncodingName("utf-8");
        settings.setDefaultFontSize(18);
        settings.setBuiltInZoomControls(true);
        settings.setDisplayZoomControls(false);
        mWebView.setWebChromeClient(new WebChromeClient());
        kiir();
    }

    private void kiir() {
        String szoveg = "<p class=\"par\"><b>27</b> Az Isten utáni vágy az ember szívébe van írva, mert Istentől és Istenért teremtetett; Isten pedig szüntelenül hivogatja magához az embert, és az ember csak Istenben találja meg az igazságot és a boldogságot, amelyet szüntelenül keres: <span class=\"idezet\">„Az emberi méltóság lényeges része az Istennel való közösségre szóló meghívás. Az ember kezdettől fogva párbeszédre hivatott Istennel, ugyanis csak azért létezik, mert Isten szeretetből megteremtette és szeretetből létben tartja, s csak akkor élhet teljesen az igazság szerint, ha önként elismeri ezt a szeretetet, és rábízza magát a Teremtőjére.”</span></p>";
        mWebView.loadData("<html><body><style>p{padding: 3px 0px;text-align: justify; line-height:"+sortav+"em; font-size: "+alapBetuMeret+"em; color:"+Statikus.szovegSzin+";} .behuzas{margin-left: 2em;} .idezet, .szidezet{font-style: italic; display:block; margin: 1em 0em 1em 1em;} .szidezet{text-align: justify;} .kulonallo{margin: 1em 0em;} .szkulonallo{display: inline-block; margin: 1em 0em;} </style>"+szoveg+"</body></html>", "text/html; charset=utf-8", "UTF-8");
    }


    public static void kepernyoLetilt(Context c, boolean tilt) {
        final PowerManager pm = (PowerManager) c.getSystemService(Context.POWER_SERVICE);
        if(tilt){
            mWakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, "zar");
            mWakeLock.acquire();
        } else {
            if(mWakeLock != null && mWakeLock.isHeld()) {
                mWakeLock.release();
            }
        }
    }


}
