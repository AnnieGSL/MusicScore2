package com.example.annie.musicscore;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
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
 * Created by Annie on 14-07-2017.
 */

public class listAdapter extends RecyclerView.Adapter<listAdapter.MyViewHolder> {
    private Context context;
    private ArrayList<item> itemList = new ArrayList<item>();
    LayoutInflater inflater;

    private static final String URL_FOR_ADD = "http://musictesis.esy.es/addAlm.php";
    private static final String TAG = "ListaAlumnos";
    ProgressDialog pDialog;

    public listAdapter(Context context, ArrayList<item> itemList){
        this.context = context;
        this.itemList = itemList;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public listAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int position) {
        View view = inflater.inflate(R.layout.list_item, parent,false);
        listAdapter.MyViewHolder holder = new listAdapter.MyViewHolder(view, context, itemList);
        return holder;
    }

    @Override
    public void onBindViewHolder(listAdapter.MyViewHolder myViewHolder, int position) {
        final item pdfDoc = itemList.get(position);
        myViewHolder.textView.setText(pdfDoc.getName());
        myViewHolder.textView2.setText(pdfDoc.getUsername());
        myViewHolder.setItemClickListener(new listAdapter.ItemClickListener(){
            public void onItemClick(int pos){
                addAlm(pdfDoc.getProfe(), pdfDoc.getName(), pdfDoc.getUsername());
            }
        });
    }
    //llama al activity de vista partitura

    private void addAlm(final String profe, final String alumno, final String userAl) {
        // Tag used to cancel the request
        String cancel_req_tag = "register";
        //pDialog.setMessage("Adding you...");
        //showDialog();
        StringRequest strReq = new StringRequest(Request.Method.POST,
                URL_FOR_ADD, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Register Response: " + response.toString());
                //hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    if (!error) {
                        String user = jObj.getJSONObject("user").getString("name");
                        Toast.makeText(context, "Alumno agregado exitosamente" , Toast.LENGTH_SHORT).show();
                    } else {
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(context, errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Agregar Error: " + error.getMessage());
                Toast.makeText(context, error.getMessage(), Toast.LENGTH_LONG).show();
                //hideDialog();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("profesor", profe );
                params.put("alumno", userAl);
                params.put("name", alumno);
                return params;
            }
        };
        // Adding request to request queue
        AppSingleton.getInstance(context).addToRequestQueue(strReq, cancel_req_tag);
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView textView, textView2;
        listAdapter.ItemClickListener itemClickListener;
        public MyViewHolder(View itemView, Context context, ArrayList<item> itemList){
            super(itemView);
            itemView.setOnClickListener(this);
            textView =(TextView)itemView.findViewById(R.id.tv_row);
            textView2 = (TextView)itemView.findViewById(R.id.tv_row2);
        }

        public void setItemClickListener(listAdapter.ItemClickListener itemClickListener){
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
