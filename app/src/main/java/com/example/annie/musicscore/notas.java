package com.example.annie.musicscore;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

/**
 * Created by Annie on 30-11-2017.
 */

public class notas extends AppCompatActivity {
    String nombre, correo, perfil;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notas);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar!=null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_36dp);

        Bundle bundle = getIntent().getExtras();
        nombre = bundle.getString("nombre");
        correo = bundle.getString("username");
        perfil = bundle.getString("perfil");
    }

    public boolean onOptionsItemSelected(MenuItem itm){
        switch (itm.getItemId()){
            case android.R.id.home:
                //onBackPressed();
                Intent intent = new Intent(notas.this, MenuPpal.class);
                intent.putExtra("nombre", nombre);
                intent.putExtra("correo", correo);
                intent.putExtra("perfil", perfil);
                notas.this.startActivity(intent);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(itm);
    }
}
