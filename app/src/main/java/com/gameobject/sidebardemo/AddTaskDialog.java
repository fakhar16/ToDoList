package com.gameobject.sidebardemo;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class AddTaskDialog extends AppCompatDialogFragment {
    EditText AddTaskTextField;
    SessionManager sessionManager;
    HashMap <String, String> user;
//    private TaskDialogListener taskDialogListener;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.add_task_dialog,null);

        builder.setView(view)
                .setTitle("Add Task")
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setPositiveButton("add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String task = AddTaskTextField.getText().toString();

                        AddTaskInDB(task);

                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                new PersonalListFragment()).commit();
                    }
                });

        AddTaskTextField = view.findViewById(R.id.user_task);

        sessionManager = new SessionManager(getContext());

        user = sessionManager.getUserDetail();

        return  builder.create();
    }


    public void AddTaskInDB(final String task)
    {
        final String REGISTER_REQUEST_URL="http://fakharahmad.000webhostapp.com/usertask.php";

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());

        StringRequest stringRequest = new StringRequest(Request.Method.POST, REGISTER_REQUEST_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {

                            JSONObject jsonObject = new JSONObject(response);

                            boolean result = jsonObject.getBoolean("result");

                            if(result)
                            {

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
        )
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("user_email" , user.get(sessionManager.EMAIL));
                params.put("user_task" , task);
                return params;
            }
        };

        requestQueue.add(stringRequest);
    }
}
