package com.zebra.datawedgelite.datawedgelite;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
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
        Log.d(LOG_CATEGORY, "onHandleIntent");
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_SOFTSCANTRIGGER.equals(action))
            {
                final String param = intent.getStringExtra(EXTRA_PARAMETER);
                handleActionSoftScanTrigger(param);
            }
            else if (ACTION_SCANNERINPUTPLUGIN.equals(action))
            {
                final String param = intent.getStringExtra(EXTRA_PARAMETER);
                handleScannerInputPlugin(param);
            }
            else if (ACTION_ENUMERATESCANNERS.equals(action))
            {
                //  No parameters
                handleEnumerateScanners();
            }
            else if (ACTION_SETDEFAULTPROFILE.equals(action))
            {
                final String param = intent.getStringExtra(EXTRA_PROFILENAME);
                handleSetDefaultProfile(param);
            }
            else if (ACTION_RESETDEFAULTPROFILE.equals(action))
            {
                final String param = intent.getStringExtra(EXTRA_PROFILENAME);
                handleResetDefaultProfile(param);
            }
            else if (ACTION_SWITCHTOPROFILE.equals(action))
            {
                final String param = intent.getStringExtra(EXTRA_PROFILENAME);
                handleSwitchToProfile(param);
            }
        }

    }

    private void handleActionSoftScanTrigger(String param)
    {

        if (param.equals("START_SCANNING"))
        {
            Intent zxingActivity = new Intent(this, ZxingActivity.class);
            //  todo - the action would come from the profile.  Check it's sensible
            zxingActivity.putExtra("intentAction", "com.zebra.datawedgecordova.ACTION");
            startActivity(zxingActivity);




        }
        else if (param.equals("STOP_SCANNING"))
        {

        }
        else if (param.equals("TOGGLE_SCANNING"))
        {

        }
        else
        {
            //  Unrecognised parameter
            Log.w(LOG_CATEGORY, "Unrecognised parameter to SoftScanTrigger: " + param);
        }
    }

    private void handleScannerInputPlugin(String param) {
        if (param.equals("ENABLE_PLUGIN"))
        {

        }
        else if (param.equals("DISABLE_PLUGIN"))
        {

        }
        else
        {
            //  Unrecognised paramter
            Log.w(LOG_CATEGORY, "Unrecognised parameter to ScannerInputPlugin: " + param);
        }
    }

    private void handleEnumerateScanners() {
    }

    private void handleSetDefaultProfile(String profileName)
    {
        //  TODO Set Default profile name
    }

    private void handleResetDefaultProfile(String profileName)
    {
        //  TODO Reset the default profile back to profile0.  Presumably profileName should be ignored and the online docs are wrong?
    }

    private void handleSwitchToProfile(String param)
    {
        Log.d(LOG_CATEGORY, "Switching to profile: " + param);
        //  TODO SWITCH TO THE SPECIFIED PROFILE
    }

}
