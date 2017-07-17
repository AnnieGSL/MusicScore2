package com.example.annie.musicscore;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
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
import java.util.HashMap;
import java.util.Map;

public class MenuPpal extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    private TextView titulo, notas, sol, silencio, acorde, penta, otro;
    private String name, username, perfil, busq;

    private static final String TAG = "Busqueda";
    private static String URL_FOR_FILTRO = "http://musictesis.esy.es/getPdfs.php";
    private static final String URL_FOR_FILTRo = "http://musictesis.esy.es/getAl.php";
    private static final String URL_FOR_PART = "http://musictesis.esy.es/getPdfs(json).php";
    private static final String URL_FOR_ALMNOS ="http://musictesis.esy.es/getAlmn.php";
    private static final String URL_FOR_BUSCAR = "http://musictesis.esy.es/getAlm.php";

    ProgressDialog pDialog;

    public static final int CONNECTION_TIMEOUT = 10000;
    public static final int READ_TIMEOUT = 15000;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Progress Dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        Bundle bundle = getIntent().getExtras();
        name = bundle.getString("nombre");
        username = bundle.getString("correo");
        perfil = bundle.getString("perfil");
        if(perfil.equalsIgnoreCase( "Profesor")){
            setContentView(R.layout.activity_menu_ppal);
        }else if(perfil.equalsIgnoreCase( "Alumno")){
            setContentView(R.layout.activity_menu_ppal2);
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        busq = "Alumno";
        titulo = (TextView)findViewById(R.id.titulo);
        notas = (TextView)findViewById(R.id.notas);
        sol = (TextView)findViewById(R.id.sol);
        silencio = (TextView)findViewById(R.id.silencio);
        acorde = (TextView)findViewById(R.id.acorde);
        penta = (TextView)findViewById(R.id.penta);
        otro = (TextView)findViewById(R.id.otro);


        //PERSAR SI QUITAR O QUE FUNCIONALIDAD DAR
        /*
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        */

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View navHeader = navigationView.getHeaderView(0);
        TextView nUsuario = (TextView)navHeader.findViewById(R.id.Nombre);
        TextView cUsuario = (TextView)navHeader.findViewById(R.id.Correo) ;

        nUsuario.setText(name);
        cUsuario.setText(username);

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        main m = new main();
        ft.add(R.id.content_frame, m);
        ft.commit();
        //fm.beginTransaction().replace(R.id.content_frame, new main()).commit();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if (perfil.equalsIgnoreCase("Profesor")) {
            getMenuInflater().inflate(R.menu.menu_al, menu);
        }else if(perfil.equalsIgnoreCase("Alumno")){
            getMenuInflater().inflate(R.menu.menu_ppal, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        if(id == R.id.action_add) {
            //Cargar en un recyclerView todos los alumnos con filtro de perfil.
            buscarAlm();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void buscarAlm() {
        String cancel_req_tag = "Alumnos";
        pDialog.setMessage("Cargando...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST, URL_FOR_BUSCAR, new Response.Listener<String>() {

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
                        ArrayList<item> info= new ArrayList<item>();
                        // Extract data from json and store into ArrayList as class objects
                        for (int i = 0; i < jArray.length(); i++) {
                            item dato = new item();
                            JSONObject part_data = jArray.getJSONObject(i);
                            String nm = part_data.getString("name");
                            String us = part_data.getString("username");
                            String pr = username;
                            //Toast.makeText(MenuPpal.this, "id:"+id, Toast.LENGTH_LONG).show();
                            dato.setName(nm);
                            dato.setUsername(us);
                            dato.setProfe(pr);
                            //Toast.makeText(MenuPpal.this, "dato: "+dato, Toast.LENGTH_LONG).show();
                            info.add(dato);
                            //Toast.makeText(MenuPpal.this, "info: "+info, Toast.LENGTH_LONG).show();
                        }
                        Gson gson = new Gson();
                        //Toast.makeText(MenuPpal.this, "infoFinal: "+info, Toast.LENGTH_LONG).show();
                        String datosJson = gson.toJson(info);
                        //Toast.makeText(MenuPpal.this, "gson: "+datosJson, Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(MenuPpal.this, listaAlumnos.class);
                        intent.putExtra("Datos",datosJson);
                        intent.putExtra("nombre", name);
                        intent.putExtra("username", username);
                        intent.putExtra("perfil", perfil);
                        MenuPpal.this.startActivity(intent);
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
                Log.e(TAG, "Alumnos Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        FragmentManager fm = getSupportFragmentManager();
        int id = item.getItemId();
        if (id == R.id.nav_principal) {
            fm.beginTransaction().replace(R.id.content_frame, new main()).commit();
            //Toast.makeText(this, "Principal", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_cuenta) {
            Perfil perfil = new Perfil();
            fm.beginTransaction().replace(R.id.content_frame, perfil).commit();
            //Toast.makeText(this, "Perfil", Toast.LENGTH_SHORT).show();
        }else if (id == R.id.nav_partituras){
            String filtro = username;
            new AsyncFiltr().execute(filtro);
        } else if (id == R.id.nav_mensajes) {
            Toast.makeText(this, "Lista de Mensaje", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_alumnos) {
            String filtro = username;
            new AsyncFiltro().execute(filtro);
            //Toast.makeText(this, "Lista de Alumnos", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_sesion) {
            Intent i = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(i);
        } else if (id == R.id.nav_info) {
            AcercaDe acerca = new AcercaDe();
            fm.beginTransaction().replace(R.id.content_frame, acerca).commit();
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void buscar (View view) {
        EditText et = (EditText)findViewById(R.id.et);
        String filtro = et.getText().toString();
        new AsyncFilt().execute(filtro);
    }

    private class AsyncFilt extends AsyncTask<String, String, String>{
        ProgressDialog pdLoading = new ProgressDialog(MenuPpal.this);
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
                    url = new URL(URL_FOR_FILTRO);

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
                pdLoading.dismiss();

                try{
                    //Toast.makeText(MenuPpal.this, "sb:"+sb, Toast.LENGTH_LONG).show();
                    JSONObject jObj = new JSONObject(sb);
                    boolean error = jObj.getBoolean("error");

                    if (!error) {
                        JSONArray jArray = jObj.getJSONArray("partitura");
                        //Toast.makeText(MenuPpal.this, "jArray"+jArray, Toast.LENGTH_LONG).show();
                        ArrayList<Datos_url> info= new ArrayList<Datos_url>();
                        // Extract data from json and store into ArrayList as class objects
                        for (int i = 0; i < jArray.length(); i++) {
                            Datos_url dato = new Datos_url();
                            JSONObject part_data = jArray.getJSONObject(i);
                            String name = part_data.getString("name");
                            String url = part_data.getString("url");
                            String id = part_data.getString("id");
                            //MODIFICAR DATO SIMPLE PARA ENVIAR PERFIL A OTRA VENTANA
                            //Toast.makeText(MenuPpal.this, "id:"+id, Toast.LENGTH_LONG).show();
                            dato.setName(name);
                            dato.setUrl(url);
                            dato.setId(id);
                            dato.setCorreo(username);
                            dato.setPerfil(perfil);
                            //Toast.makeText(MenuPpal.this, "dato: "+dato, Toast.LENGTH_LONG).show();
                            info.add(dato);
                            //Toast.makeText(MenuPpal.this, "info: "+info, Toast.LENGTH_LONG).show();
                        }
                        Gson gson = new Gson();
                        //Toast.makeText(MenuPpal.this, "infoFinal: "+info, Toast.LENGTH_LONG).show();
                        String datosJson = gson.toJson(info);
                        //Toast.makeText(MenuPpal.this, "gson: "+datosJson, Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(MenuPpal.this, Busqueda.class);
                        intent.putExtra("Datos",datosJson);
                        intent.putExtra("nombre", name);
                        intent.putExtra("username", username);
                        intent.putExtra("perfil", perfil);
                        MenuPpal.this.startActivity(intent);
                        finish();

                    }else {
                        // Error in login. Get the error message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_LONG).show();
                    }

                    // Setup and Handover data to recyclerview
                }catch (JSONException e) {
                    Toast.makeText(MenuPpal.this, e.toString(), Toast.LENGTH_LONG).show();
                }
            }
    }

    private class AsyncFiltr extends AsyncTask<String, String, String>{
        ProgressDialog pdLoading = new ProgressDialog(MenuPpal.this);
        HttpURLConnection con;
        URL url = null;
        Partituras partituras = new Partituras();

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
                url = new URL(URL_FOR_PART);

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
            pdLoading.dismiss();

            try{
                //Toast.makeText(MenuPpal.this, "sb:"+sb, Toast.LENGTH_LONG).show();
                JSONObject jObj = new JSONObject(sb);
                boolean error = jObj.getBoolean("error");
                //Toast.makeText(MenuPpal.this, "jObje000"+jObj, Toast.LENGTH_LONG).show();

                if (!error) {
                    JSONArray jArray = jObj.getJSONArray("partitura");
                    //Toast.makeText(MenuPpal.this, "jArray"+jArray, Toast.LENGTH_LONG).show();
                    ArrayList<Datos_simple> info= new ArrayList<Datos_simple>();
                    // Extract data from json and store into ArrayList as class objects
                    for (int i = 0; i < jArray.length(); i++) {
                        Datos_simple dato = new Datos_simple();
                        JSONObject part_data = jArray.getJSONObject(i);
                        String name = part_data.getString("name");
                        String url = part_data.getString("url");
                        String id = part_data.getString("id");
                        //Toast.makeText(MenuPpal.this, "id:"+id, Toast.LENGTH_LONG).show();
                        dato.setName(name);
                        dato.setUrl(url);
                        dato.setId(id);
                        dato.setCorreo(username);
                        //Toast.makeText(MenuPpal.this, "dato: "+dato, Toast.LENGTH_LONG).show();
                        info.add(dato);
                        //Toast.makeText(MenuPpal.this, "info: "+info, Toast.LENGTH_LONG).show();
                    }
                    Gson gson = new Gson();
                    //Toast.makeText(MenuPpal.this, "infoFinal: "+info, Toast.LENGTH_LONG).show();
                    String datosJson = gson.toJson(info);
                    //Toast.makeText(MenuPpal.this, "gson: "+datosJson, Toast.LENGTH_LONG).show();
                    Bundle bundle = new Bundle();
                    bundle.putString("Datos",datosJson);
                    partituras.setArguments(bundle);
                    FragmentManager fm = getSupportFragmentManager();
                    fm.beginTransaction().replace(R.id.content_frame, partituras).commit();
                    //finish();

                }else {
                    // Error in login. Get the error message
                    String errorMsg = jObj.getString("error_msg");
                    Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_LONG).show();
                }

                // Setup and Handover data to recyclerview
            }catch (JSONException e) {
                Toast.makeText(MenuPpal.this, e.toString(), Toast.LENGTH_LONG).show();
            }
        }
    }

    private class AsyncFiltro extends AsyncTask<String, String, String>{
        ProgressDialog pdLoading = new ProgressDialog(MenuPpal.this);
        HttpURLConnection con;
        URL url = null;
        Alumnos almno = new Alumnos();
        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            pdLoading.setMessage("\tLoading...");
            pdLoading.setCancelable(false);
            pdLoading.show();
        }
        @Override
        protected String doInBackground(String... params){
            try {
                url = new URL(URL_FOR_ALMNOS);

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
        protected void onPostExecute(String sb){
            pdLoading.dismiss();

            try{
                //Toast.makeText(MenuPpal.this, "sb:"+sb, Toast.LENGTH_LONG).show();
                JSONObject jObj = new JSONObject(sb);
                boolean error = jObj.getBoolean("error");

                if (!error) {
                    JSONArray jArray = jObj.getJSONArray("user");
                    //Toast.makeText(MenuPpal.this, "jArray"+jArray, Toast.LENGTH_LONG).show();
                    ArrayList<Datos> info= new ArrayList<Datos>();
                    // Extract data from json and store into ArrayList as class objects
                    for (int i = 0; i < jArray.length(); i++) {
                        Datos dato = new Datos();
                        JSONObject part_data = jArray.getJSONObject(i);
                        String nameAl = part_data.getString("name");
                        String userAl = part_data.getString("username");
                        int imageAl = R.drawable.harry;
                        //Toast.makeText(MenuPpal.this, "id:"+id, Toast.LENGTH_LONG).show();
                        dato.setName(nameAl);
                        dato.setUsername(userAl);
                        dato.setImage(imageAl);
                        //Toast.makeText(MenuPpal.this, "dato: "+dato, Toast.LENGTH_LONG).show();
                        info.add(dato);
                        //Toast.makeText(MenuPpal.this, "info: "+info, Toast.LENGTH_LONG).show();
                    }
                    Gson gson = new Gson();
                    //Toast.makeText(MenuPpal.this, "infoFinal: "+info, Toast.LENGTH_LONG).show();
                    String datosJson = gson.toJson(info);
                    //Toast.makeText(MenuPpal.this, "gson: "+datosJson, Toast.LENGTH_LONG).show();
                    Bundle bundle = new Bundle();
                    bundle.putString("Datos",datosJson);
                    almno.setArguments(bundle);
                    FragmentManager fm = getSupportFragmentManager();
                    fm.beginTransaction().replace(R.id.content_frame, almno).commit();
                }else {
                    // Error in login. Get the error message
                    String errorMsg = jObj.getString("error_msg");
                    Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_LONG).show();
                }

                // Setup and Handover data to recyclerview
            }catch (JSONException e) {
                Toast.makeText(MenuPpal.this, e.toString(), Toast.LENGTH_LONG).show();
            }
        }
    }

    public void piano (View view) {
        URL_FOR_FILTRO = "http://musictesis.esy.es/getAl.php";
        EditText et = (EditText)findViewById(R.id.et);
        String filtro = "Piano";
        new AsyncFilt().execute(filtro);
    }
    public void guitarra (View view) {
        URL_FOR_FILTRO = "http://musictesis.esy.es/getAl.php";
        EditText et = (EditText)findViewById(R.id.et);
        String filtro = "Guitarra";
        new AsyncFilt().execute(filtro);
    }
    public void violin (View view) {
        URL_FOR_FILTRO = "http://musictesis.esy.es/getAl.php";
        EditText et = (EditText)findViewById(R.id.et);
        String filtro = "Violin";
        new AsyncFilt().execute(filtro);
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
