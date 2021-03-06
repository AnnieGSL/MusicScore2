package com.example.annie.musicscore;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {
    // UI references.
    private static final String TAG = "LoginActivity";
    //private static final String URL_FOR_LOGIN = "http://192.168.1.8/MusicScore/Login.php";
    private static final String URL_FOR_LOGIN = "http://musictesis.esy.es/Login.php";
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private Button mEmailSignInButton;
    private Button btnLinkToRegister;
    private ImageView mLogin;
    private ProgressDialog pDialog;
    private String n, u, p;

    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        mPasswordView = (EditText) findViewById(R.id.password);
        mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        btnLinkToRegister = (Button) findViewById(R.id.btnLinkToRegisterScreen);
        mLogin = (ImageView)findViewById(R.id.ivLogin);

        sharedpreferences = getSharedPreferences("ArchivoLogin", getApplicationContext().MODE_PRIVATE);
        n = sharedpreferences.getString("name", "");
        u = sharedpreferences.getString("username", "");
        p = sharedpreferences.getString("perfil", "");

        //Toast.makeText(getApplicationContext(), "SP: " + n + u + p, Toast.LENGTH_SHORT).show();
        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        if(n!="" && u!="" && p!=""){
            Intent intent = new Intent(LoginActivity.this, MenuPpal.class);
            intent.putExtra("nombre",n);
            intent.putExtra("correo",u);
            intent.putExtra("perfil",p);
            LoginActivity.this.startActivity(intent);
            finish();
        }else{
            Toast.makeText(getApplicationContext(), "Nuevo inicio de sesión", Toast.LENGTH_SHORT).show();
        }


        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                final String username = mEmailView.getText().toString();
                final String password = mPasswordView.getText().toString();
                final String token = SharedPrefManager.getInstance(LoginActivity.this).getDeviceToken();

                editor = sharedpreferences.edit();

                // Tag used to cancel the request
                //Toast.makeText(getApplicationContext(), "LoginUser", Toast.LENGTH_LONG).show();
                String cancel_req_tag = "login";
                pDialog.setMessage("Iniciando ...");
                showDialog();

                if (token == null) {
                    hideDialog();
                    Toast.makeText(LoginActivity.this, "Token no fue generado", Toast.LENGTH_LONG).show();
                    return;
                }

                StringRequest strReq = new StringRequest(Request.Method.POST, URL_FOR_LOGIN, new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "Login Response: " + response.toString());
                        hideDialog();

                        //Toast.makeText(getApplicationContext(), "response: "+response, Toast.LENGTH_LONG).show();
                        try {
                            JSONObject jObj = new JSONObject(response);
                            boolean error = jObj.getBoolean("error");


                            // Check for error node in json
                            if (!error) {
                                //Toast.makeText(getApplicationContext(), "if!error", Toast.LENGTH_LONG).show();
                                String name = jObj.getJSONObject("user").getString("name");
                                String user = jObj.getJSONObject("user").getString("username");
                                String perf = jObj.getJSONObject("user").getString("perfil");
                                // Launch main activity
                                editor.putString("username", username);
                                editor.putString("password", password);
                                editor.putString("name", name);
                                editor.putString("perfil", perf);
                                editor.commit();
                                Intent intent = new Intent(LoginActivity.this, UserArea.class);
                                intent.putExtra("name",name);
                                intent.putExtra("username",user);
                                intent.putExtra("perfil",perf);
                                LoginActivity.this.startActivity(intent);
                                finish();
                            } else {
                                // Error in login. Get the error message
                                String errorMsg = jObj.getString("error_msg");
                                Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            // JSON error
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "Login Error: " + error.getMessage());
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                        hideDialog();
                    }
                }) {

                    @Override
                    protected Map<String, String> getParams() {
                        // Posting parameters to login url
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("username", username);
                        params.put("password", password);
                        params.put("token", token);
                        return params;
                    }
                };
                // Adding request to request queue
                AppSingleton.getInstance(getApplicationContext()).addToRequestQueue(strReq, cancel_req_tag);


            }
        });

        // Link to Register Screen
        btnLinkToRegister.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), Registration.class);
                startActivity(i);
                finish();
            }
        });
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