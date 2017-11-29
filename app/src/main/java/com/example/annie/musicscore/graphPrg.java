package com.example.annie.musicscore;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Annie on 20-07-2017.
 */

public class graphPrg extends AppCompatActivity {
    ArrayList<Datos_prg> array = new ArrayList<>();
    String info, date,h, pag, nMes, idP;
    ArrayList<Entry> y = new ArrayList<Entry>();
    ArrayList<String> x = new ArrayList<String>();
    ArrayList<ILineDataSet> lineDataSets = new ArrayList<ILineDataSet>();
    int year, mes, dia, min, m, pos, mesA, anoA;
    LineChart lineChart;
    private TextView pg, tit, ubi;
    private static final String TAG = "GRAFICA";
    private static final String URL_FOR_BUSCAR = "http://musictesis.esy.es/getPart.php";

    ProgressDialog pDialog;
    ArrayList<String> fechas = new ArrayList<>();
    ArrayList<Integer> minutos = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar!=null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_36dp);

        tit =(TextView)findViewById(R.id.titGraph);
        pg = (TextView)findViewById(R.id.tvPag);
        ubi = (TextView)findViewById(R.id.etPag);

        Bundle bundle = getIntent().getExtras();
        Gson gson = new Gson();
        info = bundle.getString("Datos");
        //Toast.makeText(getApplicationContext(), "info: "+info, Toast.LENGTH_LONG).show();
        Type type = new TypeToken<ArrayList<Datos_prg>>(){}.getType();
        array = gson.fromJson(info, type);

        lineChart = (LineChart)findViewById(R.id.bargraph);

        Calendar c = Calendar.getInstance();
        mesA = (c.get(Calendar.MONTH));
        anoA = c.get(Calendar.YEAR);
        //Toast.makeText(getApplicationContext(), "mes actul: "+mesA, Toast.LENGTH_LONG).show();
        int n = array.size();
        int a = n-1;
        for (int i = 0; i < array.size(); i++){
            Datos_prg data = array.get(i);
            if (i>0){
                Datos_prg d = array.get(i-1);
                if((data.getFecha()).equalsIgnoreCase(d.getFecha())){
                    m = (data.getMin()) + m;
                }else{
                    minutos.add(m);
                    fechas.add(data.getFecha());
                    m = data.getMin();
                }
            }else{
                fechas.add(data.getFecha());
                //minutos.add(data.getMin());
                m = data.getMin();
                pos = 0;
            }
        }
        minutos.add(m);
        //Toast.makeText(getApplicationContext(), "fechas: "+fechas, Toast.LENGTH_LONG).show();
        //Toast.makeText(getApplicationContext(), "minutos: "+minutos, Toast.LENGTH_LONG).show();
        int j=0;
        for (int i = 0; i < fechas.size(); i++ ){
            date = fechas.get(i);
            year = Integer.parseInt(date.substring(0,4));
            mes = Integer.parseInt(date.substring(5,7));
            dia = Integer.parseInt(date.substring(8,date.length()));
            min = minutos.get(i);
            if(mes == (mesA+1)) {
                y.add(new Entry(min, j));
                h = String.valueOf(dia);
                x.add("Día " + h);
                j = j+1;
            }
        }
        if(y.isEmpty()){
            Toast.makeText(getApplicationContext(), "No hay progreso registrado el presente mes.", Toast.LENGTH_LONG).show();
            finish();
            onBackPressed();
        }else {

            switch(mes){
                case 1:
                    nMes = "Enero";
                    break;
                case 2:
                    nMes = "Febrero";
                    break;
                case 3:
                    nMes = "Marzo";
                    break;
                case 4:
                    nMes = "Abril";
                    break;
                case 5:
                    nMes = "Mayo";
                    break;
                case 6:
                    nMes = "Junio";
                    break;
                case 7:
                    nMes = "Julio";
                    break;
                case 8:
                    nMes = "Agosto";
                    break;
                case 9:
                    nMes = "Septiembre";
                    break;
                case 10:
                    nMes = "Octubre";
                    break;
                case 11:
                    nMes = "Noviembre";
                    break;
                case 12:
                    nMes = "Diciembre";
                    break;
                default:
                    nMes ="Este mes";
                    break;
            }

            tit.setText("Minutos dedicados al día en "+nMes);
            LineDataSet lineDataSet = new LineDataSet(y, "Minutos diarios");
            lineDataSet.setDrawCircles(true);
            lineDataSet.setColor(Color.CYAN);
            lineDataSet.setDrawCubic(true);
            lineDataSet.setDrawFilled(true);
            lineDataSets.add(lineDataSet);
            lineChart.setData(new LineData(x, lineDataSets));
            lineChart.setDescription("");
            //lineChart.invalidate();
            Datos_prg d = array.get(a);
            int s = d.getPag();
            int id = d.getId();
            pag = String.valueOf(s);
            idP = String.valueOf(id);
            ubi.setText(pag);
            buscarPart(idP);
        }
        //Toast.makeText(graphPrg.this, "x: "+x+"y: "+y, Toast.LENGTH_LONG).show();
    }

    private void buscarPart(final String busq) {
        String cancel_req_tag = "Alumnos";
        //pDialog.setMessage("Cargando...");
        //showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST, URL_FOR_BUSCAR, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Busca Response: " + response.toString());
                //hideDialog();
                //Toast.makeText(getApplicationContext(), "response: "+response, Toast.LENGTH_LONG).show();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    //Toast.makeText(getApplicationContext(), "error: "+error, Toast.LENGTH_LONG).show();
                    // Check for error node in json
                    if (!error) {
                        String name = jObj.getJSONArray("user").getJSONObject(0).getString("titulo");
                        //Toast.makeText(MenuPpal.this, "jArray"+jArray, Toast.LENGTH_LONG).show();
                        pg.setText("Ultima página o compás estudiado en "+name+" es:");
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
                Log.e(TAG, "Alumnos Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                //hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("filtro", busq);
                return params;
            }
        };
        // Adding request to request queue
        AppSingleton.getInstance(getApplicationContext()).addToRequestQueue(strReq, cancel_req_tag);
    }

    public boolean onOptionsItemSelected(MenuItem itm){
        switch (itm.getItemId()){
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(itm);
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
