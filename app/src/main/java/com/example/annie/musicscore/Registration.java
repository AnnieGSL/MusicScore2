package com.example.annie.musicscore;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Registration extends AppCompatActivity {
    private static final String TAG = "RegisterActivity";
    //private static final String URL_FOR_REGISTRATION = "http://192.168.1.8/MusicScore/Register.php";
    private static final String URL_FOR_REGISTRATION = "http://musictesis.esy.es/Register.php";
    ProgressDialog pDialog;

    private EditText etAge;
    private EditText etName;
    private EditText etUser;
    private EditText etPassword;

    private Button bRegister;
    private Button btnLinkToLogin;

    private RadioGroup rgPerfil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        //Progress Dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        etAge = (EditText)findViewById(R.id.etAge);
        etName = (EditText)findViewById(R.id.etName);
        etUser = (EditText)findViewById(R.id.etUser);
        etPassword = (EditText)findViewById(R.id.etPassword);

        bRegister = (Button)findViewById(R.id.user_register_button);
        btnLinkToLogin = (Button)findViewById(R.id.btnLinkToLoginScreen);

        rgPerfil = (RadioGroup)findViewById(R.id.perfil_rg);

        bRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitForm();
            }
        });

        // Link to Login Screen
        btnLinkToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(i);
                finish();
            }
        });
    }

    private void submitForm(){
        int selectedId = rgPerfil.getCheckedRadioButtonId();
        String perfil;
        if(selectedId == R.id.a_radio_btn)
            perfil = "Alumno";
        else
            perfil = "Profesor";

        registerUser(etUser.getText().toString(), etName.getText().toString(), etPassword.getText().toString(), perfil, etAge.getText().toString());
    }

    private void registerUser(final String username, final String name, final String password, final String perfil, final String age) {
        // Tag used to cancel the request
        String cancel_req_tag = "register";
        pDialog.setMessage("Adding you...");
        showDialog();
        final String token = SharedPrefManager.getInstance(this).getDeviceToken();
        if (token == null) {
            hideDialog();
            Toast.makeText(this, "Token no fue generado", Toast.LENGTH_LONG).show();
            return;
        }

        StringRequest strReq = new StringRequest(Request.Method.POST,
                URL_FOR_REGISTRATION, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Register Response: " + response.toString());
                hideDialog();
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    if (!error) {
                        String user = jObj.getJSONObject("user").getString("name");
                        Toast.makeText(getApplicationContext(), "Hola " + user +", haz sido registrado con éxito", Toast.LENGTH_SHORT).show();

                        // Launch login activity
                        Intent intent = new Intent(Registration.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Registration Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("name", name);
                params.put("username", username);
                params.put("password", password);
                params.put("perfil", perfil);
                params.put("age", age);
                params.put("token", token);
                return params;
            }
        };
        // Adding request to request queue
        AppSingleton.getInstance(getApplicationContext()).addToRequestQueue(strReq, cancel_req_tag);
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
}
