package com.example.annie.musicscore;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class Partituras extends Fragment {
    RecyclerView recyclerView;
    partAdapter adapter;
    String info;
    ArrayList<Datos_simple> array = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        if(getArguments()!=null){

            Gson gson = new Gson();
            info = getArguments().getString("Datos");
            Type type = new TypeToken<ArrayList<Datos_simple>>(){}.getType();
            array = gson.fromJson(info, type);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fe = inflater.inflate(R.layout.fragment_partituras, container, false);

        recyclerView = (RecyclerView)fe.findViewById(R.id.rvParts);
        adapter = new partAdapter(getActivity(), array);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);
        return fe;
    }
}