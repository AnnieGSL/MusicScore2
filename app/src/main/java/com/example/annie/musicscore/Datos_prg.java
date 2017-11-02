package com.example.annie.musicscore;

/**
 * Created by Annie on 19-07-2017.
 */

public class Datos_prg {
    String fecha;
    int min, pag, id;

    public String getFecha(){
        return fecha;
    }
    public void setFecha(String fecha){  this.fecha = fecha; }
    public int getMin(){
        return min;
    }
    public void setMin(int min){
        this.min = min;
    }
    public int getPag(){
        return pag;
    }
    public void setPag(int pag){
        this.pag = pag;
    }
    public int getId(){ return  id;}
    public void setId(int id){ this.id = id;}
}
