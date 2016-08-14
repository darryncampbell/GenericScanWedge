package com.zebra.datawedgelite.datawedgelite;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;

import static com.zebra.datawedgelite.datawedgelite.R.string.enumerate_scanners_key;

public class DatawedgeLiteService extends IntentService {

    static final String LOG_CATEGORY = "DWAPI Lite";

    //  Supported Intents that this service processes (these come from applications requesting to open the scanner)
    private static final String ACTION_SOFTSCANTRIGGER = "com.symbol.datawedge.api.ACTION_SOFTSCANTRIGGER";
    private static final String ACTION_SCANNERINPUTPLUGIN = "com.symbol.datawedge.api.ACTION_SCANNERINPUTPLUGIN";
    private static final String ACTION_ENUMERATESCANNERS = "com.symbol.datawedge.api.ACTION_ENUMERATESCANNERS";
    private static final String ACTION_SETDEFAULTPROFILE = "com.symbol.datawedge.api.ACTION_SETDEFAULTPROFILE";
    private static final String ACTION_RESETDEFAULTPROFILE = "com.symbol.datawedge.api.ACTION_RESETDEFAULTPROFILE";
    private static final String ACTION_SWITCHTOPROFILE = "com.symbol.datawedge.api.ACTION_SWITCHTOPROFILE";

    //  Parameters associated with the application actions
    private static final String EXTRA_PARAMETER = "com.symbol.datawedge.api.EXTRA_PARAMETER";
    private static final String EXTRA_PROFILENAME = "com.symbol.datawedge.api.EXTRA_PROFILENAME";

