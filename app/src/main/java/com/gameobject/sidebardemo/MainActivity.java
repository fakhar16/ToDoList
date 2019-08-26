package com.gameobject.sidebardemo;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

        TextView user_name_textview;
        TextView user_email_textview;

         SessionManager sessionManager;
    HashMap <String, String> user;


    public static  CircleImageView profilePicture;

    public static String ProfilePictureURL = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        sessionManager = new SessionManager(this);
        sessionManager.CheckLogin();

         user = sessionManager.getUserDetail();

        View headerView = navigationView.getHeaderView(0);
        Menu menuView = navigationView.getMenu();

        user_name_textview = (TextView) headerView.findViewById(R.id.user_name_text);
        user_email_textview = (TextView) headerView.findViewById(R.id.user_email_text);
        profilePicture =(CircleImageView) headerView.findViewById(R.id.profilePicture);


        String name = user.get(sessionManager.NAME);
        String email = user.get(sessionManager.EMAIL);
        String id = user.get(sessionManager.ID);
        String address = user.get(sessionManager.ADDRESS);
        String password = user.get(sessionManager.PASSWORD);


        if(ProfileFragment.bitmap != null)
            UpdateProfilePicture(ProfileFragment.bitmap);
        else
            SetProfilePicture(id);


        user_name_textview.setText(name);
        user_email_textview.setText(email);

        if(savedInstanceState == null)
        {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new PersonalListFragment()).commit();

            navigationView.setCheckedItem(R.id.nav_personal_list);
        }




    }

    public void SetProfilePicture(final String id)
    {
         String URL_GET_PROFILE_PICTURE = "http://fakharahmad.000webhostapp.com/getprofilepicture.php";

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL_GET_PROFILE_PICTURE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONArray array = new JSONArray(response);

                            for (int i = 0; i < array.length(); i++) {

                                JSONObject user = array.getJSONObject(i);
                                String user_photo = user.getString("user_photo");

                                String imageUrl = "";

                                if(!user_photo.isEmpty())
                                {
                                    String []getUserIdFromUserPhoto = user_photo.split("/");
                                    String imageNameFull = getUserIdFromUserPhoto[2];

                                    String []userIdArr = imageNameFull.split("\\.");
                                    String userIdStr = userIdArr[0];

                                    if(userIdStr.equals(id))
                                    {
                                        imageUrl = "http://fakharahmad.000webhostapp.com/"+user_photo;

                                        ProfilePictureURL = imageUrl;

                                        Picasso.with(getBaseContext()).load(imageUrl)
                                                .into(profilePicture);
                                    }
                                }
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

    public static void UpdateProfilePicture(Bitmap bitmap)
    {
        profilePicture.setImageBitmap(bitmap);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_personal_list) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new PersonalListFragment()).commit();
        } else if (id == R.id.nav_shared_list) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new SharedListFragment()).commit();

        } else if (id == R.id.nav_profile) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new ProfileFragment()).commit();

        } else if (id == R.id.nav_logout) {

            final ProgressDialog progressDialog = new ProgressDialog(MainActivity.this,
                    R.style.AppTheme_Dark_Dialog);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Logging Out...");
            progressDialog.show();


            new android.os.Handler().postDelayed(
                    new Runnable() {
                        public void run() {
//                        Intent intent = new Intent(getBaseContext(),LoginActivity.class);
//                        startActivity(intent);

                            SessionManager.existingUserEmail = user.get(sessionManager.EMAIL);
                            sessionManager.logout();

                            progressDialog.dismiss();
                        }
                    }, 3000);

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
