package com.example.annie.musicscore;


import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;

/**
 * Created by Annie on 09-01-2017.
 */

public class progresoAlm extends AppCompatActivity {
    private TextView cCompas, uCompas;
    private EditText fecha, hInicio, hFinal, cantidad, ubicacion;
    private int dia, mes, ano, horai, horaf, minutosi, minutosf;

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.prog_alm);

        cantidad = (EditText)findViewById(R.id.etCant);
        ubicacion = (EditText)findViewById(R.id.etUbi);
        cCompas = (TextView)findViewById(R.id.cant);
        uCompas = (TextView)findViewById(R.id.ubicacion);
    }
    public void fecha (View view){
        fecha = (EditText)findViewById(R.id.etFecha);
        final Calendar c = Calendar.getInstance();
        dia = c.get(Calendar.DAY_OF_MONTH);
        mes = c.get(Calendar.MONTH);
        ano = c.get(Calendar.YEAR);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                fecha.setText(dayOfMonth+"/"+monthOfYear+"/"+year);
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
                hInicio.setText(hourOfDay+":"+minute);
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
                hFinal.setText(hourOfDay+":"+minute);
            }
        }
                ,horaf,minutosf,false);
        timePickerDialog.show();
    }
    public void guardar (View view){

    }
}
