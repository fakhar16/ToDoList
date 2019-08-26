package com.gameobject.sidebardemo;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatDialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
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

public class AddTaskSharedDialog extends AppCompatDialogFragment {
    EditText AddTaskTextField;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.add_task_dialog,null);
        AddTaskTextField = view.findViewById(R.id.user_task);

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

                        SessionManager sessionManager;
                        HashMap<String, String> user;

                        sessionManager = new SessionManager(getContext());
                        user = sessionManager.getUserDetail();


                        AddTaskInDB(task,user.get(sessionManager.NAME),user.get(sessionManager.EMAIL));

                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                new SharedListFragment()).commit();

                    }
                });




        return  builder.create();
    }

    public void AddTaskInDB(final String task, final String name, final String email)
    {
        final String REGISTER_REQUEST_URL="http://fakharahmad.000webhostapp.com/addusertaskshared.php";

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());

        StringRequest stringRequest = new StringRequest(Request.Method.POST, REGISTER_REQUEST_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> map = new HashMap<String, String>();

                map.put("user_email",email);
                map.put("user_name",name);
                map.put("user_task",task);

                return map;
            }
        };

        requestQueue.add(stringRequest);
    }



}
