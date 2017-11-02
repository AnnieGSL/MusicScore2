package com.example.annie.musicscore;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

/**
 * Created by Annie on 01-12-2016.
 */

public class almAdapter extends RecyclerView.Adapter<almAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<Datos> itemList;
    LayoutInflater inflater;

    public almAdapter(Context context, ArrayList<Datos> itemList){
        this.context = context;
        this.itemList = itemList;
        inflater = LayoutInflater.from(context);
    }


    @Override
    public almAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int position) {
        View view = inflater.inflate(R.layout.almns_item, parent,false);
        almAdapter.MyViewHolder holder = new almAdapter.MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final almAdapter.MyViewHolder myViewHolder, final int position) {
        final Datos pdfDoc = itemList.get(position);
        myViewHolder.textView.setText(pdfDoc.getName());
        myViewHolder.imageView.setImageResource(pdfDoc.getImage());
        myViewHolder.imageButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view)
            {
                showPopUpMenu(myViewHolder.imageButton,position, pdfDoc.getUsername(), pdfDoc.getEmisor());
            }
        });
    }

    private void showPopUpMenu(View view, final int position, final String username, final String emisor) {
        //USERNAME ALUMNO
        PopupMenu popup = new PopupMenu(view.getContext(),view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.popup_alms,popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener(){
            public boolean onMenuItemClick(MenuItem item){
                switch(item.getItemId()) {
                    case R.id.obs:
                        String filtro = username;
                        new AsyncFilt().execute(filtro);
                        break;
                    case R.id.nuv:
                        creaNueva(username);
                        break;
                    case R.id.prg:
                        String filtr = username;
                        new AsyncFiltr().execute(filtr);
                        break;
                    case R.id.msj:
                        Intent i = new Intent(context, message.class);
                        i.putExtra("username", username);
                        i.putExtra("emisor", emisor);
                        context.startActivity(i);
                        //Toast.makeText(context,"Mensaje "+position,Toast.LENGTH_SHORT).show();
                        return true;
                }
                return false;
            }
        });
        popup.show();
    }

    private void creaNueva(String username) {
        Intent i = new Intent(context, vistaObservacion.class);
        i.putExtra("USER", username);
        i.putExtra("OR", "Menu");
        context.startActivity(i);
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        TextView textView;
        ImageView imageView;
        ImageButton imageButton;
        public MyViewHolder(View itemView){
            super(itemView);
            textView =(TextView)itemView.findViewById(R.id.txv_alm);
            imageView = (ImageView)itemView.findViewById(R.id.img_alm);
            imageButton = (ImageButton)itemView.findViewById((R.id.bt_alm));
        }
    }

    private class AsyncFilt extends AsyncTask<String, String, String> {
        ProgressDialog pdLoading = new ProgressDialog(context);
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
                url = new URL("http://musictesis.esy.es/getObs.php");

            }catch(MalformedURLException e){
                e.printStackTrace();
                return e.toString();
            }
            try{
                con = (HttpURLConnection) url.openConnection();
                con.setReadTimeout(15000);
                con.setConnectTimeout(10000);
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
                //Toast.makeText(context, "sb:"+sb, Toast.LENGTH_LONG).show();
                JSONObject jObj = new JSONObject(sb);
                boolean error = jObj.getBoolean("error");

                if (!error) {
                    JSONArray jArray = jObj.getJSONArray("user");
                    //Toast.makeText(context, "jArray"+jArray, Toast.LENGTH_LONG).show();
                    ArrayList<Datos_obs> info= new ArrayList<Datos_obs>();
                    // Extract data from json and store into ArrayList as class objects
                    for (int i = 0; i < jArray.length(); i++) {
                        Datos_obs dato = new Datos_obs();
                        JSONObject part_data = jArray.getJSONObject(i);
                        String fecha = part_data.getString("fecha");
                        String hora = part_data.getString("hora");
                        String desc = part_data.getString("descripcion");
                        String username = part_data.getString("username");
                        //MODIFICAR DATO SIMPLE PARA ENVIAR PERFIL A OTRA VENTANA
                        //Toast.makeText(MenuPpal.this, "id:"+id, Toast.LENGTH_LONG).show();
                        dato.setFecha(fecha);
                        dato.setHora(hora);
                        dato.setDesc(desc);
                        dato.setUsername(username);
                        //Toast.makeText(MenuPpal.this, "dato: "+dato, Toast.LENGTH_LONG).show();
                        info.add(dato);
                        //Toast.makeText(MenuPpal.this, "info: "+info, Toast.LENGTH_LONG).show();
                    }
                    Gson gson = new Gson();
                    //Toast.makeText(MenuPpal.this, "infoFinal: "+info, Toast.LENGTH_LONG).show();
                    String datosJson = gson.toJson(info);
                    //Toast.makeText(MenuPpal.this, "gson: "+datosJson, Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(context, creaObservacion.class);
                    intent.putExtra("Datos",datosJson);
                    context.startActivity(intent);
                }else {
                    // Error in login. Get the error message
                    String errorMsg = jObj.getString("error_msg");
                    Toast.makeText(context, errorMsg + ", crear nueva.", Toast.LENGTH_LONG).show();
                }

                // Setup and Handover data to recyclerview
            }catch (JSONException e) {
                Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show();
            }
        }
    }

    private class AsyncFiltr extends AsyncTask<String, String, String>{
        ProgressDialog pdLoading = new ProgressDialog(context);
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
                url = new URL("http://musictesis.esy.es/getPrg.php");

            }catch(MalformedURLException e){
                e.printStackTrace();
                return e.toString();
            }
            try{
                con = (HttpURLConnection) url.openConnection();
                con.setReadTimeout(15000);
                con.setConnectTimeout(10000);
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
                //Toast.makeText(context, "sb:"+sb, Toast.LENGTH_LONG).show();
                JSONObject jObj = new JSONObject(sb);
                boolean error = jObj.getBoolean("error");

                if (!error) {
                    JSONArray jArray = jObj.getJSONArray("user");
                    //Toast.makeText(MenuPpal.this, "jArray"+jArray, Toast.LENGTH_LONG).show();
                    ArrayList<Datos_prg> info= new ArrayList<Datos_prg>();
                    // Extract data from json and store into ArrayList as class objects
                    for (int i = jArray.length()-1; i >=0 ; i--) {
                        Datos_prg dato = new Datos_prg();
                        JSONObject part_data = jArray.getJSONObject(i);
                        String fecha = part_data.getString("fecha");
                        int min = part_data.getInt("tiempo");
                        int pag = part_data.getInt("pagina");
                        int id = part_data.getInt("idScore");
                        //Toast.makeText(MenuPpal.this, "id:"+id, Toast.LENGTH_LONG).show();
                        dato.setFecha(fecha);
                        dato.setMin(min);
                        dato.setPag(pag);
                        dato.setId(id);
                        //Toast.makeText(MenuPpal.this, "dato: "+dato, Toast.LENGTH_LONG).show();
                        info.add(dato);
                        //Toast.makeText(MenuPpal.this, "info: "+info, Toast.LENGTH_LONG).show();
                    }
                    Gson gson = new Gson();
                    //Toast.makeText(MenuPpal.this, "infoFinal: "+info, Toast.LENGTH_LONG).show();
                    String datosJson = gson.toJson(info);
                    //Toast.makeText(context, "gson: "+datosJson, Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(context, graphPrg.class);
                    intent.putExtra("Datos",datosJson);
                    context.startActivity(intent);
                    //finish();
                }else {
                    // Error in login. Get the error message
                    String errorMsg = jObj.getString("error_msg");
                    Toast.makeText(context, errorMsg, Toast.LENGTH_LONG).show();
                }

                // Setup and Handover data to recyclerview
            }catch (JSONException e) {
                Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show();
            }
        }
    }

}