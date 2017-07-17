package com.example.annie.musicscore;

/**
 * Created by Annie on 16-07-2017.
 */

public class Datos_obs {
    String fecha, desc, username, hora;
    public String getFecha(){
        return fecha;
    }
    public void setFecha(String fecha){ this.fecha = fecha; }
    public String getHora() { return hora; }
    public void setHora(String hora){ this.hora = hora; }
    public String getDesc(){
        return desc;
    }
    public void setDesc(String desc){ this.desc = desc; }
    public String getUsername(){
        return username;
    }
    public void setUsername(String username){ this.username = username; }
}
