package com.example.annie.musicscore;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;

import java.util.ArrayList;

public class Busqueda extends AppCompatActivity {
    /*Administra el activity que contiene la lista de busqueda*/
    private static final String TAG = "Busqueda";
    //private static final String URL_FOR_LOGIN = "http://192.168.1.8/MusicScore/Login.php";
    private static final String URL_FOR_FILTRO = "http://musictesis.esy.es/getPdfs.php";

    ArrayList<Datos_url> pdfDocs = new ArrayList<Datos_url>();
    RecyclerView recyclerView;
    busqAdapter adapter;
    String name, url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_busqueda);

        //Aquí se define la flecha para volver a la ventana principal
        ActionBar actionBar = getSupportActionBar();
        assert actionBar!=null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_36dp);

        Bundle bundle = getIntent().getExtras();
        name = bundle.getString("name");
        url = bundle.getString("url");

        Datos_url pdfDoc = new Datos_url();
        pdfDoc.setName(name);
        pdfDoc.setUrl(url);
        pdfDocs.add(pdfDoc);

        //aqui se define el recyclerView del xml
        recyclerView = (RecyclerView)findViewById(R.id.rvItem);
        adapter = new busqAdapter(Busqueda.this, pdfDocs);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(Busqueda.this));
        recyclerView.setHasFixedSize(true);
        //aqui se define el adaptador que une la informacion que se mostrará contenida en Datos.java
        //con el recyclerView y la disposición que tendrá, en esta caso LinearLayout


    }

    public boolean onOptionsItemSelected(MenuItem itm){
        switch (itm.getItemId()){
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(itm);
    }
}
