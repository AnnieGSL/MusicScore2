package com.example.annie.musicscore;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by Annie on 28-08-2017.
 */

public class listMessage extends AppCompatActivity {
    RecyclerView recyclerView;
    msgAdapter adapter;
    String nombre, correo, info, perfil;
    ArrayList<msg> array = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_msg);

        //Aquí se define la flecha para volver a la ventana principal
        ActionBar actionBar = getSupportActionBar();
        assert actionBar!=null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_36dp);

        Bundle bundle = getIntent().getExtras();
        Gson gson = new Gson();
        info = bundle.getString("Datos");
        nombre = bundle.getString("nombre");
        correo = bundle.getString("username");
        perfil = bundle.getString("perfil");
        Type type = new TypeToken<ArrayList<msg>>(){}.getType();
        array = gson.fromJson(info, type);
        //Toast.makeText(listMessage.this, "perfil"+perfil, Toast.LENGTH_LONG).show();

        //aqui se define el recyclerView del xml
        recyclerView = (RecyclerView)findViewById(R.id.rvItemi);
        adapter = new msgAdapter(listMessage.this, array);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        recyclerView.setLayoutManager(new LinearLayoutManager(listMessage.this));
        recyclerView.setHasFixedSize(true);
    }

    public boolean onOptionsItemSelected(MenuItem itm){
        switch (itm.getItemId()){
            case android.R.id.home:
                //onBackPressed();
                Intent intent = new Intent(listMessage.this, MenuPpal.class);
                intent.putExtra("nombre", nombre);
                intent.putExtra("correo", correo);
                intent.putExtra("perfil", perfil);
                listMessage.this.startActivity(intent);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(itm);
    }
}
