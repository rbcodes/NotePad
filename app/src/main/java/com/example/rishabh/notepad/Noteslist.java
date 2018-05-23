package com.example.rishabh.notepad;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;


public class Noteslist extends Fragment {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private static String LOG_TAG = "CardViewActivity";
    private SQLiteHandler db;
    private RelativeLayout llLayout;
    private Activity faActivity;
    SessionManager session;
    LinearLayout nonotes;
    HashMap<String, String> user;
    ArrayList<HashMap<String ,String>> classnames = new ArrayList<HashMap<String ,String>>();
    ArrayList<HashMap<String ,byte[]>> classimages = new ArrayList<HashMap<String ,byte[]>>();
    ArrayList<String> notetittle = new ArrayList<String>();
    ArrayList<String> notetext = new ArrayList<String>();
    ArrayList<byte[]> noteimage = new ArrayList<byte[]>();
    ArrayList<String> noteimage_exist = new ArrayList<String>();
    ArrayList<String> noteid = new ArrayList<String>();
    private Boolean isFabOpen = false;
    private FloatingActionButton fab, fab1, fab2;
    private Animation fab_open, fab_close, rotate_forward, rotate_backward;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

            faActivity  = (Activity)super.getActivity();
            getActivity().setTitle("Notes");
            llLayout    = (RelativeLayout)inflater.inflate(R.layout.fragment_noteslist, container, false);
            nonotes = (LinearLayout)llLayout.findViewById(R.id.nonotes);
            super.onCreate(savedInstanceState);
             db = new SQLiteHandler(getActivity());
            mRecyclerView = (RecyclerView)llLayout.findViewById(R.id.my_recycler_view);
            mRecyclerView.setHasFixedSize(true);
            mLayoutManager = new LinearLayoutManager(getActivity());
            mRecyclerView.setLayoutManager(mLayoutManager);
            mAdapter = new MyRecyclerViewAdapter(getDataSet());
            mRecyclerView.setAdapter(mAdapter);
            session = new SessionManager(getActivity());
            user = session.getUserDetails();
            HomepageActivity.backtrack = "nothing";
            HomepageActivity.exit = false;
            // Code to Add an item with default animation
            //((MyRecyclerViewAdapter) mAdapter).addItem(obj, index);

            // Code to remove an item with default animation
            //((MyRecyclerViewAdapter) mAdapter).deleteItem(index);


        return llLayout;
        }

        @Override
        public void onResume () {
            super.onResume();
            ((MyRecyclerViewAdapter) mAdapter).setOnItemClickListener(new MyRecyclerViewAdapter
                    .MyClickListener() {
                @Override
                public void onItemClick(int position, View v) {
                    Log.i(LOG_TAG, " Clicked on Item " + position);
                    int id = position;
                    Fragment fragment = new Editnote();
                    Bundle bundle = new Bundle();
                    bundle.putString("aim", "note"+ "values" +  noteid.get(position) + "values" + notetittle.get(id) + "values" + notetext.get(id) + "values" + noteimage_exist.get(id));
                    bundle.putByteArray("image", noteimage.get(id));
                    fragment.setArguments(bundle);
                    HomepageActivity.backtrack = "notelist";
                    HomepageActivity.exit = false;
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.main_fragment_container, fragment).addToBackStack( "tag" ).commit();
                }
            });
        }

        private ArrayList<DataObject> getDataSet () {
            ArrayList results = new ArrayList<DataObject>();

           // db.getnotes(user.get(SessionManager.KEY_ID));
            session = new SessionManager(getActivity());
            user = session.getUserDetails();
            String id = user.get(SessionManager.KEY_ID);
            classnames = db.getnotes(id);
            classimages = db.getnotesimage(id);
            for(int d = 0; d<classnames.size();d++)
            {
                HashMap<String ,String> classname;
                HashMap<String ,byte[]> classimage;
                classname = classnames.get(d);


                if(classname.get("note_exist").equals("true")) {
                    if(mRecyclerView.getVisibility() == View.GONE)
                    {
                        mRecyclerView.setVisibility(View.VISIBLE);
                    }
                    if(nonotes.getVisibility() == View.VISIBLE)
                    {
                        nonotes.setVisibility(View.GONE);
                    }
                    classimage = classimages.get(d);
                    noteid.add(classname.get("note_id"));
                    notetittle.add(classname.get("note_header"));
                    notetext.add(classname.get("note_text"));
                    noteimage.add(classimage.get("note_image"));
                    noteimage_exist.add(classname.get("image_exist"));
                    DataObject obj = new DataObject(classname.get("note_header"), classname.get("note_text"), classimage.get("note_image"), Integer.parseInt(classname.get("image_exist")));

                    results.add(d, obj);
                }
                else
                {
                    if(mRecyclerView.getVisibility() == View.VISIBLE)
                    {
                        mRecyclerView.setVisibility(View.GONE);
                    }
                    if(nonotes.getVisibility() == View.GONE)
                    {
                        nonotes.setVisibility(View.VISIBLE);
                    }
                }
            }
            return results;
        }
    }
