package com.example.annie.musicscore;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Annie on 28-08-2017.
 */

public class msgAdapter extends RecyclerView.Adapter<msgAdapter.MyViewHolder> {
    private Context context;
    private ArrayList<msg> itemList = new ArrayList<msg>();
    LayoutInflater inflater;
    ProgressDialog pDialog;
    private static final String URL_FOR_MENSAJE = "http://musictesis.esy.es/leido.php";
    private static final String TAG = "MENSAJE";


    public msgAdapter(Context context, ArrayList<msg> itemList){
        this.context = context;
        this.itemList = itemList;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public msgAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int position) {
        View view = inflater.inflate(R.layout.msg_item, parent,false);
        msgAdapter.MyViewHolder holder = new msgAdapter.MyViewHolder(view, context, itemList);
        return holder;
    }

    @Override
    public void onBindViewHolder(msgAdapter.MyViewHolder myViewHolder, int position) {
        final msg pdfDoc = itemList.get(position);
        final int fl = pdfDoc.getFlag();
        if (fl == 0){
            myViewHolder.textView.setText(Html.fromHtml("<b>"+"Desde: " + pdfDoc.getEmisor()+"</b>"));
            myViewHolder.textView2.setText(pdfDoc.getFecha());
            myViewHolder.textView3.setText(pdfDoc.getHora());
            myViewHolder.setItemClickListener(new msgAdapter.ItemClickListener() {
                public void onItemClick(int pos) {
                    openMSG(pdfDoc.getMensaje(), pdfDoc.getReceptor(), pdfDoc.getEmisor(), pdfDoc.getId(), pdfDoc.getNameReceptor(), pdfDoc.getPerf());
                }
            });
        }else {
            myViewHolder.textView.setText("Desde: " + pdfDoc.getEmisor());
            myViewHolder.textView2.setText(pdfDoc.getFecha());
            myViewHolder.textView3.setText(pdfDoc.getHora());
            myViewHolder.setItemClickListener(new msgAdapter.ItemClickListener() {
                public void onItemClick(int pos) {
                    openMSG(pdfDoc.getMensaje(), pdfDoc.getReceptor(), pdfDoc.getEmisor(), pdfDoc.getId(), pdfDoc.getNameReceptor(), pdfDoc.getPerf());
                }
            });
        }
    }

    private void marcarLeido(final String id) {
        final int flag = 1;
        String cancel_req_tag = "Mensajes";
        //pDialog.setMessage("Cargando...");
        //showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST, URL_FOR_MENSAJE, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Busca Response: " + response.toString());
               // hideDialog();
                //Toast.makeText(context, "id: "+ id, Toast.LENGTH_LONG).show();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    //Toast.makeText(getApplicationContext(), "jObj: "+jObj, Toast.LENGTH_LONG).show();
                    // Check for error node in json
                    if (!error) {
                        //String l = "Leido";
                        //Toast.makeText(context, l, Toast.LENGTH_LONG).show();
                        //finish();

                    } else {
                        // Error in login. Get the error message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(context, errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    Toast.makeText(context, "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Mensaje Error: " + error.getMessage());
                Toast.makeText(context, error.getMessage(), Toast.LENGTH_LONG).show();
                //hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("id", id);
                params.put("flag", String.valueOf(flag));
                return params;
            }
        };
        // Adding request to request queue
        AppSingleton.getInstance(context).addToRequestQueue(strReq, cancel_req_tag);

    }

    private void openMSG(String mensaje, String receptor, String emisor, String id, String name, String p) {
        marcarLeido(id);
        String fuente = "lista";
        Intent intent = new Intent(this.context, VistaMensaje.class);
        intent.putExtra("mensaje",mensaje);
        intent.putExtra("username",receptor);
        intent.putExtra("emisor", emisor);
        intent.putExtra("fuente", fuente);
        intent.putExtra("name",name);
        intent.putExtra("perfil", p);
        this.context.startActivity(intent);
        ((listMessage)context).finish();

    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView textView, textView2, textView3;
        msgAdapter.ItemClickListener itemClickListener;
        public MyViewHolder(View itemView, Context context, ArrayList<msg> itemList){
            super(itemView);
            itemView.setOnClickListener(this);
            textView =(TextView)itemView.findViewById(R.id.tv_row);
            textView2 =(TextView)itemView.findViewById(R.id.tv_row2);
            textView3 =(TextView)itemView.findViewById(R.id.tv_row3);
        }

        public void setItemClickListener(msgAdapter.ItemClickListener itemClickListener){
            this.itemClickListener = itemClickListener;
        }

        @Override
        public void onClick(View v) {
            itemClickListener.onItemClick(getLayoutPosition());
        }
    }

    interface ItemClickListener{
        void onItemClick(int pos);
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
