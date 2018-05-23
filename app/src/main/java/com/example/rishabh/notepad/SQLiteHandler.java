package com.example.rishabh.notepad;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

public class SQLiteHandler extends SQLiteOpenHelper {

    private static final String TAG = "SQLiteHandler";

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "naiveindia.notes";

    // Login table name
    private static final String TABLE_USERS = "users";
    private static final String TABLE_NOTES = "notes";
    private static final String TABLE_DELETED_NOTES = "deletednotes";


    // Login Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_IMAGE = "image";
    private static final String KEY_USERID = "user_id";
    private static final String KEY_NAME = "name";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_UID = "uid";
    private static final String KEY_CREATED_AT = "created_at";

    public SQLiteHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("HHHHHHHHHHHHHHHHHH", "in on create");

        String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS + "("
                + KEY_ID +" INTEGER PRIMARY KEY AUTOINCREMENT,"+ KEY_NAME + " TEXT," +KEY_IMAGE +" TEXT,"+ KEY_EMAIL + " TEXT,"+ KEY_CREATED_AT + " TEXT" + ")";
        db.execSQL(CREATE_USERS_TABLE);

        String CREATE_NOTES_TABLE = "CREATE TABLE " + TABLE_NOTES+ "( id INTEGER PRIMARY KEY AUTOINCREMENT,user_id INTEGER ,"
                + "note_header TEXT,note_text TEXT, note_image BLOB, image_exist int, created_at TEXT, updated_at TEXT )";
        db.execSQL(CREATE_NOTES_TABLE);

