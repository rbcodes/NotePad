package com.example.rishabh.notepad;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;


public class Editnote extends Fragment {

    ImageView noteimage;
    EditText title;
    EditText note;
    Button save;
    private SQLiteHandler db;
    SessionManager session;
    HashMap<String, String> user;
    static byte[] profileimage;
    int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    String calledfragment;
    String profilephoto;
    private Bitmap bitmap;
    byte[] image;
    static int noteid;
    static String notetittle;
    static String notetext;
    static String note_image_exist;
    static byte[] imagerecieved;
    private LinearLayout llLayout;
    private Activity faActivity;

        @Override
        public View onCreateView (LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState){

            faActivity = (Activity) super.getActivity();
            llLayout = (LinearLayout) inflater.inflate(R.layout.fragment_editnote, container, false);
            super.onCreate(savedInstanceState);

            session = new SessionManager(getActivity());
            user = session.getUserDetails();
            noteimage = (ImageView) llLayout.findViewById(R.id.noteimage);
            title = (EditText) llLayout.findViewById(R.id.title);
            note = (EditText) llLayout.findViewById(R.id.note);
            save =  (Button) llLayout.findViewById(R.id.save);
            db = new SQLiteHandler(getActivity());

            Bundle bundle = this.getArguments();
            String valuesrecieved = bundle.getString("aim");


            String[] array = valuesrecieved.split("values");
            calledfragment = array[0];
            noteid = Integer.parseInt(array[1]);
            notetittle = array[2];
            notetext = array[3];
            note_image_exist = array[4];
            if(calledfragment.equals("deleted")) {
                getActivity().setTitle("Edit Deleted Note");

            }
            else if(calledfragment.equals("note")) {
                getActivity().setTitle("Edit Note");
            }

            if(Integer.parseInt(note_image_exist) == 0)
            {
                title.setText(notetittle);
                note.setText(notetext);
                noteimage.setVisibility(View.GONE);
            }
            else
            {
                imagerecieved = bundle.getByteArray("image");
                title.setText(notetittle);
                note.setText(notetext);
                noteimage.setImageBitmap(getImage(imagerecieved));
                profileimage = imagerecieved;


            }



            noteimage.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    selectImage();
                }
            });

            save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    Calendar calendar = Calendar.getInstance();
                    java.util.Date now = calendar.getTime();
                    java.sql.Timestamp currentTimestamp = new java.sql.Timestamp(now.getTime());

                    if (title.getText().toString().equals("") || note.getText().toString().equals("")) {
                        Toast.makeText(getActivity(), "Please enter Tittle and Note", Toast.LENGTH_LONG).show();
                    } else if(calledfragment.equals("note")){

                        db.updatenote(noteid + "", title.getText().toString(), note.getText().toString(), profileimage, Integer.parseInt(note_image_exist), currentTimestamp + "");

                        new android.support.v7.app.AlertDialog.Builder(getActivity())
                                .setTitle("Success")
                                .setMessage("Your note has been updated successfully.")
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        Fragment fragment = null;
                                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                        fragment = new Noteslist();
                                        Bundle bundle = new Bundle();
                                        bundle.putString("aim", "0");
                                        fragment.setArguments(bundle);
                                        fragmentManager.beginTransaction().replace(R.id.main_fragment_container, fragment).commit();
                                    }
                                })
                                .setIcon(R.drawable.logo)
                                .show();


                    }
                    else if(calledfragment.equals("deleted"))
                    {
                        new android.support.v7.app.AlertDialog.Builder(getActivity())
                                .setTitle("Are you sure?")
                                .setMessage("Updating a deleted note, will restore the note. Are you sure you want to update and restore the note?")
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {

                                        Calendar calendar = Calendar.getInstance();
                                        java.util.Date now = calendar.getTime();
                                        java.sql.Timestamp currentTimestamp = new java.sql.Timestamp(now.getTime());

                                        db.addnote(user.get(SessionManager.KEY_ID), title.getText().toString(), note.getText().toString(), profileimage, Integer.parseInt(note_image_exist), currentTimestamp + "", currentTimestamp + "");
                                        db.deletenotefinal(noteid);
                                        new android.support.v7.app.AlertDialog.Builder(getActivity())
                                                .setTitle("Success")
                                                .setMessage("Your note has been restored successfully.")
                                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        Fragment fragment = null;
                                                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                                        fragment = new Noteslist();
                                                        Bundle bundle = new Bundle();
                                                        bundle.putString("aim", "0");
                                                        fragment.setArguments(bundle);
                                                        fragmentManager.beginTransaction().replace(R.id.main_fragment_container, fragment).commit();
                                                    }
                                                })
                                                .setIcon(R.drawable.logo)
                                                .show();

                                    }
                                })
                                .setIcon(R.drawable.logo)
                                .show();
                    }


                }
            });

            setHasOptionsMenu(true);
            return llLayout;
        }


    private void selectImage() {
        final CharSequence[] items = {"Take Photo", "Choose from Library",
                "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
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
        noteimage.setImageBitmap(bm);
       // Toast.makeText(getActivity(), bitmap+"", Toast.LENGTH_LONG).show();

    }

    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {

        String selectedImagePath = getPathFromCameraData(data, getActivity());

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
        noteimage.setImageBitmap(bm);

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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_editnote, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if(id == R.id.action_delete_note){

            if(calledfragment.equals("deleted"))
            {
                new android.support.v7.app.AlertDialog.Builder(getActivity())
                        .setTitle("Delete")
                        .setMessage("Are you sure you want to delete this note permanently?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                db.deletenotefinal(noteid);

                                Fragment fragment = null;
                                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                fragment = new Deletedlist();
                                Bundle bundle = new Bundle();
                                bundle.putString("aim", "0");
                                fragment.setArguments(bundle);
                                fragmentManager.beginTransaction().replace(R.id.main_fragment_container, fragment).commit();

                            }
                        })
                        .setIcon(R.drawable.logo)
                        .show();

            }
            else if(calledfragment.equals("note")) {

                new android.support.v7.app.AlertDialog.Builder(getActivity())
                        .setTitle("Delete")
                        .setMessage("Are you sure you want to delete this note?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                db.deletenote(noteid);

                                Fragment fragment = null;
                                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                fragment = new Noteslist();
                                Bundle bundle = new Bundle();
                                bundle.putString("aim", "0");
                                fragment.setArguments(bundle);
                                fragmentManager.beginTransaction().replace(R.id.main_fragment_container, fragment).commit();

                            }
                        })
                        .setIcon(R.drawable.logo)
                        .show();

            }

            return true;
        }


        if(id == R.id.logout){


            new android.support.v7.app.AlertDialog.Builder(getActivity())
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
        return super.onOptionsItemSelected(item);
    }



}