    public DatawedgeLiteService() {
        super("DatawedgeLiteService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        //Context ctx = getApplicationContext();
        Log.v(LOG_CATEGORY, "onHandleIntent");
        if (intent != null) {
            final String action = intent.getAction();
            final ArrayList<Profile> profiles = (ArrayList<Profile>) intent.getSerializableExtra("profiles");
            final int activeProfilePosition = intent.getIntExtra("activeProfilePosition", -1);
            Profile activeProfile = profiles.get(activeProfilePosition);
            if (ACTION_SOFTSCANTRIGGER.equals(action))
            {
                final String param = intent.getStringExtra(EXTRA_PARAMETER);
                handleActionSoftScanTrigger(param, activeProfile);
            }
            else if (ACTION_SCANNERINPUTPLUGIN.equals(action))
            {
                final String param = intent.getStringExtra(EXTRA_PARAMETER);
                handleScannerInputPlugin(param, profiles, activeProfilePosition);
            }
            else if (ACTION_ENUMERATESCANNERS.equals(action))
            {
                //  No parameters
                handleEnumerateScanners();
            }
            else if (ACTION_SETDEFAULTPROFILE.equals(action))
            {
                final String param = intent.getStringExtra(EXTRA_PROFILENAME);
                handleSetDefaultProfile(param, profiles, activeProfilePosition);
            }
            else if (ACTION_RESETDEFAULTPROFILE.equals(action))
            {
                final String param = intent.getStringExtra(EXTRA_PROFILENAME);
                handleResetDefaultProfile(param);
            }
            else if (ACTION_SWITCHTOPROFILE.equals(action))
            {
                final String param = intent.getStringExtra(EXTRA_PROFILENAME);
                handleSwitchToProfile(param, profiles, activeProfilePosition);
            }
        }

    }

    private void handleActionSoftScanTrigger(String param, Profile activeProfile)
    {
        if (param.equals("START_SCANNING"))
        {
            if (!activeProfile.isBarcodeInputEnabled())
            {
                Log.d(LOG_CATEGORY, "Barcode scanning is disabled in the current profile");
                return;
            }

            if (activeProfile.getScanningEngine() == Profile.ScanningEngine.SCANNING_ENGINE_ZXING) {
                Intent zxingActivity = new Intent(this, ZxingActivity.class);
                zxingActivity.putExtra("activeProfile", activeProfile);
                startActivity(zxingActivity);
            }
            else if (activeProfile.getScanningEngine() == Profile.ScanningEngine.SCANNING_ENGINE_GOOGLE_VISION) {
                Intent googleVisionActivity = new Intent(this, GoogleVisionBarcodeActivity.class);
                googleVisionActivity.putExtra("activeProfile", activeProfile);
                startActivity(googleVisionActivity);
            }
        }
        else if (param.equals("STOP_SCANNING"))
        {
            //  Stop scanning does not make sense for ZXing or Google Vision API
            Log.w(LOG_CATEGORY, "STOP_SCANNING is not implemented for Non Zebra devices");
        }
        else if (param.equals("TOGGLE_SCANNING"))
        {
            //  Toggle scanning does not make sense for ZXing or Google Vision API
            Log.w(LOG_CATEGORY, "TOGGLE_SCANNING is not implemented for Non Zebra devices");
        }
        else
        {
            //  Unrecognised parameter
            Log.w(LOG_CATEGORY, "Unrecognised parameter to SoftScanTrigger: " + param);
        }
    }

    private void handleScannerInputPlugin(String param, ArrayList<Profile> profiles, int activeProfilePosition) {
        if (param.equals("ENABLE_PLUGIN"))
        {
            profiles.get(activeProfilePosition).setBarcodeInputEnabled(true);
        }
        else if (param.equals("DISABLE_PLUGIN"))
        {
            profiles.get(activeProfilePosition).setBarcodeInputEnabled(false);
        }
        else
        {
            //  Unrecognised paramter
            Log.w(LOG_CATEGORY, "Unrecognised parameter to ScannerInputPlugin: " + param);
        }
        MainActivity.saveProfiles(profiles, getApplicationContext());
    }

    private void handleEnumerateScanners() {
        Intent enumerateBarcodesIntent = new Intent();
        enumerateBarcodesIntent.setAction(getResources().getString(R.string.enumerate_scanners_action));
        String[] scanner_list = new String[1];
        scanner_list[0] = "CAMERA";
        Bundle bundle = new Bundle();
        bundle.putStringArray(getResources().getString(R.string.enumerate_scanners_key), scanner_list);
        enumerateBarcodesIntent.putExtras(bundle);
        sendBroadcast(enumerateBarcodesIntent);

        //  todo test this (http://techdocs.zebra.com/datawedge/5-0/guide/api/)
        //  todo - add OS license to the online repo
    }

    private void handleSetDefaultProfile(String profileName, ArrayList<Profile> profiles, int activeProfileIndex)
    {
        //  I have not implemented default profiles because we do not switch profiles dynamically
        //  depending on which application is shown so we don't have a notion of a 'default profile',
        //  it's just whichever profile is enabled.
        Log.w(LOG_CATEGORY, "Default Profile is not implemented in the Datawedge Lite Service.  Switching to specified profile");
        handleSwitchToProfile(profileName, profiles, activeProfileIndex);
        //  todo test this
    }

    private void handleResetDefaultProfile(String profileName)
    {
        //  I have not implemented default profiles because we do not switch profiles dynamically
        //  depending on which application is shown so we don't have a notion of a 'default profile',
        //  it's just whichever profile is enabled.
        Log.w(LOG_CATEGORY, "Default Profile is not implemented in the Datawedge Lite Service.");

        //  Another thing, why does this this method take a profileName paramter?  I think the online docs are wrong.
    }

    private void handleSwitchToProfile(String param, ArrayList<Profile> profiles, int activeProfileIndex)
    {
        Log.d(LOG_CATEGORY, "Switching to profile: " + param);
        //  Change the enabled profile to the specified profile name
        boolean bFoundProfile = false;
        for (int i = 0; i < profiles.size(); i++)
        {
            if (profiles.get(i).getName().equalsIgnoreCase(param))
            {
                profiles.get(activeProfileIndex).setProfileEnabled(false);
                profiles.get(i).setProfileEnabled(true);
            }
        }
        if (bFoundProfile)
            MainActivity.saveProfiles(profiles, getApplicationContext());
        else
        {
            Log.w(LOG_CATEGORY, "Unrecognised profile to switch to: " + param);
        }
        //  todo - test this
    }

}
