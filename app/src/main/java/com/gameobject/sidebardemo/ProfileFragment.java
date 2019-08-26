package com.gameobject.sidebardemo;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
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

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;

public class ProfileFragment extends Fragment{

    SessionManager sessionManager;
    private Menu action;

    EditText nameText,addressText,passwordText;

    HashMap <String, String> user;

    Button btn_photo_upload;

    CircleImageView profileImage;
    String imageUrl = "";

    public static Bitmap bitmap;

    private static String URL_Login = "http://fakharahmad.000webhostapp.com/updateUser.php";
    private static String URL_Upload_Photo = "http://fakharahmad.000webhostapp.com/uploadPhoto.php";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // TODO Add your menu entries here
        inflater.inflate(R.menu.action_menu,menu);

        action = menu;
        action.findItem(R.id.menu_save).setVisible(false);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch(id)
        {
            case R.id.menu_edit:
                nameText.setEnabled(true);
                addressText.setEnabled(true);
                passwordText.setEnabled(true);

                nameText.setFocusableInTouchMode(true);
                addressText.setFocusableInTouchMode(true);
                passwordText.setFocusableInTouchMode(true);

                InputMethodManager inputMethodManager = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.showSoftInput(nameText,InputMethodManager.SHOW_IMPLICIT);

                action.findItem(R.id.menu_edit).setVisible(false);
                action.findItem(R.id.menu_save).setVisible(true);

                return  true;

            case R.id.menu_save:

                SaveDetails();

                nameText.setFocusableInTouchMode(false);
                addressText.setFocusableInTouchMode(false);
                passwordText.setFocusableInTouchMode(false);

                nameText.setFocusable(false);
                addressText.setFocusable(false);
                passwordText.setFocusable(false);

                nameText.setEnabled(false);
                addressText.setEnabled(false);
                passwordText.setEnabled(false);


                action.findItem(R.id.menu_edit).setVisible(true);
                action.findItem(R.id.menu_save).setVisible(false);



                return true;

            default:
                return super.onOptionsItemSelected(item);

        }


    }

    private void SaveDetails()
    {
        final String newName = nameText.getText().toString();
        final String newAddress = addressText.getText().toString();
        final String newPassword = passwordText.getText().toString();
        final String Id = user.get(sessionManager.ID);
        final int finalId = Integer.parseInt(Id);
        final String email = user.get(sessionManager.EMAIL);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_Login,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            boolean result = jsonObject.getBoolean("success");

                            if(result)
                            {

                                sessionManager.createsession(newName,email,Id,newAddress,newPassword);
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
                Map<String , String> params = new HashMap<>();

                params.put("user_id" , Id);
                params.put("user_name" , newName);
                params.put("user_address" , newAddress);
                params.put("user_email" , email);
                params.put("user_password" , newPassword);
                return params;
            }
        };


        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);


    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

         nameText = (EditText) view.findViewById(R.id.name);
         addressText = view.findViewById(R.id.address);
         passwordText = view.findViewById(R.id.password);
         btn_photo_upload = view.findViewById(R.id.btn_photo);
         profileImage = view.findViewById(R.id.profile_image);

         btn_photo_upload.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 ChangePhoto();
             }
         });

        sessionManager = new SessionManager(getContext());

         user = sessionManager.getUserDetail();


        nameText.setText(user.get(sessionManager.NAME));
        addressText.setText(user.get(sessionManager.ADDRESS));
        passwordText.setText(user.get(sessionManager.PASSWORD));

        String id = user.get(sessionManager.ID);

        if(bitmap != null)
            SetPicture();
        else
            SetProfilePicture(id);

        return view;
    }

    void SetPicture()
    {
        profileImage.setImageBitmap(bitmap);
    }

    public void SetProfilePicture(final String id)
    {
        String URL_GET_PROFILE_PICTURE = "http://fakharahmad.000webhostapp.com/getprofilepicture.php";

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());

        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL_GET_PROFILE_PICTURE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONArray array = new JSONArray(response);

                            for (int i = 0; i < array.length(); i++) {

                                JSONObject user = array.getJSONObject(i);
                                String user_photo = user.getString("user_photo");



                                if(!user_photo.isEmpty())
                                {
                                    String []getUserIdFromUserPhoto = user_photo.split("/");
                                    String imageNameFull = getUserIdFromUserPhoto[2];

                                    String []userIdArr = imageNameFull.split("\\.");
                                    String userIdStr = userIdArr[0];

                                    if(userIdStr.equals(id))
                                    {
                                        imageUrl = "http://fakharahmad.000webhostapp.com/"+user_photo;

                                        Picasso.with(getContext()).load(imageUrl)
                                                .into(profileImage);
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

    void ChangePhoto()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_PICK);
        startActivityForResult(Intent.createChooser(intent,"Select Photo"),1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 1 && resultCode == RESULT_OK && data != null)
        {
            Uri filepath = data.getData();
            try {
                InputStream inputStream = getActivity().getContentResolver().openInputStream(filepath);
                bitmap = BitmapFactory.decodeStream(inputStream);
                profileImage.setImageBitmap(bitmap);

                String imageName = getStringImage(bitmap);
                String userId = user.get(sessionManager.ID) +"";

                UploadPicture(userId , imageName);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private void UploadPicture(final String id, final String photo)
    {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_Upload_Photo,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            boolean result = jsonObject.getBoolean("result");

                            if(result)
                            {
                                imageUrl = "http://fakharahmad.000webhostapp.com/upload/images/"+id+".jpeg";
                                MainActivity.ProfilePictureURL = imageUrl;

                                Toast.makeText(getContext(),"Image Uploaded Sucessfully", Toast.LENGTH_LONG).show();

                                MainActivity.UpdateProfilePicture(bitmap);


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
                Map<String , String> params = new HashMap<>();
                params.put("user_id",id);
                params.put("user_photo",photo);

                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
    }

    public String getStringImage(Bitmap bitmap)
    {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);

        byte[] imageByteArray = byteArrayOutputStream.toByteArray();
        String encodeImage = Base64.encodeToString(imageByteArray,Base64.DEFAULT);

        return encodeImage;
    }
}
