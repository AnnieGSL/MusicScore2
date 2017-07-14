package com.example.annie.musicscore;
import android.content.Context;
import android.content.Intent;
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
                showPopUpMenu(myViewHolder.imageButton,position);
            }
        });
    }

    private void showPopUpMenu(View view, final int position) {
        PopupMenu popup = new PopupMenu(view.getContext(),view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.popup_alms,popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener(){
            public boolean onMenuItemClick(MenuItem item){
                switch(item.getItemId()) {
                    case R.id.obs:
                        Toast.makeText(context,"Observacion "+position,Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(context, creaObservacion.class);
                        context.startActivity(i);
                        break;
                    case R.id.prg:
                        Toast.makeText(context,"Progreso "+position,Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.msj:
                        Toast.makeText(context,"Mensaje "+position,Toast.LENGTH_SHORT).show();
                        return true;
                }
                return false;
            }
        });
        popup.show();
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
}