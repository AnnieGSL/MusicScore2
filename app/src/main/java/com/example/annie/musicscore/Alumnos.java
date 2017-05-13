package com.example.annie.musicscore;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 */
public class Alumnos extends Fragment {
    RecyclerView recyclerView;
    almAdapter adapter;


    public Alumnos() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View fa = inflater.inflate(R.layout.fragment_alumnos, container, false);

        recyclerView = (RecyclerView)fa.findViewById(R.id.rvAlmns);

        adapter = new almAdapter(getActivity(), Datos.getData());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // Inflate the layout for this fragment
        return fa;
    }

}
