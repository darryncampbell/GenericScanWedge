package com.zebra.datawedgelite.datawedgelite;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;
import com.zebra.datawedgelite.datawedgelite.GoogleVisionBarcode.BarcodeCaptureActivity;

import java.util.ArrayList;

public class GoogleVisionBarcodeActivity extends AppCompatActivity {

    private Profile activeProfile;
    private static final int RC_BARCODE_CAPTURE = 9001;
    static final String LOG_CATEGORY = "DWAPI Lite";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null)
            return;

        setContentView(R.layout.activity_hiddenactivity);

        //  Expect the action for the return intent to be passed in
        activeProfile = (Profile) getIntent().getSerializableExtra("activeProfile");
        Intent intent = new Intent(this, com.zebra.datawedgelite.datawedgelite.GoogleVisionBarcode.BarcodeCaptureActivity.class);
        intent.putExtra(com.zebra.datawedgelite.datawedgelite.GoogleVisionBarcode.BarcodeCaptureActivity.AutoFocus, true);
        intent.putExtra(com.zebra.datawedgelite.datawedgelite.GoogleVisionBarcode.BarcodeCaptureActivity.UseFlash, true);
        intent.putExtra("formats", giveSupportedDecoders());
        startActivityForResult(intent, RC_BARCODE_CAPTURE);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_BARCODE_CAPTURE) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    Barcode barcode = data.getParcelableExtra(BarcodeCaptureActivity.BarcodeObject);
                    //statusMessage.setText(R.string.barcode_success);
                    //barcodeValue.setText(barcode.displayValue);
                    Log.d(LOG_CATEGORY, "Barcode read from Google Vision Barcode API: " + barcode.displayValue);

                    //  https://developers.google.com/android/reference/com/google/android/gms/vision/barcode/Barcode

                    Intent barcodeIntent = new Intent();
                    barcodeIntent.setAction(this.activeProfile.getIntentAction());
                    barcodeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    barcodeIntent.putExtra("com.symbol.datawedge.source", "scanner-google-vision");
                    barcodeIntent.putExtra("com.symbol.datawedge.label_type", decoderFormatToString(barcode.format));
                    barcodeIntent.putExtra("com.symbol.datawedge.data_string", barcode.displayValue);
                    barcodeIntent.putExtra("com.symbol.datawedge.decode_data", barcode.rawValue.getBytes());
                    barcodeIntent.putExtra("com.motorolasolutions.emdk.datawedge.source", "scanner-google-vision");
                    barcodeIntent.putExtra("com.motorolasolutions.emdk.datawedge.label_type", decoderFormatToString(barcode.format));
                    barcodeIntent.putExtra("com.motorolasolutions.emdk.datawedge.data_string", barcode.displayValue);
                    barcodeIntent.putExtra("com.motorolasolutions.emdk.datawedge.decode_data", barcode.rawValue.getBytes());
                    if (!this.activeProfile.getIntentCategory().equalsIgnoreCase(""))
                        barcodeIntent.addCategory(this.activeProfile.getIntentCategory());
                    //  todo startActivity / startService etc depends on the delivery mechanism of the active profile
                    try {
                        startActivity(barcodeIntent);
                    }
                    catch (ActivityNotFoundException e)
                    {
                        Log.w(LOG_CATEGORY, "No Activity found to handle barcode.  Current profile action is " + this.activeProfile.getIntentAction());
                    }

                } else {
                    Log.d(LOG_CATEGORY, "No barcode captured from Google Vision Barcode, intent data is null");
                    //  The user has cancelled the capture
                    Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
                    //  Bit of a hack but want to return to the original calling application
                    Intent finishIntent = new Intent(this, MainActivity.class);
                    finishIntent.putExtra("finish", true);
                    finishIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(finishIntent);
                }
            } else {
                Log.d(LOG_CATEGORY, String.format(getString(R.string.barcode_error),
                        CommonStatusCodes.getStatusCodeString(resultCode)));
            }
        }
        else {
            super.onActivityResult(requestCode, resultCode, data);
        }
        finish();
    }

    private String decoderFormatToString(int decoderFormat)
    {
        switch(decoderFormat) {
            case Barcode.AZTEC:
                return "AZTEC";
            case Barcode.CODABAR:
                return "CODABAR";
            case Barcode.CODE_39:
                return "CODE 39";
            case Barcode.CODE_93:
                return "CODE 93";
            case Barcode.CODE_128:
                return "CODE 128";
            case Barcode.DATA_MATRIX:
                return "Data Matrix";
            case Barcode.EAN_8:
                return "EAN 8";
            case Barcode.EAN_13:
                return "EAN 13";
            case Barcode.ISBN:
                return "ISBN";
            case Barcode.ITF:
                return "ITF";
            case Barcode.PDF417:
                return "PDF417";
            case Barcode.QR_CODE:
                return "QR Code";
            case Barcode.UPC_A:
                return "UPCA";
            case Barcode.UPC_E:
                return "UPCE";
            default:
                return "Unknown";
        }
    }

    private int giveSupportedDecoders()
    {
        int returnVal = 0;
        if (activeProfile.isDecoderEnabled(Profile.DECODER_UPCA))
            returnVal = returnVal | Barcode.UPC_A;
        if (activeProfile.isDecoderEnabled(Profile.DECODER_UPCE))
            returnVal = returnVal | Barcode.UPC_E;
        if (activeProfile.isDecoderEnabled(Profile.DECODER_EAN8))
            returnVal = returnVal | Barcode.EAN_8;
        if (activeProfile.isDecoderEnabled(Profile.DECODER_EAN13))
            returnVal = returnVal | Barcode.EAN_13;
        if (activeProfile.isDecoderEnabled(Profile.DECODER_RSS14))
        {}    //  Not supported by this decode engine.
        if (activeProfile.isDecoderEnabled(Profile.DECODER_CODE_39))
            returnVal = returnVal | Barcode.CODE_39;
        if (activeProfile.isDecoderEnabled(Profile.DECODER_CODE_93))
            returnVal = returnVal | Barcode.CODE_93;
        if (activeProfile.isDecoderEnabled(Profile.DECODER_CODE_128))
            returnVal = returnVal | Barcode.CODE_128;
        if (activeProfile.isDecoderEnabled(Profile.DECODER_ITF))
            returnVal = returnVal | Barcode.ITF;
        if (activeProfile.isDecoderEnabled(Profile.DECODER_RSS_Expanded))
        {}//  Not supported by this decode engine.
        if (activeProfile.isDecoderEnabled(Profile.DECODER_QR_CODE))
            returnVal = returnVal | Barcode.QR_CODE;
        if (activeProfile.isDecoderEnabled(Profile.DECODER_DATA_MATRIX))
            returnVal = returnVal | Barcode.DATA_MATRIX;

        //  Note: There are other formats supported by the scan engine but I haven't exposed an API
        //  for them.

        return returnVal;
    }

}