        String CREATE_DELETED_NOTES_TABLE = "CREATE TABLE " + TABLE_DELETED_NOTES+"( id INTEGER PRIMARY KEY AUTOINCREMENT, user_id INTEGER ,"
                + "note_header TEXT,note_text TEXT, note_image BLOB, image_exist int, created_at TEXT, updated_at TEXT )";
        db.execSQL(CREATE_DELETED_NOTES_TABLE);
        Log.d(TAG, "Database tables created");
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DELETED_NOTES);
        // Create tables again
        onCreate(db);
    }

    /**
     * Storing user details in database
     * */
    public void addUser(String name,String image, String email,String created_at) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues(); // user_id
        values.put(KEY_NAME, name); // Name
        values.put(KEY_IMAGE, image); // user_image
        values.put(KEY_EMAIL, email); // Email
        values.put(KEY_CREATED_AT, created_at); // Created At

        // Inserting Row
        long id = db.insert(TABLE_USERS, null, values);
        db.close(); // Closing database connection

        Log.d(TAG, "New user inserted into sqlite: " + id);
    }

    /**
     * Storing class details in database
     * */
    public void addnote(String userid,String noteheader,String notetext, byte[] noteimage, int image_exist,String createdat, String updatedat) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_USERID, userid); // user_id
        values.put("note_header", noteheader); // Name
        values.put("note_text", notetext); // user_image
        values.put("note_image", noteimage); // Email
        values.put("image_exist", image_exist); // Email
        values.put(KEY_CREATED_AT, createdat); // Created At
        values.put("updated_at", updatedat); // Created At

        // Inserting Row
        long id = db.insert(TABLE_NOTES, null, values);
        db.close(); // Closing database connection

        Log.d(TAG, "New note inserted into sqlite: " + id+ " "+noteheader+" "+notetext+" "+noteimage);
    }

    public void adddeletednote(String userid,String noteheader,String notetext, byte[] noteimage,String createdat, String updatedat) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID, userid); // user_id
        values.put("note_header", noteheader); // Name
        values.put("note_text", notetext); // user_image
        values.put("note_image", noteimage); // Email
        values.put(KEY_CREATED_AT, createdat); // Created At
        values.put("updated_at", updatedat); // Created At

        // Inserting Row
        long id = db.insert(TABLE_NOTES, null, values);
        db.close(); // Closing database connection

        Log.d(TAG, "New note inserted into sqlite: " + id);
    }

    public ArrayList<HashMap<String, String>> getnotes(String id) {
        ArrayList<HashMap<String, String>> classes = new ArrayList<HashMap<String, String>>();
        String selectQuery = "SELECT  * FROM " + TABLE_NOTES+" where user_id = "+id +" ORDER BY "+id;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            do {
                HashMap<String, String> user = new HashMap<String, String>();
                user.put("note_exist", "true");
                user.put("note_id", cursor.getString(cursor.getColumnIndex("id")));
                user.put("note_header", cursor.getString(cursor.getColumnIndex("note_header")));
                user.put("note_text", cursor.getString(cursor.getColumnIndex("note_text")));
                user.put("image_exist", cursor.getString(cursor.getColumnIndex("image_exist")));
                user.put("created_at", cursor.getString(cursor.getColumnIndex("created_at")));
                user.put("updated_at", cursor.getString(cursor.getColumnIndex("updated_at")));

                Log.d(TAG, "Fetching user from Sqlite: " + user.toString() + " " + cursor.getCount());
                classes.add(user);
            }while (cursor.moveToNext());
        }
        else {
            HashMap<String, String> user = new HashMap<String, String>();
            user.put("note_exist", "false");
            classes.add(user);
        }
        cursor.close();
        db.close();
        // return user
        return classes;
    }

    public ArrayList<HashMap<String, byte[]>> getnotesimage(String id) {
        ArrayList<HashMap<String, byte[]>> classes = new ArrayList<HashMap<String, byte[]>>();
        String selectQuery = "SELECT  * FROM " + TABLE_NOTES+" where user_id = "+id +" ORDER BY "+id;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            do {
                HashMap<String, byte[]> user = new HashMap<String, byte[]>();

                user.put("note_image", cursor.getBlob(cursor.getColumnIndex("note_image")));

                Log.d(TAG, "Fetching user from Sqlite: " + user.toString() + " " + cursor.getCount());
                classes.add(user);
            }while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        // return user
        return classes;
    }

    public ArrayList<HashMap<String, String>> getdeletednotes(String id) {
        ArrayList<HashMap<String, String>> classes = new ArrayList<HashMap<String, String>>();
        String selectQuery = "SELECT  * FROM " + TABLE_DELETED_NOTES + " where user_id = " + id + " ORDER BY " + id;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            do {
                HashMap<String, String> user = new HashMap<String, String>();
                user.put("note_exist", "true");
                user.put("note_id", cursor.getString(cursor.getColumnIndex("id")));
                user.put("note_header", cursor.getString(cursor.getColumnIndex("note_header")));
                user.put("note_text", cursor.getString(cursor.getColumnIndex("note_text")));
                user.put("image_exist", cursor.getString(cursor.getColumnIndex("image_exist")));
                user.put("created_at", cursor.getString(cursor.getColumnIndex("created_at")));
                user.put("updated_at", cursor.getString(cursor.getColumnIndex("updated_at")));

                Log.d(TAG, "Fetching user from Sqlite: " + user.toString() + " " + cursor.getCount());
                classes.add(user);
            } while (cursor.moveToNext());
        } else {
            HashMap<String, String> user = new HashMap<String, String>();
            user.put("note_exist", "false");
            classes.add(user);
        }

        cursor.close();
        db.close();
        // return user
        return classes;
    }

    public ArrayList<HashMap<String, byte[]>> getdeletednotesimage(String id) {
        ArrayList<HashMap<String, byte[]>> classes = new ArrayList<HashMap<String, byte[]>>();
        String selectQuery = "SELECT  * FROM " + TABLE_DELETED_NOTES+" where user_id = "+id +" ORDER BY "+id;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            do {
                HashMap<String, byte[]> user = new HashMap<String, byte[]>();

                user.put("note_image", cursor.getBlob(cursor.getColumnIndex("note_image")));

                Log.d(TAG, "Fetching user from Sqlite: " + user.toString() + " " + cursor.getCount());
                classes.add(user);
            }while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        // return user
        return classes;
    }

    public String getid(String mail) {
        String userid = null;
        String selectQuery = "SELECT  id FROM " + TABLE_USERS +" where email = '"+ mail+"'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            do {
                userid = cursor.getString(cursor.getColumnIndex("id"));
            }while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        // return user
        return userid;
    }


    public void deletenote(int position) {

        String selectQuery = "SELECT  * FROM " + TABLE_NOTES + " where id = "+position;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            do {
                ContentValues values = new ContentValues();
                values.put(KEY_ID, cursor.getString(cursor.getColumnIndex("id"))); // user_id
                values.put("note_header",cursor.getString(cursor.getColumnIndex("note_header"))); // Name
                values.put("user_id", cursor.getString(cursor.getColumnIndex("user_id"))); // user_image
                values.put("note_text", cursor.getString(cursor.getColumnIndex("note_text"))); // user_image
                values.put("note_image", cursor.getBlob(cursor.getColumnIndex("note_image"))); // Email
                values.put("image_exist", cursor.getString(cursor.getColumnIndex("image_exist")));
                values.put(KEY_CREATED_AT, cursor.getString(cursor.getColumnIndex("created_at"))); // Created At
                values.put("updated_at", cursor.getString(cursor.getColumnIndex("updated_at"))); // Created At

                long id = db.insert(TABLE_DELETED_NOTES, null, values);
            }while (cursor.moveToNext());
        }
        cursor.close();
        String countQuery = "DELETE FROM " + TABLE_NOTES +" WHERE id = "+position;
        Cursor cursor1 = db.rawQuery(countQuery, null);
        Log.d("ghghghghhghghghghg", "DELETING FROM OFFLINE " + countQuery + " " + cursor1.getCount());
        db.close();
        cursor.close();

        // return row count

    }
    public void deletenotefinal(int position) {

        String selectQuery = "SELECT  * FROM " + TABLE_NOTES + " where id = "+position;
        SQLiteDatabase db = this.getReadableDatabase();
        String countQuery = "DELETE FROM " + TABLE_DELETED_NOTES +" WHERE id = "+position;
        Cursor cursor1 = db.rawQuery(countQuery, null);
        Log.d("ghghghghhghghghghg", "DELETING FROM OFFLINE " + countQuery + " " + cursor1.getCount());
        db.close();
        cursor1.close();

        // return row count

    }

    public void updatedeletednote(String userid,String noteheader,String notetext, byte[] noteimage, int image_exist,String createdat, String updatedat) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_USERID, userid); // user_id
        values.put("note_header", noteheader); // Name
        values.put("note_text", notetext); // user_image
        values.put("note_image", noteimage); // Email
        values.put("image_exist", image_exist); // Email
        values.put(KEY_CREATED_AT, createdat); // Created At
        values.put("updated_at", updatedat); // Created At

        // Inserting Row
        long id = db.insert(TABLE_NOTES, null, values);
        db.close(); // Closing database connection

        Log.d(TAG, "New note inserted into sqlite: " + id + " " + noteheader + " " + notetext + " " + noteimage);
    }


    public void deleteUsers() {
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        db.delete(TABLE_USERS, null, null);
        db.delete(TABLE_NOTES, null, null);
        db.delete(TABLE_DELETED_NOTES, null, null);
        db.close();
        Log.d(TAG, "Deleted all user info from sqlite");
    }

    public int updatenote(String id,String noteheader,String notetext, byte[] noteimage, int image_exist, String updatedat) {

        if(image_exist == 1) {

            SQLiteDatabase db = this.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put("note_header", noteheader);
            values.put("note_text", notetext);
            values.put("note_image", noteimage);
            values.put("updated_at", updatedat);

            // updating row
            return db.update(TABLE_NOTES, values, KEY_ID + " = ?",
                    new String[] { ""+id});
/*
            String selectQuery = "UPDATE " + TABLE_NOTES + " SET  note_header = '" + noteheader + "' ,note_text = '" + notetext + "' ,note_image = " + noteimage + " , updated_at = '" + updatedat + "' WHERE id = " + id;
            Log.d("ghghghghhghghghghg", selectQuery);
            SQLiteDatabase db = this.getWritableDatabase();
           db.execSQL(selectQuery);*/

        }
        else
        {
            String selectQuery = "UPDATE " + TABLE_NOTES + " SET  note_header = '" + noteheader + "' ,note_text = '" + notetext + "' ,updated_at = '" + updatedat + "' WHERE id = " + id;
            Log.d("ghghghghhghghghghg", selectQuery);
             SQLiteDatabase db = this.getWritableDatabase();
             db.execSQL(selectQuery);
             db.close();
        }

        return 0;
    }





}