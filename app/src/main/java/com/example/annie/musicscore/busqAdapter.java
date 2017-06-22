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
 * Created by Annie on 30-11-2016.
 */

public class busqAdapter extends RecyclerView.Adapter<busqAdapter.MyViewHolder> {
    private Context context;
    private ArrayList<Datos_url> itemList = new ArrayList<Datos_url>();
    LayoutInflater inflater;

    public busqAdapter(Context context, ArrayList<Datos_url> itemList){
        this.context = context;
        this.itemList = itemList;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int position) {
        View view = inflater.inflate(R.layout.busq_item, parent,false);
        MyViewHolder holder = new MyViewHolder(view, context, itemList);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder myViewHolder, int position) {
        final Datos_url pdfDoc = itemList.get(position);
        myViewHolder.textView.setText(pdfDoc.getName());
        myViewHolder.setItemClickListener(new ItemClickListener(){
            public void onItemClick(int pos){
                openPDFView(pdfDoc.getUrl());
            }
        });
    }
    //llama al activity de vista partitura

    private void openPDFView(String path) {
        Intent intent = new Intent(this.context, VistaPartitura.class);
        intent.putExtra("PATH",path);
        this.context.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView textView;
        ItemClickListener itemClickListener;
        public MyViewHolder(View itemView, Context context, ArrayList<Datos_url> itemList){
            super(itemView);
            itemView.setOnClickListener(this);
            textView =(TextView)itemView.findViewById(R.id.tv_row);
        }

        public void setItemClickListener(ItemClickListener itemClickListener){
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
