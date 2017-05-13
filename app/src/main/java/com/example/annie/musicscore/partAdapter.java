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
 * Created by Annie on 13-01-2017.
 */

public class partAdapter extends RecyclerView.Adapter<partAdapter.MyViewHolder> {
    private Context context;
    private ArrayList<Datos_simple> itemList;
    LayoutInflater inflater;

    public partAdapter(Context context, ArrayList<Datos_simple> itemList){
        this.context = context;
        this.itemList = itemList;
        inflater = LayoutInflater.from(context);
    }


    @Override
    public partAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int position) {
        View view = inflater.inflate(R.layout.busq_item, parent,false);
        partAdapter.MyViewHolder holder = new partAdapter.MyViewHolder(view);
        return holder;
    }


    public void onBindViewHolder(final partAdapter.MyViewHolder myViewHolder, final int position) {
        final Datos_simple pdfDoc = itemList.get(position);
        myViewHolder.textView.setText(pdfDoc.getName());
        myViewHolder.setItemClickListener(new partAdapter.ItemClickListener(){
            public void onItemClick(int pos){
                openPDFView(pdfDoc.getPath());
            }
        });
    }

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
        public MyViewHolder(View itemView){
            super(itemView);
            itemView.setOnClickListener(this);
            textView =(TextView)itemView.findViewById(R.id.tv_row);
        }

        public void setItemClickListener(ItemClickListener itemClickListener){
            this.itemClickListener = (ItemClickListener) itemClickListener;
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