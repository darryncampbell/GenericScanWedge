package com.zebra.datawedgelite.datawedgelite;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ListView;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    static final String LOG_CATEGORY = "DWAPI Lite";
    private ArrayList<Profile> profiles = new ArrayList<>();
    private ProfilesListAdapter profilesListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        if (getIntent().getBooleanExtra("finish", false))
        {
            finish();
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                profilesListAdapter.add(new Profile("New Profile", false));
                profilesListAdapter.notifyDataSetChanged();
                saveProfiles(profiles, getApplicationContext());
            }
        });

        //  Populate the profiles available
//        profiles.add(new Profile("Profile0 (Default)"));
//        profiles.add(new Profile("Barcode Disabled"));


    }

    @Override
    protected void onResume()
    {
        super.onResume();
        Log.v(LOG_CATEGORY, "Resume");
        //  Read the profiles here and populate the UI
        try {
            FileInputStream fis = getApplicationContext().openFileInput(getResources().getString(R.string.profile_file_name));
            ObjectInputStream is = null;
            is = new ObjectInputStream(fis);
            ArrayList<Profile> profiles = (ArrayList<Profile>) is.readObject();
            if (profiles != null && profiles.size() > 0)
            {
                this.profiles = profiles;
            }
            else
            {
                profiles.add(new Profile("Profile0 (Default)", true));
            }
            ListView profilesListView = (ListView)findViewById(R.id.profiles_list);
            profilesListAdapter = new ProfilesListAdapter(this, this.profiles);
            profilesListView.setAdapter(profilesListAdapter);
            is.close();
            fis.close();

        } catch (IOException e) {
            profiles.add(new Profile("Profile0 (Default)", true));
            ListView profilesListView = (ListView)findViewById(R.id.profiles_list);
            profilesListAdapter = new ProfilesListAdapter(this, this.profiles);
            profilesListView.setAdapter(profilesListAdapter);
        }catch (ClassNotFoundException e) {
            e.printStackTrace();
        }finally
        {
        }

    }

    public static void saveProfiles(ArrayList<Profile> profiles, Context context)
    {
        try {
            FileOutputStream fos = context.openFileOutput(context.getResources().getString(R.string.profile_file_name), Context.MODE_PRIVATE);
            ObjectOutputStream os = new ObjectOutputStream(fos);
            os.writeObject(profiles);
            os.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
