package com.example.annie.musicscore;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class UserArea extends AppCompatActivity {
    private Button entrar;
    private TextView etUsername, welcomeMessage;
    private String name, username, perfil;

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


        entrar = (Button)findViewById(R.id.uabtn);

        welcomeMessage.setText(name + "Bienvenido a Music Score");
        etUsername.setText(username);

        entrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(UserArea.this, MenuPpal.class);
                i.putExtra("nombre", name);
                i.putExtra("correo", username);
                i.putExtra("perfil",perfil);
                startActivity(i);
                finish();
            }
        });
    }
}
