package com.gameobject.sidebardemo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import java.util.HashMap;

public class SessionManager {
    SharedPreferences sharedPreferences;
    public SharedPreferences.Editor editor;
    public Context context;

    int PRIVATE_MODE = 0;

    private static final String PREF_NAME = "LOGIN";
    private static final String LOGIN = "IS_LOGIN";
    public static  final String NAME = "NAME";
    public static final String EMAIL = "EMAIL";
    public static final String ID = "ID";
    public static final String ADDRESS = "ADDRESS";
    public static final String PASSWORD = "PASSWORD";

        public static String existingUserEmail = "";

    public SessionManager(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences("LOGIN",PRIVATE_MODE);
        editor = sharedPreferences.edit();
    }

    public void createsession(String name , String email , String id , String address , String password)
    {
        editor.putBoolean(LOGIN , true);
        editor.putString(NAME , name);
        editor.putString(EMAIL , email);
        editor.putString(ID , id);
        editor.putString(ADDRESS , address);
        editor.putString(PASSWORD , password);
        editor.apply();

    }

    public boolean isLoggin()
    {
        return sharedPreferences.getBoolean(LOGIN,false);
    }

    public void CheckLogin()
    {
        if(!this.isLoggin())
        {
            Intent intent = new Intent(context,LoginActivity.class);
            context.startActivity(intent);
            ((MainActivity)context).finish();
        }
    }

    public HashMap <String , String> getUserDetail()
    {
        HashMap <String, String> user =  new HashMap<>();
        user.put(NAME,sharedPreferences.getString(NAME,null));
        user.put(EMAIL,sharedPreferences.getString(EMAIL,null));
        user.put(ID,sharedPreferences.getString(ID,null));
        user.put(ADDRESS,sharedPreferences.getString(ADDRESS,null));
        user.put(PASSWORD,sharedPreferences.getString(PASSWORD,null));

        return  user;
    }

    public void logout()
    {
        editor.clear();
        editor.commit();

        Intent intent = new Intent(context,LoginActivity.class);
        context.startActivity(intent);
        ((MainActivity)context).finish();

    }
}
