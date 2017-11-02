package com.example.annie.musicscore;


import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Annie on 09-01-2017.
 */

public class progresoAlm extends AppCompatActivity {
    private TextView fecha, hInicio, hFinal, cCompas, uCompas, info, infonum;
    private EditText  cantidad;
    private int dia; private int mes; private int ano; private int horai; private int horaf; private int minutosi; private int minutosf; private int hi;
    private int mi;
    private int hf;
    private int mf; private int min; private int hr;
    private String progreso;
    private static final String URL_FOR_INSERT = "http://musictesis.esy.es/progreso.php";
    private static final String URL_FOR_PROGRESO = "http://musictesis.esy.es/getProg.php";
    private static final String TAG = "Progreso";
    private String id, correo;
    ProgressDialog pDialog;

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.prog_alm);

        //Progress Dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        cantidad = (EditText)findViewById(R.id.etCant);
        cCompas = (TextView)findViewById(R.id.cant);
        fecha = (TextView)findViewById(R.id.etFecha);
        hInicio = (TextView) findViewById(R.id.ethi);
        hFinal = (TextView) findViewById(R.id.ethf);
        info = (TextView)findViewById(R.id.info);
        infonum = (TextView)findViewById(R.id.infonum);

        Intent intent=this.getIntent();
        id = intent.getExtras().getString("id");// id de partitura
        correo = intent.getExtras().getString("user");
        buscarProg(id, correo);
    }
    public void fecha (View view){
        final Calendar c = Calendar.getInstance();
        dia = c.get(Calendar.DAY_OF_MONTH);
        mes = c.get(Calendar.MONTH);
        ano = c.get(Calendar.YEAR);

        //Toast.makeText(getApplicationContext(), "dia: "+dia +"mes: "+mes+"año: "+ano, Toast.LENGTH_LONG).show();

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                fecha.setText(dayOfMonth+"/"+(monthOfYear+1)+"/"+year);
            }
        }
        ,dia, mes, ano);
        datePickerDialog.show();
    }
    public void inicio (View view){
        final Calendar c = Calendar.getInstance();
        horai = c.get(Calendar.HOUR_OF_DAY);
        minutosi = c.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                if(hourOfDay<10){
                    if(minute == 0) {
                        hInicio.setText("0" + hourOfDay + ":" + minute + "0");
                    }else if(minute < 10) {
                        hInicio.setText("0" + hourOfDay + ":" + "0" + minute);
                    }else{
                        hInicio.setText("0" + hourOfDay + ":" + minute);
                    }
                }else if(minute == 0) {
                    hInicio.setText(hourOfDay + ":" + minute + "0");
                }else if(minute < 10){
                    hInicio.setText(hourOfDay + ":" + "0" + minute);
                }else{
                    hInicio.setText(hourOfDay + ":" + minute);
                }
                hi = hourOfDay;
                mi = minute;
            }
        }
        ,horai,minutosi,false);
        timePickerDialog.show();

    }
    public void fin (View view){
        final Calendar c = Calendar.getInstance();
        horaf = c.get(Calendar.HOUR_OF_DAY);
        minutosf = c.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                if(hourOfDay<10){
                    if(minute == 0) {
                        hFinal.setText("0" + hourOfDay + ":" + minute + "0");
                    }else if(minute < 10) {
                        hFinal.setText("0" + hourOfDay + ":" + "0" + minute);
                    }else{
                        hFinal.setText("0" + hourOfDay + ":" + minute);
                    }
                }else if(minute == 0) {
                    hFinal.setText(hourOfDay + ":" + minute + "0");
                }else if(minute < 10){
                    hFinal.setText(hourOfDay + ":" + "0" + minute);
                }else{
                    hFinal.setText(hourOfDay + ":" + minute);
                }
                hf = hourOfDay;
                mf = minute;
            }
        }
        ,horaf,minutosf,false);
        timePickerDialog.show();
    }
    public void guardar (View view){
        if(mf < mi){
            mf = mf + 60;
            if(hf<hi) {
                hf = (hf + hi);
                min = mf - mi;
                hr = hf - hi;
            }else{
                hf = hf - 1;
                min = mf - mi;
                hr = hf - hi;
            }
        }else if (hf < hi){
            hf = hf + 24;
            min = mf - mi;
            hr = hf - hi;
        }else{
            min = mf-mi;
            hr = hf-hi;
        }
        hr = hr * 60;
        min = min + hr;
        String mn = ""+min+"";
        String f;
        //Toast.makeText(getApplicationContext(), "minutos: "+min, Toast.LENGTH_LONG).show();
        if(ano == 0 & mes == 0 & dia ==0){
            f = "";
        }else{
            f = ano+"-"+(mes+1)+"-"+dia;
        }
        addProgres(f.toString(), hInicio.getText().toString()+":00", hFinal.getText().toString()+":00", mn.toString(), cantidad.getText().toString());
    }

    public void addProgres(final String fecha, final String inicio, final String fin, final String m, final String cantidad){
        String cancel_req_tag = "progreso";
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
                        Toast.makeText(getApplicationContext(), "Progreso guardado con éxito!", Toast.LENGTH_LONG).show();
                        onBackPressed();
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
                Log.e(TAG, "Progreso Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                //hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("fecha", fecha);
                params.put("hInicio", inicio);
                params.put("hFin", fin);
                params.put("cantCompas", cantidad);
                params.put("id", id);
                params.put("correo", correo);
                params.put("min",m);
                return params;
            }
        };
        // Adding request to request queue
        AppSingleton.getInstance(getApplicationContext()).addToRequestQueue(strReq, cancel_req_tag);
    }

    private void buscarProg(final String idb, final String correob) {
        String cancel_req_tag = "Mensajes";
        pDialog.setMessage("Cargando...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST, URL_FOR_PROGRESO, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Busca Response: " + response.toString());
                hideDialog();
                //Toast.makeText(getApplicationContext(), "response: "+response, Toast.LENGTH_LONG).show();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    //Toast.makeText(getApplicationContext(), "jObj: "+jObj, Toast.LENGTH_LONG).show();
                    // Check for error node in json
                    if (!error) {
                        JSONArray jArray = jObj.getJSONArray("user");
                        //Toast.makeText(MenuPpal.this, "jArray"+jArray, Toast.LENGTH_LONG).show();
                        // Extract data from json and store into ArrayList as class objects
                        for (int i = jArray.length()-1; i >=0 ; i--) {
                            JSONObject part_data = jArray.getJSONObject(i);
                            progreso = part_data.getString("compases");
                            //Toast.makeText(MenuPpal.this, "id:"+id, Toast.LENGTH_LONG).show();
                            //Toast.makeText(MenuPpal.this, "dato: "+dato, Toast.LENGTH_LONG).show();
                            //Toast.makeText(MenuPpal.this, "info: "+info, Toast.LENGTH_LONG).show();
                        }
                        infonum.setText(progreso);
                        //Toast.makeText(MenuPpal.this, "infoFinal: "+info, Toast.LENGTH_LONG).show();
                        //Toast.makeText(MenuPpal.this, "gson: "+datosJson, Toast.LENGTH_LONG).show();
                        //finish();

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
                Log.e(TAG, "Mensaje Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("id", idb);
                params.put("correo", correob);
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
