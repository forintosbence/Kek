package hu.kek;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class TematikusActivity extends AppCompatActivity {

    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Statikus.temaBeallit(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tematikus);
        getSupportActionBar().setTitle("Tematikus jegyzékek");

        if(!BeallitasokActivity.sotetTema) {
            View view = (View) findViewById(R.id.activity_tematikus);
            view.setBackgroundColor(getResources().getColor(R.color.hatterSzin));
        }

        Button targymutatoButton = (Button) findViewById(R.id.button_targymutato);
        targymutatoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TematikusActivity.this, TargymutatoElolapActivity.class);
                startActivity(intent);
            }
        });

        Button szentIrasForrasButton = (Button) findViewById(R.id.button_szentirasForras);
        szentIrasForrasButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TematikusActivity.this, SzentIrasActivity.class);
                startActivity(intent);
            }
        });

        Button egyhaziForrasikButton = (Button) findViewById(R.id.egyhaziForrasokButton);
        egyhaziForrasikButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TematikusActivity.this, EgyhaziActivity.class);
                startActivity(intent);
            }
        });
    }
}
