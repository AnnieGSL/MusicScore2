package com.example.annie.musicscore;

/**
 * Created by Annie on 28-08-2017.
 */

public class msg {
    String id, mensaje, fecha, hora, emisor, receptor, perfil, name;
    int flag;

    public String getId() {return  id;}
    public void setId(String id){this.id = id;}
    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje){this.mensaje = mensaje;}
    public String getFecha(){
        return fecha;
    }
    public void setFecha(String fecha){
        this.fecha = fecha;
    }
    public String getHora(){
        return hora;
    }
    public void setHora(String hora){
        this.hora = hora;
    }
    public String getEmisor(){
        return emisor;
    }
    public void setEmisor(String emisor){
        this.emisor = emisor;
    }
    public String getReceptor(){return receptor;}
    public void setReceptor(String receptor){this.receptor = receptor;}
    public String getNameReceptor(){return name;}
    public void setNameReceptor(String name){this.name =name;}
    public int getFlag(){return flag;}
    public void setFlag(int flag){this.flag = flag;}
    public String getPerf(){return perfil;}
    public void setPerf(String perfil){this.perfil = perfil;}

}
