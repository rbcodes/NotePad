package com.example.rishabh.notepad;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import java.util.HashMap;

public class SessionManager {
    // Shared Preferences
    SharedPreferences pref;

    // Editor for Shared preferences
    Editor editor;

    // Context
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Sharedpref file name
    private static final String PREF_NAME = "AndroidHivePref";

    // All Shared Preferences Keys
    private static final String IS_LOGIN = "IsLoggedIn";

    // User name (make variable public to access from outside)
    public static final String KEY_NAME = "name";

    public static final String KEY_ID = "id";


    // Email address (make variable public to access from outside)
    public static final String KEY_EMAIL = "email";


    // Email address (make variable public to access from outside)
    public static final String KEY_PROFILEPIC = "profilepic";




    // Constructor
    public SessionManager(Context context){
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    /**
     * Create login session
     * */
    public void createLoginSession(String id,String name,String profilepic, String email){
        // Storing login value as TRUE
        editor.putBoolean(IS_LOGIN, true);

        editor.putString(KEY_ID, id);

        editor.putString(KEY_EMAIL, email);
        // Storing name in pref
        editor.putString(KEY_NAME, name);

        editor.putString(KEY_PROFILEPIC, profilepic);
        // commit changes
        editor.commit();
    }


    public void setprofilepic(String profilepic){

        editor.putString(KEY_PROFILEPIC, profilepic);
        // commit changes
        editor.commit();
    }

    public void setname(String name){

        editor.putString(KEY_NAME, name);
        // commit changes
        editor.commit();
    }





    /**
     * Get stored session data
     * */
    public HashMap<String, String> getUserDetails(){
        HashMap<String, String> user = new HashMap<String, String>();
        // user name
        user.put(KEY_ID, pref.getString(KEY_ID, null));

        user.put(KEY_NAME, pref.getString(KEY_NAME, null));

        // user email id

        user.put(KEY_PROFILEPIC, pref.getString(KEY_PROFILEPIC, null));

        user.put(KEY_EMAIL, pref.getString(KEY_EMAIL, null));


        // return user
        return user;
    }

    /**
     * Clear session details
     * */

    public void logoutUser(){
        // Clearing all data from Shared Preferences
        editor.clear();
        editor.commit();

        // After logout redirect user to Loing Activity
        Intent i = new Intent(_context, MainActivity.class);
        // Closing all the Activities
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // Add new Flag to start new Activity
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // Staring Login Activity
        _context.startActivity(i);
    }

    /**
     * Quick check for login
     * **/
    // Get Login State
    public boolean isLoggedIn(){
        return pref.getBoolean(IS_LOGIN, false);
    }
}