package com.example.annie.musicscore;

import android.app.ProgressDialog;
import android.content.Intent;
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

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                final String username = mEmailView.getText().toString();
                final String password = mPasswordView.getText().toString();
                // Tag used to cancel the request
                //Toast.makeText(getApplicationContext(), "LoginUser", Toast.LENGTH_LONG).show();
                String cancel_req_tag = "login";
                pDialog.setMessage("Iniciando ...");
                showDialog();

                StringRequest strReq = new StringRequest(Request.Method.POST, URL_FOR_LOGIN, new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "Login Response: " + response.toString());
                        hideDialog();
                        //Toast.makeText(getApplicationContext(), "onrespose", Toast.LENGTH_LONG).show();

                        try {
                            JSONObject jObj = new JSONObject(response);
                            boolean error = jObj.getBoolean("error");
                            Toast.makeText(getApplicationContext(), "try", Toast.LENGTH_LONG).show();

                            // Check for error node in json
                            if (!error) {
                                //Toast.makeText(getApplicationContext(), "if!error", Toast.LENGTH_LONG).show();
                                String name = jObj.getJSONObject("user").getString("name");
                                String user = jObj.getJSONObject("user").getString("username");
                                String perf = jObj.getJSONObject("user").getString("perfil");
                                //Toast.makeText(getApplicationContext(), name, Toast.LENGTH_LONG).show();
                                // Launch main activity
                                Intent intent = new Intent(LoginActivity.this, UserArea.class);
                                intent.putExtra("name",name);
                                intent.putExtra("username",user);
                                intent.putExtra("perfil",perf);
                                LoginActivity.this.startActivity(intent);
                                //Toast.makeText(getApplicationContext(), user, Toast.LENGTH_LONG).show();
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