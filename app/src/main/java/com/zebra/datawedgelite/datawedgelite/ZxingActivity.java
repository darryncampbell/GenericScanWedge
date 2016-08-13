package com.zebra.datawedgelite.datawedgelite;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class ZxingActivity extends AppCompatActivity {

    private String intentAction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hiddenactivity);

        //  Expect the action for the return intent to be passed in
        intentAction = getIntent().getStringExtra("intentAction");
        //  todo would also expect decoder information to come from the DW profiles

        //  Launching Zebra Crossing
        IntentIntegrator integrator = new IntentIntegrator(this);
        //integrator.setDesiredBarcodeFormats(IntentIntegrator.PRODUCT_CODE_TYPES);
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
                barcodeIntent.setAction(this.intentAction);
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
        //        barcodeIntent.addCategory("");
                startActivity(barcodeIntent);

            }
        }
        // else continue with any other code you need in the method
        // TODO: 12/08/2016



        finish();
    }
}
