package com.example.annie.musicscore;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class VistaPartitura extends AppCompatActivity {
    PDFView pdfView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vista_partitura);

        ActionBar actionBar = getSupportActionBar();
        //getSupportActionBar().hide();
        assert actionBar!=null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_36dp);

        pdfView = (PDFView) findViewById(R.id.pdfView);
        //ScrollBar scrollBar = (ScrollBar)findViewById(R.id.scrollBar);
        //pdfView.setScrollBar(scrollBar);
        //scrollBar.setHorizontal(false);
        Intent intent=this.getIntent();
        String path = intent.getExtras().getString("PATH");
        Toast.makeText(this, "url: "+path, Toast.LENGTH_SHORT).show();
        new RetrievePDFStream().execute(path);

        //Modalidad para leer desde almacenamiento del movil
        //File file = new File(path);
        /*
        if(file.canRead()){
            pdfView.fromFile(file).defaultPage(1).onLoad(new OnLoadCompleteListener() {
                @Override
                public void loadComplete(int nbPages) {
                    Toast.makeText(VistaPartitura.this, String.valueOf(nbPages), Toast.LENGTH_LONG).show();
                }
            }).load();
        }
        */
    }

    public boolean onOptionsItemSelected(MenuItem itm){
        switch (itm.getItemId()){
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.agregar_partitura:
                Toast.makeText(VistaPartitura.this, "Partitura en favoritos", Toast.LENGTH_LONG).show();
                return true;
            case R.id.descargar_partitura:
                Toast.makeText(VistaPartitura.this, "Iniciando Descarga", Toast.LENGTH_LONG).show();
                return true;
            case R.id.registrar_progreso:
                Intent i = new Intent(this, progresoAlm.class);
                this.startActivity(i);
                break;
        }
        return super.onOptionsItemSelected(itm);
    }


    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.popup_part, menu);
        return true;
    }

    class RetrievePDFStream extends AsyncTask<String,Void,InputStream>{
        @Override
        protected InputStream doInBackground(String... strings) {
            InputStream inputStream = null;
            try{
                URL url = new URL(strings[0]);
                HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();
                if(urlConnection.getResponseCode() == 200){
                    inputStream = new BufferedInputStream(urlConnection.getInputStream());
                }
            }
            catch (IOException e){
                return null;
            }
            return inputStream;
        }

        protected void onPostExecute(InputStream inputStream){
            pdfView.fromStream(inputStream).load();
        }
    }
}