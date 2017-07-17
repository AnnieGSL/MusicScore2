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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Annie on 09-01-2017.
 */

public class progresoAlm extends AppCompatActivity {
    private TextView cCompas, uCompas;
    private EditText fecha, hInicio, hFinal, cantidad;
    private int dia, mes, ano, horai, horaf, minutosi, minutosf;
    private static final String URL_FOR_INSERT = "http://musictesis.esy.es/progreso.php";
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
        fecha = (EditText)findViewById(R.id.etFecha);

        Intent intent=this.getIntent();
        id = intent.getExtras().getString("id");
        correo = intent.getExtras().getString("user");
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
        hInicio = (EditText)findViewById(R.id.ethi);
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
            }
        }
        ,horai,minutosi,false);
        timePickerDialog.show();
    }
    public void fin (View view){
        hFinal = (EditText)findViewById(R.id.ethf);
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
            }
        }
        ,horaf,minutosf,false);
        timePickerDialog.show();
    }
    public void guardar (View view){
        String f = ano+"-"+(mes+1)+"-"+dia;
        addProgres(f.toString(), hInicio.getText().toString()+":00", hFinal.getText().toString()+":00", cantidad.getText().toString());
    }

    public void addProgres(final String fecha, final String inicio, final String fin, final String cantidad){
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
