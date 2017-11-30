package com.example.annie.musicscore;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Annie on 28-08-2017.
 */

public class VistaMensaje extends AppCompatActivity {
    TextView msg;
    String mensaje, emisor, receptor, fuente, name, p;
    private static final String URL_FOR_MENSAJE = "http://musictesis.esy.es/getMsg.php";
    private static final String TAG = "MENSAJE";

    ProgressDialog pDialog;

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vista_msg);

        //Progress Dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        ActionBar actionBar = getSupportActionBar();
        //getSupportActionBar().hide();
        assert actionBar!=null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_36dp);

        Intent intent=this.getIntent();
        mensaje= intent.getExtras().getString("mensaje");
        emisor = intent.getExtras().getString("emisor");
        receptor = intent.getExtras().getString("username");
        fuente = intent.getExtras().getString("fuente");
        name = intent.getExtras().getString("name");
        p = intent.getExtras().getString("perfil");

        msg = (TextView)findViewById(R.id.msj);

        msg.setText(mensaje);
    }

    public void answer(View view){
        String em = receptor;
        String re = emisor;
        Intent i = new Intent(this, message.class);
        i.putExtra("emisor", em);
        i.putExtra("username", re);
        this.startActivity(i);
    }

    public boolean onOptionsItemSelected(MenuItem itm) {
        switch (itm.getItemId()) {
            case android.R.id.home:
                //onBackPressed();
                String filtro = receptor;
                buscarMsg(filtro);
                return true;
        }
        return super.onOptionsItemSelected(itm);
    }

    private void buscarMsg(final String filtro) {
        String cancel_req_tag = "Mensajes";
        pDialog.setMessage("Cargando...");
        //showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST, URL_FOR_MENSAJE, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Busca Response: " + response.toString());
                //hideDialog();
                //Toast.makeText(getApplicationContext(), "response: "+response, Toast.LENGTH_LONG).show();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    //Toast.makeText(getApplicationContext(), "jObj: "+jObj, Toast.LENGTH_LONG).show();
                    // Check for error node in json
                    if (!error) {
                        JSONArray jArray = jObj.getJSONArray("user");
                        //Toast.makeText(MenuPpal.this, "jArray"+jArray, Toast.LENGTH_LONG).show();
                        ArrayList<msg> info= new ArrayList<msg>();
                        // Extract data from json and store into ArrayList as class objects
                        for (int i = 0; i < jArray.length(); i++) {
                            msg dato = new msg();
                            JSONObject part_data = jArray.getJSONObject(i);
                            String id = part_data.getString("idMsj");
                            String msg = part_data.getString("message");
                            String fech = part_data.getString("fecha");
                            String hr = part_data.getString("hora");
                            String em = part_data.getString("emisor");
                            String rec = part_data.getString("receptor");
                            int fl = part_data.getInt("flag");
                            String nRec = name;
                            String pl = p;
                            //Toast.makeText(MenuPpal.this, "id:"+id, Toast.LENGTH_LONG).show();
                            dato.setId(id);
                            dato.setMensaje(msg);
                            dato.setFecha(fech);
                            dato.setHora(hr);
                            dato.setEmisor(em);
                            dato.setReceptor(rec);
                            dato.setFlag(fl);
                            dato.setNameReceptor(nRec);
                            dato.setPerf(pl);
                            //Toast.makeText(MenuPpal.this, "dato: "+dato, Toast.LENGTH_LONG).show();
                            info.add(dato);
                            //Toast.makeText(MenuPpal.this, "info: "+info, Toast.LENGTH_LONG).show();
                        }
                        Gson gson = new Gson();
                        //Toast.makeText(MenuPpal.this, "infoFinal: "+info, Toast.LENGTH_LONG).show();
                        String datosJson = gson.toJson(info);
                        //Toast.makeText(MenuPpal.this, "gson: "+datosJson, Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(VistaMensaje.this, listMessage.class);
                        intent.putExtra("Datos",datosJson);
                        intent.putExtra("nombre", name);
                        intent.putExtra("username", receptor);
                        intent.putExtra("perfil", p);
                        VistaMensaje.this.startActivity(intent);
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
                Log.e(TAG, "Mensaje Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                //hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("filtro", filtro);
                return params;
            }
        };
        // Adding request to request queue
        AppSingleton.getInstance(getApplicationContext()).addToRequestQueue(strReq, cancel_req_tag);
    }

}
