package com.example.rishabh.notepad;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.plus.Plus;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class HomepageActivity extends AppCompatActivity  {

    static Menu m;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    String[]titles = {"", "Notes", "Deleted Notes"};
    private CharSequence mTitle;
    private CharSequence mDrawerTitle;
    private ActionBarDrawerToggle mDrawerToggle;
    public Toolbar topToolBar;
    SessionManager session;
    HashMap<String, String> user;
    ImageView profilepic = null;
    TextView name;
    TextView lastsynced;
    CustomAdapter adapter;
    ProgressDialog pd;
    View listHeaderView;
    private SQLiteHandler db;
    static Bitmap mprofilepicture;
    Button footer;
    byte[] profileimage;
    int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    static  int image_exist = 0;
    private Bitmap bitmap;
    ContentValues values;
    private GoogleApiClient mGoogleApiClient;
    static String backtrack;
    static Boolean exit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);
        this.backtrack = "nothing";

        session = new SessionManager(getApplicationContext());

        mTitle = mDrawerTitle = getTitle();

        topToolBar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(topToolBar);
        //topToolBar.setLogo(R.drawable.logo);
        topToolBar.setLogoDescription(getResources().getString(R.string.logo_desc));

        db = new SQLiteHandler(getApplicationContext());

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        LayoutInflater inflater = getLayoutInflater();
        listHeaderView = inflater.inflate(R.layout.header_list, null, false);


        //ImageView profile = (ImageView)listHeaderView.findViewById(R.id.profile_picture);
        name = (TextView) listHeaderView.findViewById(R.id.headername);
        profilepic = (ImageView) listHeaderView.findViewById(R.id.profile);


        user = session.getUserDetails();
        profilepic.setImageBitmap(mprofilepicture);
        name.setText(user.get(SessionManager.KEY_NAME));
        mDrawerList.addHeaderView(listHeaderView);

        List<ItemObject> listViewItems = new ArrayList<ItemObject>();
        listViewItems.add(new ItemObject("Notes", R.drawable.notes));
        listViewItems.add(new ItemObject("Deleted Notes", R.drawable.delete));
        listViewItems.add(new ItemObject("Logout", R.drawable.logout));

        adapter = new CustomAdapter(this, listViewItems);


        mDrawerList.setAdapter(new CustomAdapter(this, listViewItems));

        mDrawerToggle = new ActionBarDrawerToggle(HomepageActivity.this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close) {

            /**
             * Called when a drawer has settled in a completely closed state.
             */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getSupportActionBar().setTitle(mTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /**
             * Called when a drawer has settled in a completely open state.
             */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getSupportActionBar().setTitle(mDrawerTitle);
                session = new SessionManager(getApplicationContext());
                user = session.getUserDetails();
                profilepic.setImageBitmap(StringToBitMap(user.get(SessionManager.KEY_PROFILEPIC)));
                name.setText(user.get(SessionManager.KEY_NAME));
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };

        // Set the drawer toggle as the DrawerListener
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.setDrawerIndicatorEnabled(true);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // make Toast when click
                selectItemFragment(position);
            }
        });

        Fragment fragment = new Noteslist();
        Bundle bundle = new Bundle();
        bundle.putString("aim", "0");
        fragment.setArguments(bundle);
        FragmentManager fragmentManager = this.getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.main_fragment_container, fragment).commit();
        HomepageActivity.this.invalidateOptionsMenu();
    }

    @Override
    public void onBackPressed() {
        //  Toast.makeText(getApplicationContext(), backtrack, Toast.LENGTH_LONG).show();

       if (exit) {
            exit = false;
            finish(); // finish activity
        }

        if(backtrack.equals("nothing")) {
            Toast.makeText(getApplicationContext(), "Press back again to exit!", Toast.LENGTH_LONG).show();
            this.backtrack = "everything";
            exit = true;

        }
        if(backtrack.equals("notelist")) {
            Fragment fragment = new Noteslist();
            setTitle("Select Class");
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.main_fragment_container, fragment).commit();
            exit = true;

        }
        if(backtrack.equals("deletelist")) {
            Fragment fragment = new Deletedlist();
            setTitle("Select Class");
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.main_fragment_container, fragment).commit();
            exit = true;

        }




    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.


        this.m = menu;
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_logout, menu);

        return super.onCreateOptionsMenu(menu);

    }
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement


            if(id == R.id.logout){

                new AlertDialog.Builder(this)
                        .setTitle("Logout")
                        .setMessage("Are you sure you want to Logout?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {


                                session.logoutUser();
                                db.deleteUsers();
                                MainActivity.revokeGplusAccess();

                            }
                        })
                        .setIcon(R.drawable.logo)
                        .show();
                return true;
            }

        if(id == R.id.action_camera_image){

            selectImage();
            return true;
        }

        if(id == R.id.action_note_add){


            Fragment fragment = new Addnote();
            Bundle bundle = new Bundle();
            bundle.putString("aim", image_exist + "");
            fragment.setArguments(bundle);
            FragmentManager fragmentManager = this.getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.main_fragment_container, fragment).addToBackStack("tag").commit();
            HomepageActivity.this.invalidateOptionsMenu();
            return true;
        }

        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    private void selectItemFragment(int position){

        Fragment fragment = null;
        FragmentManager fragmentManager = getSupportFragmentManager();
        switch(position) {
            default:
            case 0:
                //fragment = new Profile();
                break;
            case 1:

                fragment = new Noteslist();
                Bundle bundle = new Bundle();
                bundle.putString("aim", "0");
                fragment.setArguments(bundle);
                fragmentManager.beginTransaction().replace(R.id.main_fragment_container, fragment).addToBackStack("tag").commit();
                mDrawerList.setItemChecked(position, true);
                setTitle(titles[position]);
                mDrawerLayout.closeDrawer(mDrawerList);

                break;
            case 2:
                fragment = new Deletedlist();
                Bundle bundle1 = new Bundle();
                bundle1.putString("aim", "0");
                fragment.setArguments(bundle1);
                HomepageActivity.backtrack = "notelist";
                HomepageActivity.exit = false;
                fragmentManager.beginTransaction().replace(R.id.main_fragment_container, fragment).addToBackStack( "tag" ).commit();
                mDrawerList.setItemChecked(position, true);
                setTitle(titles[position]);
                mDrawerLayout.closeDrawer(mDrawerList);

                break;
            case 3:



                break;
        }

    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getSupportActionBar().setTitle(mTitle);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }


    public String BitMapToString(Bitmap bitmap) {
        ByteArrayOutputStream ByteStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, ByteStream);
        byte[] b = ByteStream.toByteArray();
        String temp = Base64.encodeToString(b, Base64.DEFAULT);
        Log.d("PROFILEPIC", temp);
        return temp;
    }

    public Bitmap StringToBitMap(String encodedString) {
        try {
            byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }

    private void selectImage() {
        final CharSequence[] items = {"Take Photo", "Choose from Library",
                "Cancel"};

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take Photo")) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, REQUEST_CAMERA);
                } else if (items[item].equals("Choose from Library")) {
                    Intent intent = new Intent(
                            Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(
                            Intent.createChooser(intent, "Select File"),
                            SELECT_FILE);
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE) {
                onSelectFromGalleryResult(data);
            } else if (requestCode == REQUEST_CAMERA)
              onCaptureImageResult(data);
        }
    }



    private void onCaptureImageResult(Intent data) {
        Bitmap bm = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

        File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");


        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        bitmap = bm;
        profileimage = getBytes(bm);
        image_exist = 1;
        Fragment fragment = new Addnote();
        Bundle bundle = new Bundle();
        bundle.putString("aim", image_exist + "");
        bundle.putByteArray("image", profileimage);
        fragment.setArguments(bundle);
        FragmentManager fragmentManager = this.getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.main_fragment_container, fragment).commit();
        HomepageActivity.this.invalidateOptionsMenu();
    }

    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {

        String selectedImagePath = getPathFromCameraData(data, this);

        Bitmap bm;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(selectedImagePath, options);
        final int REQUIRED_SIZE = 200;
        int scale = 1;
        while (options.outWidth / scale / 2 >= REQUIRED_SIZE
                && options.outHeight / scale / 2 >= REQUIRED_SIZE)
            scale *= 2;
        options.inSampleSize = scale;
        options.inJustDecodeBounds = false;
        bm = BitmapFactory.decodeFile(selectedImagePath, options);
        bitmap = bm;
        profileimage = getBytes(bm);
        image_exist = 1;

        Fragment fragment = new Addnote();
        Bundle bundle = new Bundle();
        bundle.putString("aim", image_exist+"");
        bundle.putByteArray("image", profileimage);
        fragment.setArguments(bundle);
        FragmentManager fragmentManager = this.getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.main_fragment_container, fragment).commit();
        HomepageActivity.this.invalidateOptionsMenu();
    }

    public static String getPathFromCameraData(Intent data, Context context) {
        Uri selectedImage = data.getData();
        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(selectedImage,
                filePathColumn, null, null, null);
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String picturePath = cursor.getString(columnIndex);
        cursor.close();
        return picturePath;
    }



    public static Bitmap getImage(byte[] image) {
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }
    public static byte[] getBytes(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
        return stream.toByteArray();
    }


}
