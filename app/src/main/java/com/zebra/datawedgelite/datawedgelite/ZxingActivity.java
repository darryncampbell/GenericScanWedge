package com.zebra.datawedgelite.datawedgelite;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.Collection;

public class ZxingActivity extends AppCompatActivity {

    private Profile activeProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hiddenactivity);

        //  Expect the action for the return intent to be passed in
        activeProfile = (Profile) getIntent().getSerializableExtra("activeProfile");

        //  Launching Zebra Crossing
        IntentIntegrator integrator = new IntentIntegrator(this);
        //  todo TEST THIS would also expect decoder information to come from the DW profiles
        //integrator.setDesiredBarcodeFormats(IntentIntegrator.PRODUCT_CODE_TYPES);
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

        /*
            Intent data = new Intent("com.google.zxing.client.android.SCAN");
            data.putExtra("SCAN_MODE", "QR_CODE_MODE");
            startActivityForResult(data, 0);
        */

    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanResult != null) {
            // handle scan result
            if(scanResult.getContents() == null) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
                //  Bit of a hack but want to return to the original calling application
                Intent finishIntent = new Intent(this, MainActivity.class);
                finishIntent.putExtra("finish", true);
                finishIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(finishIntent);
            } else {
                Intent barcodeIntent = new Intent();
                barcodeIntent.setAction(this.activeProfile.getIntentAction());
                barcodeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                //  todo return a source like 'scanner-ZXing?' or keep consistent with Datawedge?
                barcodeIntent.putExtra("com.symbol.datawedge.source", "scanner-zxing");
                //  todo Convert formatname to Symbol names???
                barcodeIntent.putExtra("com.symbol.datawedge.label_type", scanResult.getFormatName());
                barcodeIntent.putExtra("com.symbol.datawedge.data_string", scanResult.getContents());
                barcodeIntent.putExtra("com.symbol.datawedge.decode_data", scanResult.getContents().getBytes());
                barcodeIntent.putExtra("com.motorolasolutions.emdk.datawedge.source", "scanner-zxing");
                barcodeIntent.putExtra("com.motorolasolutions.emdk.datawedge.label_type", scanResult.getFormatName());
                barcodeIntent.putExtra("com.motorolasolutions.emdk.datawedge.data_string", scanResult.getContents());
                barcodeIntent.putExtra("com.motorolasolutions.emdk.datawedge.decode_data", scanResult.getContents().getBytes());
                barcodeIntent.addCategory(this.activeProfile.getIntentCategory());
                //  todo startActivity / startService etc depends on the delivery mechanism of the active profile
                startActivity(barcodeIntent);

            }
        }
        finish();
    }
}
