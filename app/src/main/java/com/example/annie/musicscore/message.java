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


public class message extends AppCompatActivity {
    EditText msg;
    String fecha, receptor, emisor, msj, time;
    int dia, mes, ano, hora, minutos, segundos;
    int flag;

    ProgressDialog pDialog;

    private static final String URL_FOR_MSG= "http://musictesis.esy.es/addMsg.php";
    private static final String URL_FOR_PUSH = "http://musictesis.esy.es/pushNotification.php";
    private static final String TAG = "Mensaje";

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.message);

        //Progress Dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        ActionBar actionBar = getSupportActionBar();
        //getSupportActionBar().hide();
        assert actionBar!=null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_36dp);

        Intent intent=this.getIntent();
        receptor = intent.getExtras().getString("username");
        emisor = intent.getExtras().getString("emisor");

        msg = (EditText)findViewById(R.id.etmsg);

    }


    public boolean onOptionsItemSelected(MenuItem itm) {
        switch (itm.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(itm);
    }

    public void send(View view){
        final Calendar c = Calendar.getInstance();
        dia = c.get(Calendar.DAY_OF_MONTH);
        mes = c.get(Calendar.MONTH);
        ano = c.get(Calendar.YEAR);
        hora = c.get(Calendar.HOUR_OF_DAY);
        minutos = c.get(Calendar.MINUTE);
        segundos = c.get(Calendar.SECOND);

        msj = msg.getText().toString();
        fecha = ano+"-"+(mes+1)+"-"+dia;
        time = hora+":"+minutos+":"+segundos;
        flag = 0;
        enviarDatos(msj, fecha, time, emisor,receptor, flag);
        sendNotificacion(msj, emisor, receptor);
    }

    private void enviarDatos(final String ms, final String fech, final String tim, final String emi, final String rec, final int fl) {
        final String fla = String.valueOf(fl);
        //Toast.makeText(getApplicationContext(), ms+","+fech+","+tim+","+emi+","+rec+","+fla, Toast.LENGTH_LONG).show();
        String cancel_req_tag = "Alumnos";
        pDialog.setMessage("Cargando...");
        showDialog();
        StringRequest strReq = new StringRequest(Request.Method.POST, URL_FOR_MSG, new Response.Listener<String>() {
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
                        Toast.makeText(getApplicationContext(), "Mensaje enviado a " + user, Toast.LENGTH_SHORT).show();
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
                params.put("msg", ms);
                params.put("date", fech);
                params.put("time", tim);
                params.put("emisor", emi);
                params.put("receptor", rec);
                params.put("flag",fla);
                return params;
            }
        };
        // Adding request to request queue
        AppSingleton.getInstance(getApplicationContext()).addToRequestQueue(strReq, cancel_req_tag);
    }

    private void sendNotificacion(final String mens, final String emi, final String rec){
        //Toast.makeText(getApplicationContext(), mens+","+emi+","+rec, Toast.LENGTH_LONG).show();
        String cancel_req_tag = "Alumnos";
        pDialog.setMessage("Cargando...");
        showDialog();
        StringRequest strReq = new StringRequest(Request.Method.POST, URL_FOR_PUSH, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Obs Response: " + response.toString());
                hideDialog();
                //Toast.makeText(getApplicationContext(), "response: "+response, Toast.LENGTH_LONG).show();
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    //Toast.makeText(getApplicationContext(), "try: "+jObj, Toast.LENGTH_LONG).show();
                    // Check for error node in json
                    if (!error) {
                        //Toast.makeText(getApplicationContext(), "try: "+ response, Toast.LENGTH_SHORT).show();
                    } else {
                        // Error in login. Get the error message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    //Toast.makeText(getApplicationContext(), "Json error notif: " + e.getMessage(), Toast.LENGTH_LONG).show();
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
                params.put("mCont", mens);
                params. put("emisor", emi);
                params.put("receptor", rec);
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