package com.example.annie.musicscore;

import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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

/**
 * Created by Annie on 12-07-2017.
 */

public class miPartitura extends AppCompatActivity {
    PDFView pdfView;
    String id, correo, path;
    public static final int CONNECTION_TIMEOUT = 10000;
    public static final int READ_TIMEOUT = 15000;
    private static final String URL_FOR_INSERT = "http://musictesis.esy.es/addFav.php";
    private static final String TAG = "VistaPartitura";
    private ProgressDialog pDialog;
    DownloadManager downloadManager;

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
        path = intent.getExtras().getString("PATH");
        id = intent.getExtras().getString("ID");
        correo = intent.getExtras().getString("USER");
        new miPartitura.RetrievePDFStream().execute(path);
    }

    public boolean onOptionsItemSelected(MenuItem itm){
        switch (itm.getItemId()){
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.descargar_partitura:
                Toast.makeText(miPartitura.this, "Iniciando Descarga", Toast.LENGTH_LONG).show();
                descargar(path);
                return true;
            case R.id.registrar_progreso:
                Intent i = new Intent(this, progresoAlm.class);
                i.putExtra("id", id);
                i.putExtra("user", correo);
                this.startActivity(i);
                break;
        }
        return super.onOptionsItemSelected(itm);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.popup_parti, menu);
        return true;
    }

    class RetrievePDFStream extends AsyncTask<String,Void,InputStream> {
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

    private void descargar(String path){
        downloadManager = (DownloadManager)getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(path);
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        Long reference = downloadManager.enqueue(request);
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
}
