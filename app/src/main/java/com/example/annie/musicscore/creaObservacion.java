package com.example.annie.musicscore;

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
 * Created by Annie on 30-01-2017.
 */

public class creaObservacion extends AppCompatActivity {
    RecyclerView recyclerView;
    obsAdapter adapter;
    String info, username;
    ArrayList<Datos_obs> array = new ArrayList<>();

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.crea_obs);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar!=null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_36dp);

        Bundle bundle = getIntent().getExtras();
        Gson gson = new Gson();
        info = bundle.getString("Datos");
        Type type = new TypeToken<ArrayList<Datos_obs>>(){}.getType();
        array = gson.fromJson(info, type);

        recyclerView = (RecyclerView)findViewById(R.id.rvItem);
        adapter = new obsAdapter(this, array);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
    }

/*
    //Lee dato desde memoria interna
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
                    pdfDoc.setUrl(file.getAbsolutePath());
                    pdfDocs.add(pdfDoc);
                }
            }
        }
        return pdfDocs;
    }
*/
    public boolean onOptionsItemSelected(MenuItem itm){
        switch (itm.getItemId()){
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(itm);
    }
}