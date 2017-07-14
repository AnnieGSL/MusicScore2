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
public class Alumnos extends Fragment {
    RecyclerView recyclerView;
    almAdapter adapter;
    String info;
    ArrayList<Datos> array = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        if(getArguments()!=null){
            Gson gson = new Gson();
            info = getArguments().getString("Datos");
            Type type = new TypeToken<ArrayList<Datos>>(){}.getType();
            array = gson.fromJson(info, type);
        }else{

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View fa = inflater.inflate(R.layout.fragment_alumnos, container, false);

        recyclerView = (RecyclerView)fa.findViewById(R.id.rvAlmns);

        adapter = new almAdapter(getActivity(), array);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);
        return fa;
    }
}
