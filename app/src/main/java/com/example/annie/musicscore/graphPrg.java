package com.example.annie.musicscore;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by Annie on 20-07-2017.
 */

public class graphPrg extends AppCompatActivity {
    ArrayList<Datos_prg> array = new ArrayList<>();
    String info, date,h;
    ArrayList<Entry> y = new ArrayList<>();
    ArrayList<String> x = new ArrayList<>();
    ArrayList<ILineDataSet> lineDataSets = new ArrayList<>();
    int year, mes, dia, min, m, pos;
    LineChart lineChart;

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


        Bundle bundle = getIntent().getExtras();
        Gson gson = new Gson();
        info = bundle.getString("Datos");
        //Toast.makeText(getApplicationContext(), "info: "+info, Toast.LENGTH_LONG).show();
        Type type = new TypeToken<ArrayList<Datos_prg>>(){}.getType();
        array = gson.fromJson(info, type);

        lineChart = (LineChart)findViewById(R.id.bargraph);

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
        for (int i = 0; i < fechas.size(); i++ ){
            date = fechas.get(i);
            year = Integer.parseInt(date.substring(0,4));
            mes = Integer.parseInt(date.substring(5,7));
            dia = Integer.parseInt(date.substring(8,date.length()));
            min = minutos.get(i);
            y.add(new Entry(min,i));
            h = String.valueOf(dia);
            x.add("Día "+h);
        }
        LineDataSet lineDataSet = new LineDataSet(y,"Minutos diarios");
        lineDataSet.setDrawCircles(true);
        lineDataSet.setColor(Color.BLUE);
        lineDataSets.add(lineDataSet);
        lineChart.setData(new LineData(x,lineDataSets));

        //Toast.makeText(graphPrg.this, "x: "+x+"y: "+y, Toast.LENGTH_LONG).show();
    }

    public boolean onOptionsItemSelected(MenuItem itm){
        switch (itm.getItemId()){
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(itm);
    }

}
