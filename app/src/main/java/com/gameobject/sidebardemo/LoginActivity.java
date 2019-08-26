package com.gameobject.sidebardemo;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class LoginActivity extends AppCompatActivity {

    EditText emailText;
    EditText passwordText;

    int user_id;
    String user_name;
    String user_address;
    String user_email;
    String user_password;

    SessionManager sessionManager;

    Boolean isLogin = false;
    private static String URL_Login = "http://fakharahmad.000webhostapp.com/login.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailText = findViewById(R.id.input_email);
        passwordText = findViewById(R.id.input_password);

        sessionManager = new SessionManager(this);

    }

    //Signup Link Clicked
    public void SignUpButtonClicked(View v)
    {
        Intent intent = new Intent(this, SignUpActivity.class);
        startActivity(intent);

    }

    //Login Button Clicked
    public void Login(View v)
    {
        boolean isValid = Check();

        if(isValid == false)
        {
            Toast.makeText(this, "Login failed", Toast.LENGTH_LONG).show();
            return;
        }

        v.clearFocus();
                final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this,
                R.style.AppTheme_Dark_Dialog);
                progressDialog.setIndeterminate(true);
                progressDialog.setMessage("Authenticating...");
                progressDialog.show();


        RequestQueue requestQueue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL_Login,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            //converting the string to json array object
                            JSONArray array = new JSONArray(response);

                            //traversing through all the object
                            for (int i = 0; i < array.length(); i++) {

                                //getting product object from json array
                                JSONObject user = array.getJSONObject(i);

                                 user_id = user.getInt("user_id");
                                user_name = user.getString("user_name");
                                user_address = user.getString("user_address");
                                user_email = user.getString("user_email");
                                user_password = user.getString("user_password");

                                if(emailText.getText().toString().equals(user_email) && passwordText.getText().toString().equals(user_password))
                                {
                                    isLogin = true;
                                    break;

                                }

                            }


                            if(isLogin)
                            {
                                progressDialog.setMessage("Redirecting...");
                                new android.os.Handler().postDelayed(
                                        new Runnable() {
                                            public void run() {
                                                // On complete call either onLoginSuccess or onLoginFailed
                                                Intent intent = new Intent(getApplicationContext(),MainActivity.class);

                                                intent.putExtra("user_name" , user_name);
                                                intent.putExtra("user_address" , user_address);
                                                intent.putExtra("user_email" , user_email);
                                                intent.putExtra("user_password" , user_password);

                                                String userid_str = user_id+"";


                                                    if(!SessionManager.existingUserEmail.equals(user_email))
                                                        ProfileFragment.bitmap = null;





                                                sessionManager.createsession(user_name,user_email,userid_str,user_address,user_password);

                                                startActivity(intent);

                                                progressDialog.dismiss();
                                            }
                                        }, 5000);
                            }
                            else
                            {
                                progressDialog.dismiss();
                                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                                builder.setMessage("Incorrect username or password")
                                        .setNegativeButton("Retry",null)
                                        .create()
                                        .show();
                            }





                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }
        );

        requestQueue.add(stringRequest);


    }

    public boolean Check()
    {
        boolean isValid = true;

        String email = emailText.getText().toString();
        String password = passwordText.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            emailText.setError("provide a valid email address");
            isValid = false;
        }
        else
        {
            emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10)
        {
            passwordText.setError("between 4 and 10 alphanumeric characters");
            isValid = false;
        }
        else
        {
            passwordText.setError(null);
        }

        return  isValid;
    }



}

