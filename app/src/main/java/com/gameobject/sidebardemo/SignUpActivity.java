package com.gameobject.sidebardemo;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {


    EditText nameText;
    EditText addressText;
    EditText emailText;
    EditText passwordText;
    EditText reEnterPasswordText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        nameText = findViewById(R.id.input_name);
        addressText = findViewById(R.id.input_address);
        emailText = findViewById(R.id.input_email);
        passwordText = findViewById(R.id.input_password);
        reEnterPasswordText = findViewById(R.id.input_reEnterPassword);

    }


    //Login Link Clicked
    public void LoginButtonClicked(View v)
    {
        Intent intent = new Intent(this,LoginActivity.class);
        startActivity(intent);
    }

    public void SignUp(View v)
    {
        boolean isValid = Check();

        if(isValid == false)
        {
            Toast.makeText(this, "SignUp failed", Toast.LENGTH_LONG).show();
            return;
        }
        v.clearFocus();

        final ProgressDialog progressDialog = new ProgressDialog(SignUpActivity.this, R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creating Account...");
        progressDialog.show();

        final String name = nameText.getText().toString();
        final String address = addressText.getText().toString();
        final String email = emailText.getText().toString();
        final String password = passwordText.getText().toString();

        final String REGISTER_REQUEST_URL="http://fakharahmad.000webhostapp.com/register.php";

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, REGISTER_REQUEST_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {

                            JSONObject jsonObject = new JSONObject(response);

                            boolean result = jsonObject.getBoolean("result");

                            if(result)
                            {


                                AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
                                builder.setMessage("User Created Successfully")
                                        .setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {

                                                progressDialog.setMessage("Redirecting...");

                                                new android.os.Handler().postDelayed(
                                                        new Runnable() {
                                                            public void run() {

                                                                Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
                                                                startActivity(intent);

                                                                progressDialog.dismiss();
                                                            }
                                                        }, 5000);
                                            }
                                        })
                                        .create()
                                        .show();

                            }
                            else
                            {
                                progressDialog.dismiss();
                                AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this );
                                builder.setMessage("Registration Failed")
                                        .setNegativeButton("Retry", null)
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
                        progressDialog.dismiss();
                        AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
                        builder.setMessage(error.toString())
                                .setNegativeButton("Retry",null)
                                .create()
                                .show();
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("user_name",name);
                params.put("user_address",address);
                params.put("user_email" , email);
                params.put("user_password" , password);
                return params;
            }
        };


        requestQueue.add(stringRequest);

    }

    public boolean Check() {
        boolean valid = true;

        String name = nameText.getText().toString();
        String address = addressText.getText().toString();
        String email = emailText.getText().toString();
        String password = passwordText.getText().toString();
        String reEnterPassword = reEnterPasswordText.getText().toString();

        if (name.isEmpty() || name.length() < 3) {
            nameText.setError("at least 3 characters");
            valid = false;
        } else {
            nameText.setError(null);
        }

        if (address.isEmpty()) {
            addressText.setError("Enter Valid Address");
            valid = false;
        } else {
            addressText.setError(null);
        }


        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailText.setError("enter a valid email address");
            valid = false;
        } else {
            emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            passwordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            passwordText.setError(null);
        }

        if (reEnterPassword.isEmpty() || reEnterPassword.length() < 4 || reEnterPassword.length() > 10 || !(reEnterPassword.equals(password))) {
            reEnterPasswordText.setError("Password Do not match");
            valid = false;
        } else {
            reEnterPasswordText.setError(null);
        }

        return valid;
    }
}

