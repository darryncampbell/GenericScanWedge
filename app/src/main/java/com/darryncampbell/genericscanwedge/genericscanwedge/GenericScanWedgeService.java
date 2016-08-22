package com.darryncampbell.genericscanwedge.genericscanwedge;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;

//  This service handles DataWedge API intents from the calling application
public class GenericScanWedgeService extends IntentService {

    static final String LOG_TAG = "Generic Scan Wedge";

    //  As defined by the DataWedge API:
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

    public GenericScanWedgeService() {
        super("GenericScanWedgeService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        //Context ctx = getApplicationContext();
        Log.v(LOG_TAG, "onHandleIntent");
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

    //  Calling application is asking to initiate or stop a scan in progress
    private void handleActionSoftScanTrigger(String param, Profile activeProfile)
    {
        if (param.equals("START_SCANNING"))
        {
            if (!activeProfile.isBarcodeInputEnabled())
            {
                Log.d(LOG_TAG, "Barcode scanning is disabled in the current profile");
                return;
            }

            //  If any more scan engines are supported they need adding here.
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
            Log.w(LOG_TAG, "STOP_SCANNING is not implemented for Non Zebra devices");
        }
        else if (param.equals("TOGGLE_SCANNING"))
        {
            //  Toggle scanning does not make sense for ZXing or Google Vision API
            Log.w(LOG_TAG, "TOGGLE_SCANNING is not implemented for Non Zebra devices");
        }
        else
        {
            //  Unrecognised parameter
            Log.w(LOG_TAG, "Unrecognised parameter to SoftScanTrigger: " + param);
        }
    }

    //  Calling application is asking to enable or disable scanning in the active profile
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
            //  Unrecognised parameter
            Log.w(LOG_TAG, "Unrecognised parameter to ScannerInputPlugin: " + param);
        }
        MainActivity.saveProfiles(profiles, getApplicationContext());
    }

    //  Calling application is asking to enumerate the available scanners.  For ZXing or Google Barcode API we just return the camera
    private void handleEnumerateScanners() {
        Intent enumerateBarcodesIntent = new Intent();
        enumerateBarcodesIntent.setAction(getResources().getString(R.string.enumerate_scanners_action));
        String[] scanner_list = new String[1];
        scanner_list[0] = "Camera Scanner";
        Bundle bundle = new Bundle();
        bundle.putStringArray(getResources().getString(R.string.enumerate_scanners_key), scanner_list);
        enumerateBarcodesIntent.putExtras(bundle);
        sendBroadcast(enumerateBarcodesIntent);
    }

    private void handleSetDefaultProfile(String profileName, ArrayList<Profile> profiles, int activeProfileIndex)
    {
        //  I have not implemented default profiles because we do not switch profiles dynamically
        //  depending on which application is shown so we don't have a notion of a 'default profile',
        //  it's just whichever profile is enabled.
        Log.w(LOG_TAG, "Default Profile is not implemented in the generic scan wedge Service.  Switching to specified profile");
        handleSwitchToProfile(profileName, profiles, activeProfileIndex);
    }

    private void handleResetDefaultProfile(String profileName)
    {
        //  I have not implemented default profiles because we do not switch profiles dynamically
        //  depending on which application is shown so we don't have a notion of a 'default profile',
        //  it's just whichever profile is enabled.
        Log.w(LOG_TAG, "Default Profile is not implemented in the generic scan wedge Service.");

        //  Another thing, why does this this method take a profileName paramter?  I think the online docs are wrong.
    }

    //  Calling application has requested to switch to a specific profile
    private void handleSwitchToProfile(String param, ArrayList<Profile> profiles, int activeProfileIndex)
    {
        Log.d(LOG_TAG, "Switching to profile: " + param);
        //  Change the enabled profile to the specified profile name
        boolean bFoundProfile = false;
        for (int i = 0; i < profiles.size(); i++)
        {
            if (profiles.get(i).getName().equalsIgnoreCase(param))
            {
                profiles.get(activeProfileIndex).setProfileEnabled(false);
                profiles.get(i).setProfileEnabled(true);
                bFoundProfile = true;
            }
        }
        if (bFoundProfile)
            MainActivity.saveProfiles(profiles, getApplicationContext());
        else
        {
            Log.w(LOG_TAG, "Unrecognised profile to switch to: " + param);
        }
    }
}
