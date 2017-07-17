package com.example.annie.musicscore;

import android.app.ProgressDialog;
import android.content.Intent;
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

import org.json.JSONException;
import org.json.JSONObject;

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
                onBackPressed();
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
        }else if(origen.equalsIgnoreCase("Menu")){
            descSend = obs.getText().toString();
            fecha = ano+"-"+(mes+1)+"-"+dia;
            time = hora+":"+minutos+":"+segundos;
            enviarDatos(descSend, fecha, time, username);
        }
    }

    private void enviarDatos(final String descS, final String fech, final String tim, final String user) {
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
}