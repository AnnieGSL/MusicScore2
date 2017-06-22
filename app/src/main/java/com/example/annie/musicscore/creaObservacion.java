package com.example.annie.musicscore;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Annie on 30-01-2017.
 */

public class creaObservacion extends AppCompatActivity {
    RecyclerView recyclerView;
    partAdapter adapter; //CAMBIAR, CREAR ADAPTADOR PARA QYE LEA BASE DE DATOS MYSQL

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.crea_obs);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar!=null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_36dp);

        recyclerView = (RecyclerView)findViewById(R.id.rvItem);
        adapter = new partAdapter(this, getPDFs());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
    }

    //QUITARpdf

    private ArrayList<Datos_simple> getPDFs() {
        ArrayList<Datos_simple> pdfDocs = new ArrayList<>();
        File downloadsFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        Datos_simple pdfDoc;
        if(downloadsFolder.exists()){
            File[] files = downloadsFolder.listFiles();
            for (int i=0; i<files.length; i++){
                File file=files[i];
                if(file.getPath().endsWith("pdf")){
                    pdfDoc = new Datos_simple();
                    pdfDoc.setName(file.getName());
                    pdfDoc.setPath(file.getAbsolutePath());
                    pdfDocs.add(pdfDoc);
                }
            }
        }
        return pdfDocs;
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