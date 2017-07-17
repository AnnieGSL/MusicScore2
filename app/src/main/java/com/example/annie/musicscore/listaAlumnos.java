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
 * Created by Annie on 14-07-2017.
 */

public class listaAlumnos extends AppCompatActivity {
     /*Administra el activity que contiene la lista de busqueda*/

    RecyclerView recyclerView;
    listAdapter adapter;
    String nombre, correo, info, perfil;
    ArrayList<item> array = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista);

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
        Type type = new TypeToken<ArrayList<item>>(){}.getType();
        array = gson.fromJson(info, type);
        //Toast.makeText(listaAlumnos.this, "array"+array, Toast.LENGTH_LONG).show();

        //aqui se define el recyclerView del xml
        recyclerView = (RecyclerView)findViewById(R.id.rvItema);
        adapter = new listAdapter(listaAlumnos.this, array);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(listaAlumnos.this));
        recyclerView.setHasFixedSize(true);
    }

    public boolean onOptionsItemSelected(MenuItem itm){
        switch (itm.getItemId()){
            case android.R.id.home:
                //onBackPressed();
                Intent intent = new Intent(listaAlumnos.this, MenuPpal.class);
                intent.putExtra("nombre", nombre);
                intent.putExtra("correo", correo);
                intent.putExtra("perfil", perfil);
                listaAlumnos.this.startActivity(intent);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(itm);
    }
}
