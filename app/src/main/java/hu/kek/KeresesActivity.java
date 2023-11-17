package hu.kek;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class KeresesActivity extends AppCompatActivity {

    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Statikus.temaBeallit(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kereses);
        getSupportActionBar().setTitle("Keresés");

        ((EditText)findViewById(R.id.kereseAcText)).setTextColor(Color.parseColor(Statikus.szovegSzin.toString()));
        ((SwitchCompat)findViewById(R.id.apostoliKapcsolo)).setTextColor(Color.parseColor(Statikus.szovegSzin.toString()));
        ((SwitchCompat)findViewById(R.id.szavakKapcsolo)).setTextColor(Color.parseColor(Statikus.szovegSzin.toString()));
        ((SwitchCompat)findViewById(R.id.kiemelKapcsolo)).setTextColor(Color.parseColor(Statikus.szovegSzin.toString()));

        Button keresesButton = (Button) findViewById(R.id.keresesAcButton);
        keresesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String keresendo = ((EditText)findViewById(R.id.kereseAcText)).getText().toString();
                if(keresendo.equals("") || keresendo.equals(" ")) {
                    AlertDialog alertDialog;
                    if(BeallitasokActivity.sotetTema) {
                        alertDialog = new AlertDialog.Builder(new ContextThemeWrapper(KeresesActivity.this, R.style.AlertDialogSotet)).create();
                    } else {
                        alertDialog = new AlertDialog.Builder(new ContextThemeWrapper(KeresesActivity.this, R.style.AlertDialogVilagos)).create();
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
                    if(BeallitasokActivity.sotetTema) {
                        dialog = new ProgressDialog(KeresesActivity.this, R.style.DialogSotet);
                    } else {
                        dialog = new ProgressDialog(KeresesActivity.this, R.style.Dialog);
                    }
                    dialog.setMessage(Statikus.szovegFormaz("Keresés..."));
                    dialog.setCancelable(true);
                    dialog.show();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            String keresendo = ((EditText)findViewById(R.id.kereseAcText)).getText().toString();
                            if(keresendo.matches("(.*) $")) {
                                keresendo = keresendo.substring(0, keresendo.length()-1);
                            }
                            TalalatokActivity.kereses(keresendo, ((SwitchCompat)findViewById(R.id.apostoliKapcsolo)).isChecked(), ((SwitchCompat)findViewById(R.id.szavakKapcsolo)).isChecked(), ((SwitchCompat)findViewById(R.id.kiemelKapcsolo)).isChecked());
                            Intent intent = new Intent(KeresesActivity.this, TalalatokActivity.class);
                            startActivity(intent);
                            dialog.dismiss();
                        }
                    }).start();
                }
            }
        });
    }
}
