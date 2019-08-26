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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SharedListFragment extends Fragment {

    SessionManager sessionManager;
    HashMap<String, String> user;

    ArrayList<String> taskList;
    ArrayList<String> taskSubmittedByList;

    private RecyclerView recyclerView;

    private  RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    String tasktToDelete;

    boolean isUndoPressed = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_sharedlist, container, false);

        sessionManager = new SessionManager(getContext());
        user = sessionManager.getUserDetail();

        //add task floating button handling
        FloatingActionButton floatingAddButton = (FloatingActionButton) view.findViewById(R.id.addtaskSharedButton);

        floatingAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddTaskSharedDialog addTaskDialog = new AddTaskSharedDialog();
                addTaskDialog.show(getFragmentManager(), " Task Dialog");
            }
        });
        //end code of floating  button handling

        //Displaying the task into the recycler view
        taskList = new ArrayList<String>();
        taskSubmittedByList = new ArrayList<String>();

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_sharedlist);

         String URL_READ_USER_TASK_SHARED = "http://fakharahmad.000webhostapp.com/readusertaskshared.php";

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());

        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL_READ_USER_TASK_SHARED,
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
                                String user_name = jsonObject.getString("user_name");

                                taskList.add(user_task);
                                taskSubmittedByList.add(user_name);

                                //Recycler View Code Starts


                                layoutManager = new LinearLayoutManager(getContext());
                                recyclerView.setLayoutManager(layoutManager);

                                adapter = new UserTaskSharedAdapter(taskList,taskSubmittedByList,getContext());

                                recyclerView.setItemAnimator(new DefaultItemAnimator());
                                recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));

                                recyclerView.setAdapter(adapter);



                                //Recycler View Code Ends



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
                });



                requestQueue.add(stringRequest);




        final ProgressDialog progressDialog = new ProgressDialog(getContext(), R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Loading...");
        progressDialog.show();


        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {

                        progressDialog.dismiss();

                        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelperShared(0, ItemTouchHelper.LEFT, new RecyclerItemTouchHelperShared.RecyclerItemTouchHelperListener() {
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
        final String DELETE_REQUEST_URL="http://fakharahmad.000webhostapp.com/deletesharedtask.php";
        RequestQueue requestQueue1 = Volley.newRequestQueue(getContext());

        StringRequest stringRequest = new StringRequest(Request.Method.POST, DELETE_REQUEST_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("Fakhar" ,response);
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

                map.put("user_task" ,tasktToDelete);

                Log.e("Fakhar" , tasktToDelete);
                return map;
            }
        };



        requestQueue1.add(stringRequest);

    }

    public void Swiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (viewHolder instanceof UserTaskSharedAdapter.UserTaskViewHolder) {

            String name = taskList.get(viewHolder.getAdapterPosition());
            tasktToDelete = name;


            final String deletedItem1 = taskList.get(viewHolder.getAdapterPosition());
            final String deletedItem2 = taskSubmittedByList.get(viewHolder.getAdapterPosition());
            final int deletedIndex = viewHolder.getAdapterPosition();


            ((UserTaskSharedAdapter) adapter).removeItem(viewHolder.getAdapterPosition());



            Snackbar snackbar = Snackbar
                    .make(getView(), name + " removed from Task List!", Snackbar.LENGTH_LONG);
            snackbar.setAction("UNDO", new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    isUndoPressed = true;
                    ((UserTaskSharedAdapter) adapter).restoreItem(deletedItem1,deletedItem2, deletedIndex);
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
