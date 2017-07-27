package com.example.annie.musicscore;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;


public class UserArea extends AppCompatActivity {
    private TextView etUsername, welcomeMessage;
    private String name, username, perfil, temp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_area);

        etUsername = (TextView)findViewById(R.id.etUsername);
        welcomeMessage = (TextView)findViewById(R.id.tv_WelcomeMsg);

        Bundle bundle = getIntent().getExtras();
        name = bundle.getString("name");
        username = bundle.getString("username");
        perfil = bundle.getString("perfil");

        welcomeMessage.setText("Bienvenido a Music Score");
        etUsername.setText(name);


        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                Intent i = new Intent(UserArea.this, MenuPpal.class);
                i.putExtra("perfil",perfil);
                i.putExtra("nombre",name);
                i.putExtra("correo",username);

                UserArea.this.startActivity(i);
                finish();
            }
        }, 1000);
    }
}
