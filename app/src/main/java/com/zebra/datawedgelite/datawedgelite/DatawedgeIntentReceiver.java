package com.zebra.datawedgelite.datawedgelite;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;

/**
 * Created by darry on 12/08/2016.
 */
public class DatawedgeIntentReceiver extends BroadcastReceiver {

    static final String LOG_CATEGORY = "Datawedge Lite Service";

    @Override
    public void onReceive(Context context, Intent intent) {
        //Log.d(LOG_CATEGORY, "Received Broadcast Intent: " + intent.getAction());
        //  Just forward any intent we receive to the intent service unless we're running
        //  on a Zebra device
        if (!android.os.Build.MANUFACTURER.equalsIgnoreCase("Zebra Technologies") &&
                !Build.MANUFACTURER.equalsIgnoreCase("Motorola Solutions"))
        {

            //  TESTING
            try {
                FileInputStream fis = context.openFileInput("testFile");
                ObjectInputStream is = null;
                is = new ObjectInputStream(fis);
                ArrayList<Profile> profiles = (ArrayList<Profile>) is.readObject();
                if (profiles != null)
                {
                    //Toast.makeText(context, "Read Profile: " + profiles.get(0).getName(), Toast.LENGTH_SHORT).show();
                    Log.d(LOG_CATEGORY, "Read Profile: " + profiles.get(0).getName());
                }
                is.close();
                fis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            //  END TESTING

            Intent newIntent = new Intent(context, DatawedgeLiteService.class);
            newIntent.setAction(intent.getAction());
            newIntent.putExtras(intent.getExtras());
            context.startService(newIntent);

        }
    }
}
