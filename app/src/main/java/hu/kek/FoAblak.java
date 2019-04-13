package hu.kek;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import static hu.kek.R.layout.activity_fo_ablak;

public class FoAblak extends AppCompatActivity {

    private ProgressDialog dialog;
    private static boolean indulas = true;
    public static Activity foAlbakActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        beallitasokBeolvas();
        Statikus.beallitasok();
        Statikus.temaBeallit(this);
        KedvencekActivity.szetvagdos();
        super.onCreate(savedInstanceState);
        setContentView(activity_fo_ablak);
        if(!BeallitasokActivity.sotetTema) {
            View view = (View) findViewById(R.id.activity_fo_ablak);
            view.setBackgroundColor(getResources().getColor(R.color.hatterSzin));
        }
        getSupportActionBar().setTitle("A Katolikus Egyház Katekizmusa");
        szovegek();

        foAlbakActivity = this;

        Button kekButton = (Button) findViewById(R.id.button_katekizmus);
        kekButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Statikus.cimKeres("p27");
                if(BeallitasokActivity.sotetTema) {
                    dialog = new ProgressDialog(FoAblak.this, R.style.DialogSotet);
                } else {
                    dialog = new ProgressDialog(FoAblak.this, R.style.Dialog);
                }
                dialog.setMessage(Statikus.szovegFormaz("Betöltés..."));
                dialog.setCancelable(true);
                if(!TartalomActivity.szovegOsszerakva){
                    dialog.show();
                }
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while (true) {
                            if(TartalomActivity.szovegOsszerakva) {
                                Intent intent = new Intent(FoAblak.this, TartalomActivity.class);
                                startActivity(intent);
                                dialog.dismiss();
                                break;
                            } else {
                                try {
                                    Thread.sleep(100);
                                } catch (InterruptedException e) {
                                    //e.printStackTrace();
                                }
                            }
                        }
                    }
                }).start();
            }
        });

        Button tematikusButton = (Button) findViewById(R.id.button_tematikus);
        tematikusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FoAblak.this, TematikusActivity.class);
                startActivity(intent);
            }
        });

        Button ugrasButton = (Button) findViewById(R.id.button_ugras);
        ugrasButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FoAblak.this, ParagrafushozUgrasActivity.class);
                startActivity(intent);
            }
        });

        Button keresesButton = (Button) findViewById(R.id.button_keres);
        keresesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FoAblak.this, KeresesActivity.class);
                startActivity(intent);
            }
        });

        Button kedvencekButton = (Button) findViewById(R.id.button_kedvencek);
        kedvencekButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FoAblak.this, KedvencekActivity.class);
                startActivity(intent);
            }
        });

        if(BeallitasokActivity.kedvencekTiltva) {
            kedvencekButton.setVisibility(View.GONE);
        }

        Button impresszumButton = (Button) findViewById(R.id.button_impresszum);
        impresszumButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FoAblak.this, ImpresszumActivity.class);
                startActivity(intent);
            }
        });

        Button beallitasokButton = (Button) findViewById(R.id.button_beallitasok);
        beallitasokButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FoAblak.this, BeallitasokActivity.class);
                startActivity(intent);
            }
        });

        ImageView szitLogo = (ImageView) findViewById(R.id.SzitLogo);
        szitLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://szitkonyvek.hu/termek/1133"));
                startActivity(browserIntent);
            }
        });

    }

    private void beallitasokBeolvas() {
        SharedPreferences sharedPref = getSharedPreferences("beallitasok", Context.MODE_PRIVATE);
        BeallitasokActivity.kepernyozarTilt = sharedPref.getBoolean("kepernyo", false);
        BeallitasokActivity.sortav = sharedPref.getFloat("sortav", (float) 1.4);
        BeallitasokActivity.alapBetuMeret = sharedPref.getFloat("betuMeret", (float) 1.0);
        BeallitasokActivity.sotetTema = sharedPref.getBoolean("sotetTema", false);
        BeallitasokActivity.feketeHatter = sharedPref.getBoolean("feketeHatter", false);
        BeallitasokActivity.tartalomCimek = sharedPref.getBoolean("tartalomCimek", false);
        BeallitasokActivity.tartalomCimStruktura = sharedPref.getBoolean("tartalomCimStruktura", false);
        BeallitasokActivity.hivatkozasCimek = sharedPref.getBoolean("hivatkozasCimek", false);
        BeallitasokActivity.hivatkozasCimStruktura = sharedPref.getBoolean("hivatkozasCimStruktura", false);
        BeallitasokActivity.kedvencekTiltva = sharedPref.getBoolean("kedvencekTiltva", false);
        KedvencekActivity.kedvencek = sharedPref.getString("kedvencek", "");
    }

    private static void szovegek() {
        if(indulas) {
            TartalomActivity.szovegOsszerak.start();
           indulas = false;
        }
    }
}
