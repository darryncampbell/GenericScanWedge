package com.darryncampbell.genericscanwedge.genericscanwedge;

import android.app.IntentService;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * helper methods.
 */
public class BluetoothConnectionService extends NonStopIntentService {

    private static BluetoothChatService mChatService = null;
    private BluetoothAdapter mBluetoothAdapter = null;
    static final String LOG_TAG = "Generic Scan Wedge";
    private Profile activeProfile = null;

    public static final String ACTION_DISCONNECT = "com.darryncampbell.genericscanwedge.genericscanwedge.action.BLUETOOTH_DISCONNECT";
    public static final String ACTION_CONNECT = "com.darryncampbell.genericscanwedge.genericscanwedge.action.BLUETOOTH_CONNECT";

    public BluetoothConnectionService() {
        super("BluetoothConnectionService");
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null)
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();

    }


    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            if (mChatService == null)
                mChatService = new BluetoothChatService(null, mHandler);
            final String action = intent.getAction();
            if (ACTION_CONNECT.equals(action)) {
                // Get the device MAC address
                activeProfile = (Profile)intent.getSerializableExtra("activeProfile");
                if (activeProfile != null)
                    Log.i(LOG_TAG, "Active profile is: " + activeProfile.getName());
                String address = intent.getStringExtra("macAddress");
                // Get the BluetoothDevice object
                BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
                // Attempt to connect to the device
                if (mChatService.getState() == BluetoothChatService.STATE_NONE)
                    mChatService.connect(device, true);

            } else if (ACTION_DISCONNECT.equals(action)) {
                if (mChatService.getState() != BluetoothChatService.STATE_NONE)
                    mChatService.stop();

            }
        }
    }

    private final Handler mHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case BluetoothChatService.Constants.MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    String readMessage = new String(readBuf, 0, msg.arg1);
                    if (activeProfile != null)
                        SendBluetoothScannerIntent(readMessage, activeProfile);
                    break;
                case BluetoothChatService.Constants.MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case BluetoothChatService.STATE_CONNECTED:
                            ShowToastInIntentService("Bluetooth Scanner connected");
                            break;
                        case BluetoothChatService.STATE_CONNECTING:
                            ShowToastInIntentService("Bluetooth Scanner connecting");
                            break;
                        case BluetoothChatService.STATE_LISTEN:
                        case BluetoothChatService.STATE_NONE:
                            ShowToastInIntentService("Bluetooth Scanner disconnected");
                            break;
                    }
                    break;
            }
        }
    };

    private void SendBluetoothScannerIntent(String data, Profile activeProfile)
    {
        //  Intent format to return to the calling application is as defined by DataWedge
        //  including backwards compatibility with pre-Zebra devices.

        Intent barcodeIntent = new Intent(activeProfile.getIntentAction());
        barcodeIntent.putExtra("com.symbol.datawedge.source", "bluetooth_scanner-spp");
        barcodeIntent.putExtra("com.symbol.datawedge.label_type", "Unknown");
        barcodeIntent.putExtra("com.symbol.datawedge.data_string", data);
        barcodeIntent.putExtra("com.symbol.datawedge.decode_data", data.getBytes());
        barcodeIntent.putExtra("com.motorolasolutions.emdk.datawedge.source", "bluetooth_scanner-spp");
        barcodeIntent.putExtra("com.motorolasolutions.emdk.datawedge.label_type", "Unknown");
        barcodeIntent.putExtra("com.motorolasolutions.emdk.datawedge.data_string", data);
        barcodeIntent.putExtra("com.motorolasolutions.emdk.datawedge.decode_data", data.getBytes());
        if (!activeProfile.getIntentCategory().equalsIgnoreCase(""))
            barcodeIntent.addCategory(activeProfile.getIntentCategory());
        if (activeProfile.getIntentDelivery() == Profile.IntentDelivery.INTENT_DELIVERY_START_ACTIVITY) {
            try {
                barcodeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(barcodeIntent);
            } catch (ActivityNotFoundException e) {
                Log.w(LOG_TAG, "No Activity found to handle barcode.  Current profile action is " + activeProfile.getIntentAction());
            }
        }
        else if (activeProfile.getIntentDelivery() == Profile.IntentDelivery.INTENT_DELIVERY_START_SERVICE)
        {
            try {
                startService(ZxingActivity.createExplicitFromImplicitIntent(getApplicationContext(), barcodeIntent));
            }
            catch (Exception e)
            {
                Log.w(LOG_TAG, "No Service found to handle barcode.  Current profile action is " + activeProfile.getIntentAction());
            }
        }
        else if (activeProfile.getIntentDelivery() == Profile.IntentDelivery.INTENT_DELIVERY_BROADCAST_INTENT)
        {
            if (activeProfile.getReceiverForegroundFlag())
                barcodeIntent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
            sendBroadcast(barcodeIntent);
        }
    }

    public void ShowToastInIntentService(final String sText) {
        final Context MyContext = this;

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast toast1 = Toast.makeText(MyContext, sText, Toast.LENGTH_SHORT);
                toast1.show();
            }
        });
    };
}
