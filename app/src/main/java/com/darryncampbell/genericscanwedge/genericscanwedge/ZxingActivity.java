package com.darryncampbell.genericscanwedge.genericscanwedge;

import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.List;

//  Activity to encapsulate initiating a barcode scan using the ZXing library.  This needs to be
//  its own activity as that is what the ZXing interface needs.
public class ZxingActivity extends AppCompatActivity {

    private Profile activeProfile;
    static final String LOG_TAG = "Generic Scan Wedge";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hiddenactivity);

        //  Expect the action for the return intent to be passed in
        activeProfile = (Profile) getIntent().getSerializableExtra("activeProfile");

        //  Launching Zebra Crossing
        IntentIntegrator integrator = new IntentIntegrator(this);
        //  Specify the enabled decoders in ZXing (based on the active profile)
        ArrayList<String> desiredDecoders = new ArrayList<String>();
        if (activeProfile.isDecoderEnabled(Profile.DECODER_UPCA))
            desiredDecoders.add("UPC_A");
        if (activeProfile.isDecoderEnabled(Profile.DECODER_UPCE))
            desiredDecoders.add("UPC_E");
        if (activeProfile.isDecoderEnabled(Profile.DECODER_EAN8))
            desiredDecoders.add("EAN_8");
        if (activeProfile.isDecoderEnabled(Profile.DECODER_EAN13))
            desiredDecoders.add("EAN_13");
        if (activeProfile.isDecoderEnabled(Profile.DECODER_RSS14))
            desiredDecoders.add("RSS_14");
        if (activeProfile.isDecoderEnabled(Profile.DECODER_CODE_39))
            desiredDecoders.add("CODE_39");
        if (activeProfile.isDecoderEnabled(Profile.DECODER_CODE_93))
            desiredDecoders.add("CODE_93");
        if (activeProfile.isDecoderEnabled(Profile.DECODER_CODE_128))
            desiredDecoders.add("CODE_128");
        if (activeProfile.isDecoderEnabled(Profile.DECODER_ITF))
            desiredDecoders.add("ITF");
        if (activeProfile.isDecoderEnabled(Profile.DECODER_RSS_Expanded))
            desiredDecoders.add("RSS_EXPANDED");
        if (activeProfile.isDecoderEnabled(Profile.DECODER_QR_CODE))
            desiredDecoders.add("QR_CODE");
        if (activeProfile.isDecoderEnabled(Profile.DECODER_DATA_MATRIX))
            desiredDecoders.add("DATA_MATRIX");
        integrator.setDesiredBarcodeFormats(desiredDecoders);
        //https://github.com/zxing/zxing/blob/master/android-integration/src/main/java/com/google/zxing/integration/android/IntentIntegrator.java
        integrator.initiateScan();
    }

    //  Receive the barcode scan from the ZXing library
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanResult != null) {
            // handle scan result
            if(scanResult.getContents() == null) {
                //  User pressed the back key
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
                sendMainActivityFinishIntent();
            } else {
                //  Intent format to return to the calling application is as defined by DataWedge
                //  including backwards compatibility with pre-Zebra devices.
                Intent barcodeIntent = new Intent(this.activeProfile.getIntentAction());
                barcodeIntent.putExtra("com.symbol.datawedge.source", "camera-zxing");
                barcodeIntent.putExtra("com.symbol.datawedge.label_type", scanResult.getFormatName());
                barcodeIntent.putExtra("com.symbol.datawedge.data_string", scanResult.getContents());
                barcodeIntent.putExtra("com.symbol.datawedge.decode_data", scanResult.getContents().getBytes());
                barcodeIntent.putExtra("com.motorolasolutions.emdk.datawedge.source", "scanner-zxing");
                barcodeIntent.putExtra("com.motorolasolutions.emdk.datawedge.label_type", scanResult.getFormatName());
                barcodeIntent.putExtra("com.motorolasolutions.emdk.datawedge.data_string", scanResult.getContents());
                barcodeIntent.putExtra("com.motorolasolutions.emdk.datawedge.decode_data", scanResult.getContents().getBytes());
                if (!this.activeProfile.getIntentCategory().equalsIgnoreCase(""))
                    barcodeIntent.addCategory(this.activeProfile.getIntentCategory());
                if (this.activeProfile.getIntentDelivery() == Profile.IntentDelivery.INTENT_DELIVERY_START_ACTIVITY) {
                    try {
                        barcodeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(barcodeIntent);
                        //  This also prevents ZXing from still showing if we tab back to the profile app
                        sendMainActivityFinishIntent();
                    } catch (ActivityNotFoundException e) {
                        Log.w(LOG_TAG, "No Activity found to handle barcode.  Current profile action is " + this.activeProfile.getIntentAction());
                    }
                }
                else if (this.activeProfile.getIntentDelivery() == Profile.IntentDelivery.INTENT_DELIVERY_START_SERVICE)
                {
                    try {
                        startService(createExplicitFromImplicitIntent(getApplicationContext(), barcodeIntent));
                        sendMainActivityFinishIntent();
                    }
                    catch (Exception e)
                    {
                        Log.w(LOG_TAG, "No Service found to handle barcode.  Current profile action is " + this.activeProfile.getIntentAction());
                    }
                }
                else if (this.activeProfile.getIntentDelivery() == Profile.IntentDelivery.INTENT_DELIVERY_BROADCAST_INTENT)
                {
                    if (this.activeProfile.getReceiverForegroundFlag())
                        barcodeIntent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
                    sendBroadcast(barcodeIntent);
                    sendMainActivityFinishIntent();
                }
            }
        }
        finish();
    }

    private void sendMainActivityFinishIntent()
    {
        //  Bit of a hack but want to return to the original calling application
        Intent finishIntent = new Intent(this, MainActivity.class);
        finishIntent.putExtra("finish", true);
        finishIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(finishIntent);
    }

    //  Work around for sending to a service.  Could probably be more intelligent here as just
    //  copied from stack overflow
    private Intent createExplicitFromImplicitIntent(Context context, Intent implicitIntent) {
        // Retrieve all services that can match the given intent
        PackageManager pm = context.getPackageManager();
        List<ResolveInfo> resolveInfo = pm.queryIntentServices(implicitIntent, 0);

        // Make sure only one match was found
        if (resolveInfo == null || resolveInfo.size() != 1) {
            return null;
        }

        // Get component info and create ComponentName
        ResolveInfo serviceInfo = resolveInfo.get(0);
        String packageName = serviceInfo.serviceInfo.packageName;
        String className = serviceInfo.serviceInfo.name;
        ComponentName component = new ComponentName(packageName, className);

        // Create a new intent. Use the old one for extras and such reuse
        Intent explicitIntent = new Intent(implicitIntent);

        // Set the component to be explicit
        explicitIntent.setComponent(component);

        return explicitIntent;
    }
}
