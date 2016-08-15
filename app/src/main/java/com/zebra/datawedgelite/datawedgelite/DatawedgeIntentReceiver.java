package com.zebra.datawedgelite.datawedgelite;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;

/**
 * Created by darry on 12/08/2016.
 */
public class DatawedgeIntentReceiver extends BroadcastReceiver {

    static final String LOG_TAG = "DWAPI Lite";

    @Override
    public void onReceive(Context context, Intent intent) {
        //Log.d(LOG_TAG, "Received Broadcast Intent: " + intent.getAction());
        //  Just forward any intent we receive to the intent service unless we're running
        //  on a Zebra device
        if (!android.os.Build.MANUFACTURER.equalsIgnoreCase("Zebra Technologies") &&
                !Build.MANUFACTURER.equalsIgnoreCase("Motorola Solutions"))
        {

            try {
                ArrayList<Profile> profiles = MainActivity.readProfiles(context);
                if (profiles != null)
                {
                    Profile activeProfile = null;
                    int activeProfilePosition = -1;
                    for (int i = 0; i < profiles.size(); i++)
                        if (profiles.get(i).getProfileEnabled()) {
                            activeProfile = profiles.get(i);
                            activeProfilePosition = i;
                            break;
                        }
                    if (activeProfile == null)
                        Log.e(LOG_TAG, "No Active profile currently defined and enabled.  No barcode will be scanned");
                    else
                    {
                        //  We have successfully read in the configured profiles, find the active one
                        Log.d(LOG_TAG, "Active Profile: " + activeProfile.getName());
                        Intent newIntent = new Intent(context, DatawedgeLiteService.class);
                        newIntent.setAction(intent.getAction());
                        if (intent.getExtras() != null)
                            newIntent.putExtras(intent.getExtras());
                        newIntent.putExtra("activeProfilePosition", activeProfilePosition);
                        newIntent.putExtra("profiles", profiles);
                        //newIntent.putExtra
                        context.startService(newIntent);
                    }
                }
            } catch (IOException e) {
                //e.printStackTrace();
                Log.e(LOG_TAG, "Unable to read DataWedge profile, please configure an active profile");
            }catch (ClassNotFoundException e) {
                //e.printStackTrace();
                Log.e(LOG_TAG, "Unable to read DataWedge profile, please configure an active profile");
            }

        }
    }
}
