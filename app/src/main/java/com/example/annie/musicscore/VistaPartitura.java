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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.github.barteksc.pdfviewer.PDFView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class VistaPartitura extends AppCompatActivity {
    PDFView pdfView;
    String id, correo, path, origen, perfil, name;
    private static final String URL_FOR_INSERT = "http://musictesis.esy.es/addFav.php";
    private static final String TAG = "VistaPartitura";
    ProgressDialog pDialog;
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

        //Progress Dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        pdfView = (PDFView) findViewById(R.id.pdfView);
        //ScrollBar scrollBar = (ScrollBar)findViewById(R.id.scrollBar);
        //pdfView.setScrollBar(scrollBar);
        //scrollBar.setHorizontal(false);
        Intent intent=this.getIntent();
        name = intent.getExtras().getString("NAME");
        path = intent.getExtras().getString("PATH");
        id = intent.getExtras().getString("ID");
        correo = intent.getExtras().getString("USER");
        perfil = intent.getExtras().getString("PERFIL");
        origen = intent.getExtras().getString("origen");
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
                insertDatos();
                return true;
            case R.id.descargar_partitura:
                Toast.makeText(VistaPartitura.this, "Iniciando Descarga", Toast.LENGTH_LONG).show();
                descargar(path);
                return true;
            case R.id.registrar_progreso:
                Intent i = new Intent(this, progresoAlm.class);
                i.putExtra("id", id);
                i.putExtra("user", correo);
                i.putExtra("name", name);
                i.putExtra("perf", perfil);
                this.startActivity(i);
                break;
        }
        return super.onOptionsItemSelected(itm);
    }


    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if (origen.equalsIgnoreCase("busqueda")){
            if (perfil.equalsIgnoreCase("Profesor")){
                getMenuInflater().inflate(R.menu.popup_partit, menu);
            }else {
                getMenuInflater().inflate(R.menu.popup_part, menu);
            }
        }else if (origen.equalsIgnoreCase("partitura")){
            getMenuInflater().inflate(R.menu.popup_parti, menu);
        }
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

    private void insertDatos(){
        String cancel_req_tag = "vista";
        pDialog.setMessage("Cargando...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST, URL_FOR_INSERT, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Login Response: " + response.toString());
                hideDialog();
                //Toast.makeText(getApplicationContext(), "response: "+response, Toast.LENGTH_LONG).show();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    //Toast.makeText(getApplicationContext(), "jObj: "+jObj, Toast.LENGTH_LONG).show();
                    // Check for error node in json
                    if (!error) {
                        String user = jObj.getString("nombre");
                        Toast.makeText(getApplicationContext(), user +",fue agregada exitosamente", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        // Error in login. Get the error message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Login Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("fk_user", correo);
                params.put("fk_score", id);
                return params;
            }
        };
        // Adding request to request queue
        AppSingleton.getInstance(getApplicationContext()).addToRequestQueue(strReq, cancel_req_tag);
    }

    private void descargar(String path){
        downloadManager = (DownloadManager)getSystemService(Context.DOWNLOAD_SERVICE);
        Toast.makeText(this, "uri pdf: "+path, Toast.LENGTH_SHORT).show();
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