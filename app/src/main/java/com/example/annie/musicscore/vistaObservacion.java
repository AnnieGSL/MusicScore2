package com.example.annie.musicscore;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Annie on 30-01-2017.
 */

public class vistaObservacion extends AppCompatActivity {
    EditText obs;
    String fecha, desc, username, origen, descSend, time;
    int dia, mes, ano, hora, minutos, segundos;

    ProgressDialog pDialog;

    private static final String URL_FOR_OBS= "http://musictesis.esy.es/addObs.php";
    private static final String TAG = "Observacion";

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vista_obs);

        //Progress Dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        ActionBar actionBar = getSupportActionBar();
        //getSupportActionBar().hide();
        assert actionBar!=null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_36dp);

        Intent intent=this.getIntent();
        fecha = intent.getExtras().getString("DATE");
        time = intent.getExtras().getString("TIME");
        desc = intent.getExtras().getString("DESC");
        username = intent.getExtras().getString("USER");
        origen = intent.getExtras().getString("OR");

        obs = (EditText)findViewById(R.id.wObs);

        if (origen.equalsIgnoreCase("Lista")){
            obs.setText(desc);
        }
    }


    public boolean onOptionsItemSelected(MenuItem itm) {
        switch (itm.getItemId()) {
            case android.R.id.home:
                //onBackPressed();
                if(origen.equalsIgnoreCase("Lista")){
                    String filtro = username;
                    new AsyncFilt().execute(filtro);
                }else{
                    onBackPressed();
                }
                return true;
        }
        return super.onOptionsItemSelected(itm);
    }

    public void save(View view){
        final Calendar c = Calendar.getInstance();
        dia = c.get(Calendar.DAY_OF_MONTH);
        mes = c.get(Calendar.MONTH);
        ano = c.get(Calendar.YEAR);
        hora = c.get(Calendar.HOUR_OF_DAY);
        minutos = c.get(Calendar.MINUTE);
        segundos = c.get(Calendar.SECOND);

        if (origen.equalsIgnoreCase("Lista")){
            descSend = obs.getText().toString();
            enviarDatos(descSend, fecha, time, username);
            String filtro = username;
            new AsyncFilt().execute(filtro);
        }else if(origen.equalsIgnoreCase("Menu")){
            descSend = obs.getText().toString();
            fecha = ano+"-"+(mes+1)+"-"+dia;
            time = hora+":"+minutos+":"+segundos;
            enviarDatos(descSend, fecha, time, username);
        }
    }

    private void enviarDatos(final String descS, final String fech, final String tim, final String user) {
        final String fechan = ano+"-"+(mes+1)+"-"+dia;
        final String timen = hora+":"+minutos+":"+segundos;
        String cancel_req_tag = "Alumnos";
        pDialog.setMessage("Cargando...");
        showDialog();
        StringRequest strReq = new StringRequest(Request.Method.POST, URL_FOR_OBS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Obs Response: " + response.toString());
                hideDialog();
                //Toast.makeText(getApplicationContext(), "response: "+response, Toast.LENGTH_LONG).show();
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    //Toast.makeText(getApplicationContext(), "jObj: "+jObj, Toast.LENGTH_LONG).show();
                    // Check for error node in json
                    if (!error) {
                        String user = jObj.getString("name");
                        Toast.makeText(getApplicationContext(), "Observación para " + user +", almacenada con éxito", Toast.LENGTH_SHORT).show();
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
                Log.e(TAG, "Observacion Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("desc", descS);
                params.put("date", fech);
                params.put("time", tim);
                params.put("user", user);
                params.put("nDate", fechan);
                params.put("nTime", timen);
                return params;
            }
        };
        // Adding request to request queue
        AppSingleton.getInstance(getApplicationContext()).addToRequestQueue(strReq, cancel_req_tag);
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
    private class AsyncFilt extends AsyncTask<String, String, String> {
        //ProgressDialog pdLoading = new ProgressDialog(vistaObservacion.this);
        HttpURLConnection con;
        URL url = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //pdLoading.setMessage("\tLoading...");
            //pdLoading.setCancelable(false);
            //pdLoading.show();
        }
        @Override
        protected String doInBackground(String... params) {
            try {
                url = new URL("http://musictesis.esy.es/getObs.php");

            }catch(MalformedURLException e){
                e.printStackTrace();
                return e.toString();
            }
            try{
                con = (HttpURLConnection) url.openConnection();
                con.setReadTimeout(15000);
                con.setConnectTimeout(10000);
                con.setRequestMethod("POST");
                con.setDoInput(true);
                con.setDoOutput(true);

                Uri.Builder builder = new Uri.Builder().appendQueryParameter("filtro",params[0]);
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
            //pdLoading.dismiss();

            try{
                //Toast.makeText(context, "sb:"+sb, Toast.LENGTH_LONG).show();
                JSONObject jObj = new JSONObject(sb);
                boolean error = jObj.getBoolean("error");

                if (!error) {
                    JSONArray jArray = jObj.getJSONArray("user");
                    //Toast.makeText(context, "jArray"+jArray, Toast.LENGTH_LONG).show();
                    ArrayList<Datos_obs> info= new ArrayList<Datos_obs>();
                    // Extract data from json and store into ArrayList as class objects
                    for (int i = 0; i < jArray.length(); i++) {
                        Datos_obs dato = new Datos_obs();
                        JSONObject part_data = jArray.getJSONObject(i);
                        String fecha = part_data.getString("fecha");
                        String hora = part_data.getString("hora");
                        String desc = part_data.getString("descripcion");
                        String username = part_data.getString("username");
                        //MODIFICAR DATO SIMPLE PARA ENVIAR PERFIL A OTRA VENTANA
                        //Toast.makeText(MenuPpal.this, "id:"+id, Toast.LENGTH_LONG).show();
                        dato.setFecha(fecha);
                        dato.setHora(hora);
                        dato.setDesc(desc);
                        dato.setUsername(username);
                        //Toast.makeText(MenuPpal.this, "dato: "+dato, Toast.LENGTH_LONG).show();
                        info.add(dato);
                        //Toast.makeText(MenuPpal.this, "info: "+info, Toast.LENGTH_LONG).show();
                    }
                    Gson gson = new Gson();
                    //Toast.makeText(MenuPpal.this, "infoFinal: "+info, Toast.LENGTH_LONG).show();
                    String datosJson = gson.toJson(info);
                    //Toast.makeText(MenuPpal.this, "gson: "+datosJson, Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(vistaObservacion.this, creaObservacion.class);
                    intent.putExtra("Datos",datosJson);
                    vistaObservacion.this.startActivity(intent);
                    finish();
                }else {
                    // Error in login. Get the error message
                    String errorMsg = jObj.getString("error_msg");
                    Toast.makeText(vistaObservacion.this, errorMsg + ", crear nueva.", Toast.LENGTH_LONG).show();
                }

                // Setup and Handover data to recyclerview
            }catch (JSONException e) {
                Toast.makeText(vistaObservacion.this, e.toString(), Toast.LENGTH_LONG).show();
            }
        }
    }
}