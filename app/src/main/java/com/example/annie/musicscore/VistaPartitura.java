package com.example.annie.musicscore;

import android.app.ProgressDialog;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class VistaPartitura extends AppCompatActivity {
    PDFView pdfView;
    String id, correo;
    public static final int CONNECTION_TIMEOUT = 10000;
    public static final int READ_TIMEOUT = 15000;

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
        id = intent.getExtras().getString("ID");
        correo = intent.getExtras().getString("USER");
        Toast.makeText(this, "id: "+id+" correo: "+correo, Toast.LENGTH_SHORT).show();
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
                new AsyncFilt().execute(id, correo);
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

    private class AsyncFilt extends AsyncTask<String, String, String>{
        ProgressDialog pdLoading = new ProgressDialog(VistaPartitura.this);
        HttpURLConnection con;
        URL url = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pdLoading.setMessage("\tLoading...");
            pdLoading.setCancelable(false);
            pdLoading.show();
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                url = new URL("http://musictesis.esy.es/addFav.php");

            }catch(MalformedURLException e){
                e.printStackTrace();
                return e.toString();
            }
            try{
                con = (HttpURLConnection) url.openConnection();
                con.setReadTimeout(READ_TIMEOUT);
                con.setConnectTimeout(CONNECTION_TIMEOUT);
                con.setRequestMethod("POST");
                con.setDoInput(true);
                con.setDoOutput(true);

                Uri.Builder builder = new Uri.Builder().appendQueryParameter("id",params[0]).appendQueryParameter("correo", params[1]);
                String query = builder.build().getEncodedQuery();

                OutputStream os = con.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os,"UTF-8"));
                writer.write(query);
                writer.flush();
                writer.close();
                os.close();
                con.connect();

            }catch (IOException e1){
                e1.printStackTrace();
                return e1.toString();
            }
            try{
                int response_code = con.getResponseCode();
                if(response_code == HttpURLConnection.HTTP_OK){
                    InputStream input = con.getInputStream();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(input));
                    StringBuilder sb = new StringBuilder();

                    String json;
                    while((json = bufferedReader.readLine())!= null){
                        sb.append(json+"\n");
                    }
                    return (sb.toString());
                }else{
                    return ("unsuccessful");
                }
            }catch (IOException e){
                e.printStackTrace();
                return e.toString();
            }finally{
                con.disconnect();
            }
        }
        @Override
        protected void onPostExecute(String sb) {
            pdLoading.dismiss();

            try{
                Toast.makeText(VistaPartitura.this, "sb:"+sb, Toast.LENGTH_LONG).show();
                JSONObject jObj = new JSONObject(sb);
                boolean error = jObj.getBoolean("error");

                if (!error) {
                    Toast.makeText(VistaPartitura.this, "Partitura en favoritos", Toast.LENGTH_LONG).show();
                }else {
                    // Error in login. Get the error message
                    String errorMsg = jObj.getString("error_msg");
                    Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_LONG).show();
                }

                // Setup and Handover data to recyclerview
            }catch (JSONException e) {
                Toast.makeText(VistaPartitura.this, e.toString(), Toast.LENGTH_LONG).show();
            }
        }
    }
}