package com.gameobject.sidebardemo;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class PersonalListFragment extends Fragment {
    private static String URL_Login = "http://fakharahmad.000webhostapp.com/readusertask.php";
    ArrayList<String> taskList;

    SessionManager sessionManager;
    HashMap<String, String> user;
    String loginEmail;

    private RecyclerView recyclerView;
    private  RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    String tasktToDelete;

    boolean isUndoPressed = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {



        View view = inflater.inflate(R.layout.fragment_personallist, container, false);


        FloatingActionButton floatingAddButton = (FloatingActionButton) view.findViewById(R.id.floatingAddButton);

        floatingAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddTaskDialog addTaskDialog = new AddTaskDialog();
                addTaskDialog.show(getFragmentManager(), " Task Dialog");
            }
        });

        taskList = new ArrayList<String>();


        sessionManager = new SessionManager(getContext());

        user = sessionManager.getUserDetail();

         loginEmail = user.get(sessionManager.EMAIL);


        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);




        final RequestQueue requestQueue = Volley.newRequestQueue(getContext());

        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL_Login,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray array = new JSONArray(response);


                            //traversing through all the object
                            for (int i = 0; i < array.length(); i++) {

                                //getting product object from json array
                                JSONObject jsonObject = array.getJSONObject(i);


                                String user_email = jsonObject.getString("user_email");
                                String user_task = jsonObject.getString("user_task");

                                if(user_email.equals(user.get(sessionManager.EMAIL)))
                                    taskList.add(user_task);

                            }

                            //Recycler View Code Starts


                            layoutManager = new LinearLayoutManager(getContext());
                            recyclerView.setLayoutManager(layoutManager);

                            adapter = new UserTaskAdapter(taskList,getContext());

                            recyclerView.setItemAnimator(new DefaultItemAnimator());
                            recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));

                            recyclerView.setAdapter(adapter);



                            //Recycler View Code Ends



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

        final ProgressDialog progressDialog = new ProgressDialog(getContext(), R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Loading...");
        progressDialog.show();



        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {

                        progressDialog.dismiss();

                        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, new RecyclerItemTouchHelper.RecyclerItemTouchHelperListener() {
                            @Override
                            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
                                                Swiped(viewHolder,direction,position);
                            }
                        });
                        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);

                    }

                }, 3000);









         return view;

    }

    public void deleteTask()
    {
        final String DELETE_REQUEST_URL="http://fakharahmad.000webhostapp.com/delete.php";
        RequestQueue requestQueue1 = Volley.newRequestQueue(getContext());

        StringRequest stringRequest = new StringRequest(Request.Method.POST, DELETE_REQUEST_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                    }
                }
                , new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> map = new HashMap<String, String>();
                map.put("user_email", user.get(sessionManager.EMAIL));
                map.put("user_task" ,tasktToDelete);

                return map;
            }
        };



        requestQueue1.add(stringRequest);

    }

    public void Swiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (viewHolder instanceof UserTaskAdapter.UserTaskViewHolder) {

            String name = taskList.get(viewHolder.getAdapterPosition());
            tasktToDelete = name;


            final String deletedItem = taskList.get(viewHolder.getAdapterPosition());
            final int deletedIndex = viewHolder.getAdapterPosition();


            ((UserTaskAdapter) adapter).removeItem(viewHolder.getAdapterPosition());



            Snackbar snackbar = Snackbar
                    .make(getView(), name + " removed from Task List!", Snackbar.LENGTH_LONG);
            snackbar.setAction("UNDO", new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    isUndoPressed = true;
                    ((UserTaskAdapter) adapter).restoreItem(deletedItem, deletedIndex);
                }
            });
            snackbar.setActionTextColor(Color.YELLOW);
            snackbar.show();

            snackbar.addCallback(new Snackbar.Callback() {

                @Override
                public void onDismissed(Snackbar snackbar, int event) {

                    if(!isUndoPressed)
                    {
                        deleteTask();
                    }
                    isUndoPressed = false;

                }

                @Override
                public void onShown(Snackbar snackbar) {

                }
            });









        }
    }

}


