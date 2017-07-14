package com.example.annie.musicscore;

/**
 * Created by Annie on 30-11-2016.
 * Aqui se define el contenido del recyclerView.
 */

public class Datos {
    String nombre;
    int image;

    public String getName(){
        return nombre;
    }
    public void setName(String nombre){
        this.nombre = nombre;
    }
    public int getImage(){
        return image;
    }
    public void setImage(int image){
        this.image = image;
    }
    /*
    public static ArrayList<item>getData(){
        ArrayList<item> data = new ArrayList<>();
        int[] images = {
                R.drawable.reliquias, R.drawable.platform, R.drawable.cicatriz, R.drawable.gafas,R.drawable.hedwid,
                R.drawable.harry,R.drawable.hermione, R.drawable.gryffindor,R.drawable.ravenclaw,R.drawable.hogward,
                R.drawable.slytherin,R.drawable.hufflepuff,R.drawable.newt,R.drawable.niffler,R.drawable.demiguise,
                R.drawable.scamander,R.drawable.tina,R.drawable.queenie, R.drawable.jacob };
        String [] titulo = {"Reliquias de la Muerte","Plataforma 9 y 3/4", "Cicatriz de Harry","Gafas de Harry","Hedwid","Harry Potter",
                "Hermione Granger","Gryffindor","Ravenclaw","Hogwarts","Slytherin","Hufflepuff","Newt Scamander","Niffler","Demiguise","Newt Scamander",
                "Tina Goldstein","Queenie Goldstein","Jacob Kowalski"};

        for(int i = 0; i<images.length; i++){
            item current = new item();
            current.title = titulo[i];
            current.imageId = images[i];

            data.add(current);
        }
        return data;
    }
    */
}
