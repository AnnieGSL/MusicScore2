package com.example.annie.musicscore;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;

import org.apache.commons.codec.binary.Base64;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
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
    private String name, username, perfil, busq, a, b, c, d, e, path, str, pdf;
    private EditText tit, comp, inst;

    public static final String  URL_FOR_UPLOAD = "http://musictesis.esy.es/upload.php";
    private int PICK_PDF_REQUEST = 1;
    private static final int STORAGE_PERMISSION_CODE = 1;
    private Uri filePath;

    private static final String TAG = "PRINCIPAL";
    private static String URL_FOR_FILTRO;
    private static final String URL_FOR_PART = "http://musictesis.esy.es/getPdfs(json).php";
    private static final String URL_FOR_ALMNOS ="http://musictesis.esy.es/getAlmn.php";
    private static final String URL_FOR_BUSCAR = "http://musictesis.esy.es/getAlm.php";
    private static final String URL_FOR_MENSAJE = "http://musictesis.esy.es/getMsg.php";


    ProgressDialog pDialog;

    public static final int CONNECTION_TIMEOUT = 10000;
    public static final int READ_TIMEOUT = 15000;

    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;

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


        requestStoragePermission();

        sharedpreferences = getSharedPreferences("ArchivoLogin",MenuPpal.MODE_PRIVATE);

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
            String filtro = username;
            buscarMsg(filtro);
            /*
            Intent i = new Intent(MenuPpal.this, message.class);
            i.putExtra("name", name);
            MenuPpal.this.startActivity(i);
            finish();
            */
            //Toast.makeText(this, "Lista de Mensaje", Toast.LENGTH_SHORT).show();
            return true;
        }
        if(id == R.id.action_add) {
            //Cargar en un recyclerView todos los alumnos con filtro de perfil.
            buscarAlm();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void buscarMsg(final String filtro) {
        String cancel_req_tag = "Mensajes";
        pDialog.setMessage("Cargando...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST, URL_FOR_MENSAJE, new Response.Listener<String>() {

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
                            String rec = part_data.getString("receptor");//yo
                            int fl = part_data.getInt("flag");
                            String perfEm = perfil;
                            String nRec = name;
                            //Toast.makeText(MenuPpal.this, "id:"+id, Toast.LENGTH_LONG).show();
                            dato.setId(id);
                            dato.setMensaje(msg);
                            dato.setFecha(fech);
                            dato.setHora(hr);
                            dato.setEmisor(em);
                            dato.setReceptor(rec);
                            dato.setFlag(fl);
                            dato.setPerf(perfEm);
                            dato.setNameReceptor(nRec);
                            //Toast.makeText(MenuPpal.this, "dato: "+dato, Toast.LENGTH_LONG).show();
                            info.add(dato);
                            //Toast.makeText(MenuPpal.this, "info: "+info, Toast.LENGTH_LONG).show();
                        }
                        Gson gson = new Gson();
                        //Toast.makeText(MenuPpal.this, "infoFinal: "+info, Toast.LENGTH_LONG).show();
                        String datosJson = gson.toJson(info);
                        //Toast.makeText(MenuPpal.this, "gson: "+datosJson, Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(MenuPpal.this, listMessage.class);
                        intent.putExtra("Datos",datosJson);
                        intent.putExtra("nombre", name);
                        intent.putExtra("username", username);
                        intent.putExtra("perfil", perfil);
                        MenuPpal.this.startActivity(intent);
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
                params.put("filtro", filtro);
                return params;
            }
        };
        // Adding request to request queue
        AppSingleton.getInstance(getApplicationContext()).addToRequestQueue(strReq, cancel_req_tag);
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
        }else if (id == R.id.nav_cuenta) {
            Perfil perfil = new Perfil();
            fm.beginTransaction().replace(R.id.content_frame, perfil).commit();
            //Toast.makeText(this, "NO hablitado por el momento", Toast.LENGTH_SHORT).show();
        }else if (id == R.id.nav_partituras){
            String filtro = username;
            new AsyncFiltr().execute(filtro);
        }else if (id == R.id.nav_mensajes) {
            //Toast.makeText(this, "Lista de Mensaje", Toast.LENGTH_SHORT).show();
            String filtro = username;
            buscarMsg(filtro);
        }else if (id == R.id.nav_alumnos) {
            String filtro = username;
            new AsyncFiltro().execute(filtro);
            //Toast.makeText(this, "Lista de Alumnos", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_sesion) {
            editor = sharedpreferences.edit();
            editor.clear();
            editor.commit();
            Intent i = new Intent(MenuPpal.this, LoginActivity.class);
            MenuPpal.this.startActivity(i);
            finish();
        } else if (id == R.id.nav_info) {
            AcercaDe acerca = new AcercaDe();
            fm.beginTransaction().replace(R.id.content_frame, acerca).commit();
        }else if(id == R.id.nav_upload){
            Upload upload = new Upload();
            fm.beginTransaction().replace(R.id.content_frame, upload).commit();
            //Toast.makeText(this, "NO hablitado por el momento", Toast.LENGTH_SHORT).show();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void buscar (View view) {
        URL_FOR_FILTRO = "http://musictesis.esy.es/getPdfs.php";
        EditText et = (EditText)findViewById(R.id.et);
        String text = et.getText().toString();
        String filtro = text;
        //Toast.makeText(MenuPpal.this, "Filtro: "+filtro, Toast.LENGTH_SHORT).show();
        new AsyncFilt().execute(filtro);
    }

    public void cargar (View view){
        showFileChooser();
    }
    private void showFileChooser() {
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath());
        //Toast.makeText(this, "File:"+ file, Toast.LENGTH_SHORT).show();//storage/emulated/0
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse(String.valueOf(file)),"application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Pdf"), PICK_PDF_REQUEST);
    }

    public void subir (View view) throws IOException {
        //Toast.makeText(this, "titulo"+tit.getText().toString(), Toast.LENGTH_SHORT).show();
        uploadMultipart();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_PDF_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            //Log.d("pdf", filePath);
            Log.d("pdf", data.getDataString());
            Log.d("pdf", data.getData().getEncodedPath());
            Button cargaButon = (Button)findViewById(R.id.cargarpdf);
            //Toast.makeText(this, "Path:"+ filePath, Toast.LENGTH_SHORT).show();//com.android.exterbakstorage.documents/document/primay%3ADownload%2Fcupon_casilla.pdf
            cargaButon.setText("PDF seleccionado");
        }
    }

    public void uploadMultipart() throws IOException {
        tit = (EditText)findViewById(R.id.etTit);
        comp = (EditText)findViewById(R.id.etComp);
        inst = (EditText)findViewById(R.id.etIns);
        a =tit.getText().toString().trim();
        final String titul = a;
        b = comp.getText().toString().trim();
        final String compositor = b;
        c = inst.getText().toString().trim();
        final String instrumento =  c;
        final String nm = titul+"-"+compositor;
        if(filePath!=null) {
            path = FilePath.getPath(this, filePath);
            pdf = pdftostring(path);
            //Toast.makeText(this,"pdfSTR:"+pdf , Toast.LENGTH_SHORT).show();
            Log.d("path", path);
        }else {
            Toast.makeText(this,"Cargue el archivo" , Toast.LENGTH_SHORT).show();
        }
        if (path == null) {
            Toast.makeText(this, "o por favor, mueve tu archivo al almacenamiento interno y vuelve a intentar", Toast.LENGTH_LONG).show();
        }else {
            try {
                String cancel_req_tag = "Upload";
                pDialog.setMessage("Cargando...");
                showDialog();
                StringRequest strReq = new StringRequest(Request.Method.POST, URL_FOR_UPLOAD, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "upl Response: " + response.toString());
                        hideDialog();
                        //Toast.makeText(getApplicationContext(), "response: "+response, Toast.LENGTH_LONG).show();
                        try {
                            JSONObject jObj = new JSONObject(response);
                            boolean error = jObj.getBoolean("error");
                            if(!error){
                                String msg = jObj.getString("msg");
                                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                                FragmentManager fm = getSupportFragmentManager();
                                fm.beginTransaction().replace(R.id.content_frame, new main()).commit();
                            }else {
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
                        Log.e(TAG, "Update Error: " + error.getMessage());
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                        hideDialog();
                    }
                }) {

                    @Override
                    protected Map<String, String> getParams() {
                        // Posting parameters to login url
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("titulo", titul);
                        params.put("compositor", compositor);
                        params.put("instrumento", instrumento);
                        params.put("pdf", pdf);
                        params.put("name", nm);
                        return params;
                    }
                };
                // Adding request to request queue
                AppSingleton.getInstance(getApplicationContext()).addToRequestQueue(strReq, cancel_req_tag);
            } catch (Exception exc) {
                exc.printStackTrace();
                Toast.makeText(this, exc.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }


    private String pdftostring(String npath) throws IOException {
        File file = new File(String.valueOf(npath));
        if (file.exists()) {
            byte[] bytes = loadFile(file);
            /*
            int size = (int) file.length();
            byte[] bytes = new byte[size];
            try {
                BufferedInputStream buf = new BufferedInputStream(new FileInputStream(file));
                buf.read(bytes, 0, bytes.length);
                buf.close();
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            byte[] encoded = Base64.encodeBase64(bytes);
            str =  new String(encoded);
            Log.d("str","data:application/pdf;base64,"+str);
            */
            byte[] encoded = Base64.encodeBase64(bytes);
            str =  new String(encoded);
            Log.d("str","data:application/pdf;base64,"+str);
        } else {
            Log.d("pdf","not found");
        }
        return str;
    }

    private static byte[] loadFile(File file) throws IOException {
        InputStream is = new FileInputStream(file);

        long length = file.length();
        if (length > Integer.MAX_VALUE) {
            // File is too large
        }
        byte[] bytes = new byte[(int)length];

        int offset = 0;
        int numRead = 0;
        while (offset < bytes.length
                && (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
            offset += numRead;
        }

        if (offset < bytes.length) {
            throw new IOException("Could not completely read file "+file.getName());
        }

        is.close();
        return bytes;
    }

    private void requestStoragePermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(MenuPpal.this, android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
            Toast.makeText(MenuPpal.this,"READ_EXTERNAL_STORAGE permission Access Dialog", Toast.LENGTH_LONG).show();
            //And finally ask for the permission
        }else{
            ActivityCompat.requestPermissions(MenuPpal.this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        //Checking the request code of our request
        if (requestCode == STORAGE_PERMISSION_CODE) {

            //If permission is granted
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Displaying a toast
                //Toast.makeText(this, "Permission granted now you can read the storage", Toast.LENGTH_LONG).show();
            } else {
                //Displaying another toast if permission is not granted
                Toast.makeText(this, "NO tienes permisos para acceder a la memoria", Toast.LENGTH_LONG).show();
            }
        }
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
                            String url = part_data.getString("url")+".pdf";
                            String id = part_data.getString("id");
                            //MODIFICAR DATO SIMPLE PARA ENVIAR PERFIL A OTRA VENTANA
                            //Toast.makeText(MenuPpal.this, "id:"+id+"url"+url, Toast.LENGTH_LONG).show();
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
                        String url = part_data.getString("url")+".pdf";
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
                    //for (int i = jArray.length()-1; i >=0 ; i--) {
                    for (int i=0; i<jArray.length();i++){
                        Datos dato = new Datos();
                        JSONObject part_data = jArray.getJSONObject(i);
                        String nameAl = part_data.getString("name");
                        String userAl = part_data.getString("username");
                        int imageAl = R.drawable.ic_person_black_24dp;
                        String emisor = username;
                        //Toast.makeText(MenuPpal.this, "id:"+id, Toast.LENGTH_LONG).show();
                        dato.setName(nameAl);
                        dato.setUsername(userAl);
                        dato.setImage(imageAl);
                        dato.setEmisor(emisor);
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
        String filtro = "Piano";
        new AsyncFilt().execute(filtro);
    }
    public void guitarra (View view) {
        URL_FOR_FILTRO = "http://musictesis.esy.es/getAl.php";
        String filtro = "Guitarra";
        new AsyncFilt().execute(filtro);
    }
    public void violin (View view) {
        URL_FOR_FILTRO = "http://musictesis.esy.es/getAl.php";
        String filtro = "Violin";
        new AsyncFilt().execute(filtro);
    }

    public void nombrenew(View view){
        URL_FOR_FILTRO = "http://musictesis.esy.es/updNombre.php";
        EditText et = (EditText)findViewById(R.id.editText);
        String nn= et.getText().toString();
        String newname = nn;
        String fuente = "cierre";
        enviarDato(newname, fuente);;
    }

    public void edadnew(View view){
        URL_FOR_FILTRO = "http://musictesis.esy.es/updEdad.php";
        EditText et = (EditText)findViewById(R.id.editText2);
        String ne= et.getText().toString();
        String newage = ne;
        String fuente = "no";
        enviarDato(newage, fuente);
    }

    public void passnew(View view){
        URL_FOR_FILTRO = "http://musictesis.esy.es/updPass.php";
        EditText et = (EditText)findViewById(R.id.editText4);
        String np= et.getText().toString();
        String newpass = np;
        String fuente = "cierre";
        enviarDato(newpass, fuente);
    }

    public void finalizar(View view){
        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction().replace(R.id.content_frame, new main()).commit();
    }

    private void enviarDato(final String dato, final String fuente) {
        String cancel_req_tag = "Menu";
        pDialog.setMessage("Cargando...");
        showDialog();
        StringRequest strReq = new StringRequest(Request.Method.POST, URL_FOR_FILTRO, new Response.Listener<String>() {
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
                        String user = jObj.getString("username");
                        Toast.makeText(getApplicationContext(), "Información de: " + user +" modificada", Toast.LENGTH_SHORT).show();
                        if (fuente.equalsIgnoreCase("cierre")){
                            editor = sharedpreferences.edit();
                            editor.clear();
                            editor.commit();
                            Intent i = new Intent(MenuPpal.this, LoginActivity.class);
                            MenuPpal.this.startActivity(i);
                            finish();
                        }
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
                Log.e(TAG, "Update Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("new", dato);
                params.put("username", username);
                return params;
            }
        };
        // Adding request to request queue
        AppSingleton.getInstance(getApplicationContext()).addToRequestQueue(strReq, cancel_req_tag);
    }

    public void notas (View view){
        Intent i = new Intent(MenuPpal.this, notas.class);
        i.putExtra("nombre", name);
        i.putExtra("username", username);
        i.putExtra("perfil", perfil);
        MenuPpal.this.startActivity(i);
        finish();
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
