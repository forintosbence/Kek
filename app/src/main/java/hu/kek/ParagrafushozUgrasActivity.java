package hu.kek;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class ParagrafushozUgrasActivity extends AppCompatActivity {

    private ProgressDialog dialog;
    private boolean fut = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Statikus.temaBeallit(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paragrafushoz_ugras);
        getSupportActionBar().setTitle("Szakaszhoz ugrás");

        //((SwitchCompat)findViewById(R.id.cimekMegjeleniteseParagrafus)).setTextColor(Color.parseColor(Statikus.szovegSzin.toString()));
        ((TextView) findViewById(R.id.paragrafusKezdoText)).setTextColor(Color.parseColor(Statikus.szovegSzin.toString()));
        ((TextView) findViewById(R.id.paragrafusVegeText)).setTextColor(Color.parseColor(Statikus.szovegSzin.toString()));

        Button kekButton = (Button) findViewById(R.id.ugrasButton);
        final EditText kezdoMezo = (EditText) findViewById(R.id.kezdoParagrafusText);
        final EditText vegeMezo = (EditText) findViewById(R.id.vegeParagrafusText);
        kezdoMezo.setTextColor(Color.parseColor(Statikus.szovegSzin.toString()));
        vegeMezo.setTextColor(Color.parseColor(Statikus.szovegSzin.toString()));
        SwitchCompat cimekMegjelenitese = (SwitchCompat)findViewById(R.id.cimekMegjeleniteseParagrafus);
        cimekMegjelenitese.setVisibility(View.GONE);


        kekButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mezoEllenorzes(kezdoMezo, vegeMezo);
            }
        });

        kezdoMezo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    int val = Integer.parseInt(s.toString());
                    if(val > 2865) {
                        s.replace(0, s.length(), "2865", 0, 4);
                    } else if(val < 1) {
                        s.replace(0, s.length(), "1", 0, 1);
                    }
                } catch (NumberFormatException ex) {
                    // Do something
                }
            }
        });

        vegeMezo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    int val = Integer.parseInt(s.toString());
                    if(val > 2865) {
                        s.replace(0, s.length(), "2865", 0, 4);
                    } else if(val < 1) {
                        s.replace(0, s.length(), "1", 0, 1);
                    }
                } catch (NumberFormatException ex) {
                    // Do something
                }
            }
        });

        kezdoMezo.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(kezdoMezo, InputMethodManager.SHOW_IMPLICIT);
    }

    private void mezoEllenorzes(EditText kezdoMezo, EditText vegeMezo) {
        if(!vegeMezo.getText().toString().equals("") || (kezdoMezo.getText().toString().equals("") && vegeMezo.getText().toString().equals(""))) {
            AlertDialog alertDialog;
            if(BeallitasokActivity.sotetTema) {
                alertDialog = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AlertDialogSotet)).create();
            } else {
                alertDialog = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AlertDialogVilagos)).create();
            }
            if(kezdoMezo.getText().toString().equals("")) {
                alertDialog.setTitle("Hibás adatok");
                alertDialog.setMessage("Adjon meg kezdő pontot! Ha csak egy pontot kíván megjeleníteni, akkor csak a kezdő pont értékét töltse ki!");
                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Bezárás",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
            } else {
                int kezdoErtek = Integer.parseInt(kezdoMezo.getText().toString());
                int vegeErtek = Integer.parseInt(vegeMezo.getText().toString());
                if(kezdoErtek > vegeErtek) {
                    alertDialog.setTitle("Hibás adatok");
                    alertDialog.setMessage("A kezdő pont száma nagyobb, mint a vége pont száma!");
                    alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Bezárás",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();
                } else if (vegeErtek-kezdoErtek > 300) {
                    alertDialog.setTitle("Figyelmeztetés");
                    alertDialog.setMessage("Több mint 300 pontot akar megjeleníteni. Ez a lassabb telefonokon akár lefagyáshoz is vezethet. Biztos folytatja?");
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Igen",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    megjelenit(true);
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Mégsem",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();
                } else {
                    megjelenit(false);
                }
            }
        } else {
            megjelenit(false);
        }
    }

    private void megjelenit (final boolean betolt) {
        if(betolt) {
            if(BeallitasokActivity.sotetTema) {
                dialog = new ProgressDialog(ParagrafushozUgrasActivity.this, R.style.DialogSotet);
            } else {
                dialog = new ProgressDialog(ParagrafushozUgrasActivity.this, R.style.Dialog);
            }
            dialog.setMessage(Statikus.szovegFormaz("Betöltés..."));
            dialog.setCancelable(true);
            dialog.show();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    /*int cim;
                    if(((SwitchCompat)findViewById(R.id.cimekMegjeleniteseParagrafus)).isChecked()) {
                        cim = 1;
                    } else {
                        cim = 0;
                    }*/
                    KekSzovegActivity.nagyKesz = false;
                    KekSzovegActivity.osszerak("kekapp:/#p" + ((EditText) findViewById(R.id.kezdoParagrafusText)).getText() + "–" + ((EditText) findViewById(R.id.vegeParagrafusText)).getText());
                }
            }).start();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(ParagrafushozUgrasActivity.this, KekSzovegActivity.class);
                if(betolt) {
                    intent.putExtra("nagy", true);
                    while (!KekSzovegActivity.nagyKesz) {
                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
                if(((EditText)findViewById(R.id.vegeParagrafusText)).getText().toString().equals("")) {
                    intent.putExtra("par", "kekapp:/#p"+((EditText)findViewById(R.id.kezdoParagrafusText)).getText());
                    intent.putExtra("fejlec",((EditText)findViewById(R.id.kezdoParagrafusText)).getText() + ". pont");
                } else {
                    intent.putExtra("par", "kekapp:/#p"+((EditText)findViewById(R.id.kezdoParagrafusText)).getText()+"–"+((EditText)findViewById(R.id.vegeParagrafusText)).getText());
                    intent.putExtra("fejlec",((EditText)findViewById(R.id.kezdoParagrafusText)).getText()+" – "+((EditText)findViewById(R.id.vegeParagrafusText)).getText() + ". pont");
                }
               /* if(((SwitchCompat)findViewById(R.id.cimekMegjeleniteseParagrafus)).isChecked()) {
                    intent.putExtra("szakaszCimek", 1);
                } else {
                    intent.putExtra("szakaszCimek", 0);
                }*/
                startActivity(intent);
                if (betolt) {
                    dialog.dismiss();
                }
            }
        }).start();
    }
}
