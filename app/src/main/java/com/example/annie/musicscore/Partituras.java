package com.example.annie.musicscore;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.File;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class Partituras extends Fragment {
    RecyclerView recyclerView;
    partAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fe = inflater.inflate(R.layout.fragment_partituras, container, false);


        recyclerView = (RecyclerView)fe.findViewById(R.id.rvParts);

        adapter = new partAdapter(getActivity(), getPDFs());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);
        return fe;
    }

    private ArrayList<Datos_simple> getPDFs() {
        ArrayList<Datos_simple> pdfDocs = new ArrayList<>();
        File downloadsFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        Datos_simple pdfDoc;
        if(downloadsFolder.exists()){
            File[] files = downloadsFolder.listFiles();
            for (int i=0; i<files.length; i++){
                File file=files[i];
                if(file.getPath().endsWith("pdf")){
                    pdfDoc = new Datos_simple();
                    pdfDoc.setName(file.getName());
                    pdfDoc.setPath(file.getAbsolutePath());
                    pdfDocs.add(pdfDoc);
                }
            }
        }
        return pdfDocs;
    }

}