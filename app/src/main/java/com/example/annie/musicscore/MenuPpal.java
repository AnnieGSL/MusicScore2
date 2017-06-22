package com.example.annie.musicscore;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MenuPpal extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{
    private EditText et;
    private TextView titulo, notas, sol, silencio, acorde, penta, otro;
    private String name, username, perfil;
    private Button buscar;
    private static final String TAG = "Busqueda";
    private static final String URL_FOR_FILTRO = "http://musictesis.esy.es/getPdfs.php?filtro=";

    private ProgressDialog pDialog;

    public static final int CONNECTION_TIMEOUT = 10000;
    public static final int READ_TIMEOUT = 15000;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_ppal);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        et = (EditText)findViewById(R.id.et);
        titulo = (TextView)findViewById(R.id.titulo);
        notas = (TextView)findViewById(R.id.notas);
        sol = (TextView)findViewById(R.id.sol);
        silencio = (TextView)findViewById(R.id.silencio);
        acorde = (TextView)findViewById(R.id.acorde);
        penta = (TextView)findViewById(R.id.penta);
        otro = (TextView)findViewById(R.id.otro);
        buscar = (Button)findViewById(R.id.button);


        Bundle bundle = getIntent().getExtras();
        name = bundle.getString("nombre");
        username = bundle.getString("correo");
        perfil = bundle.getString("perfil"); //INVESTIGAR RESTRICCION
/*
        buscar.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                new AsyncFilt().execute();
            }
        });
*/

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

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
        fm.beginTransaction().replace(R.id.content_frame, new main()).commit();
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
        getMenuInflater().inflate(R.menu.menu_ppal, menu);
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

        return super.onOptionsItemSelected(item);
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
            Partituras partitura = new Partituras();
            fm.beginTransaction().replace(R.id.content_frame, partitura).commit();
            //Toast.makeText(this, "Partitura Almacenada", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_mensajes) {
            Toast.makeText(this, "Lista de Mensaje", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_alumnos) {
            Alumnos almno = new Alumnos();
            fm.beginTransaction().replace(R.id.content_frame, almno).commit();
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
        new AsyncFilt().execute();
    }


    private class AsyncFilt extends AsyncTask<String, String, String>{
            ProgressDialog pdLoading = new ProgressDialog(MenuPpal.this);
            HttpURLConnection con;
            URL url = null;
            //final String filtro = et.getText().toString();
            String filtro = "Hello";


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
                    url = new URL(URL_FOR_FILTRO+filtro);
                    Toast.makeText(MenuPpal.this, "filtro:"+url, Toast.LENGTH_LONG).show();

                }catch(MalformedURLException e){
                    e.printStackTrace();
                    return e.toString();
                }
                try{
                    con = (HttpURLConnection) url.openConnection();
                    con.setReadTimeout(READ_TIMEOUT);
                    con.setConnectTimeout(CONNECTION_TIMEOUT);
                    con.setRequestMethod("GET");
                    con.setDoOutput(true);
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
                Toast.makeText(MenuPpal.this, "sb"+sb, Toast.LENGTH_LONG).show();

                try{
                    //Toast.makeText(MenuPpal.this, "sb:"+sb, Toast.LENGTH_LONG).show();
                    JSONObject jObj = new JSONObject(sb);
                    boolean error = jObj.getBoolean("error");

                    if (!error) {
                        JSONArray jArray = jObj.getJSONArray("partitura");
                        //Toast.makeText(MenuPpal.this, "jArray"+jArray, Toast.LENGTH_LONG).show();

                        // Extract data from json and store into ArrayList as class objects
                        for (int i = 0; i < jArray.length(); i++) {
                            JSONObject part_data = jArray.getJSONObject(i);

                            String name = part_data.getString("name");
                            String url = part_data.getString("url");

                            Intent intent = new Intent(MenuPpal.this, Busqueda.class);
                            intent.putExtra("name", name);
                            intent.putExtra("url", url);
                            MenuPpal.this.startActivity(intent);
                            finish();
                        }
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

    /*public void piano (View view) {
        Intent i = new Intent(this, Busqueda.class);
        startActivity(i);
    }
    public void guitarra (View view) {
        Intent i = new Intent(this, Busqueda.class);
        startActivity(i);
    }
    public void violin (View view) {
        Intent i = new Intent(this, Busqueda.class);
        startActivity(i);
    }*/

}
