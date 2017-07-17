package com.example.annie.musicscore;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Annie on 16-07-2017.
 */

public class obsAdapter extends RecyclerView.Adapter<obsAdapter.MyViewHolder> {
    private Context context;
    private ArrayList<Datos_obs> itemList = new ArrayList<Datos_obs>();
    LayoutInflater inflater;

    public obsAdapter(Context context, ArrayList<Datos_obs> itemList){
        this.context = context;
        this.itemList = itemList;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public obsAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int position) {
        View view = inflater.inflate(R.layout.busq_item, parent,false);
        obsAdapter.MyViewHolder holder = new obsAdapter.MyViewHolder(view, context, itemList);
        return holder;
    }

    @Override
    public void onBindViewHolder(obsAdapter.MyViewHolder myViewHolder, int position) {
        final Datos_obs pdfDoc = itemList.get(position);
        myViewHolder.textView.setText(pdfDoc.getFecha()+", "+pdfDoc.getHora());
        myViewHolder.setItemClickListener(new obsAdapter.ItemClickListener(){
            public void onItemClick(int pos){
                openOBSView(pdfDoc.getFecha(),pdfDoc.getHora(),pdfDoc.getDesc(), pdfDoc.getUsername());
            }
        });
    }

    private void openOBSView(String date, String time, String desc, String user) {
        String origen = "Lista";
        Intent intent = new Intent(this.context, vistaObservacion.class);
        intent.putExtra("DATE",date);
        intent.putExtra("TIME", time);
        intent.putExtra("DESC",desc);
        intent.putExtra("USER", user);
        intent.putExtra("OR", origen);
        this.context.startActivity(intent);
    }

    @Override
    public int getItemCount() { return itemList.size(); }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView textView;
        obsAdapter.ItemClickListener itemClickListener;
        public MyViewHolder(View itemView, Context context, ArrayList<Datos_obs> itemList){
            super(itemView);
            itemView.setOnClickListener(this);
            textView =(TextView)itemView.findViewById(R.id.tv_row);
        }

        public void setItemClickListener(obsAdapter.ItemClickListener itemClickListener){
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
}
