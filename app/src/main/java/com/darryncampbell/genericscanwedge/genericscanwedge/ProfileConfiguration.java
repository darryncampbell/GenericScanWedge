package com.darryncampbell.genericscanwedge.genericscanwedge;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import java.util.ArrayList;

//  Logic underlying the profile configuration screen, accepts input from the user on what attributes
//  should be in the specified profile and persists it to disk.
public class ProfileConfiguration extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {

    //  The list of all profiles (passed via Intent).  We need this to ensure all profiles are persisted (as we save them all in the same file)
    ArrayList<Profile> profiles;
    //  The position in the profiles array which corresponds with the profile we are to display
    int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_configuration);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        profiles = (ArrayList<Profile>)getIntent().getSerializableExtra("profileObjects");
        position = getIntent().getIntExtra("profilePosition", 0);
        Profile profile = profiles.get(position);
        if (profile != null)
        {
            getSupportActionBar().setTitle("Configure Profile: " + profile.getName());
        }

        //  For each control, set the current value and add an event listener to process changes.
        final Button button = (Button) findViewById(R.id.btnAdminDeleteProfile);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                profiles.remove(position);
                MainActivity.saveProfiles(profiles, getApplicationContext());
                finish();
            }
        });

        EditText editTextProfileName = (EditText) findViewById(R.id.editProfileName);
        editTextProfileName.setText(this.profiles.get(position).getName());
        editTextProfileName.addTextChangedListener(new TextWatcher()
        {
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                profiles.get(position).setName(s.toString());
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void afterTextChanged(Editable s) {}
        });

        CheckBox profileEnabledCheck = (CheckBox) findViewById(R.id.checkProfileEnabled);
        profileEnabledCheck.setChecked(this.profiles.get(position).getProfileEnabled());
        profileEnabledCheck.setOnCheckedChangeListener(this);

        final Spinner spinnerScanningEngine = (Spinner) findViewById(R.id.spinnerScaningEngine);
        switch (profiles.get(position).getScanningEngine())
        {
            case SCANNING_ENGINE_ZXING:
                spinnerScanningEngine.setSelection(0);
                break;
            case SCANNING_ENGINE_GOOGLE_VISION:
                spinnerScanningEngine.setSelection(1);
                break;
        }
        spinnerScanningEngine.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {

                String selectedMechanism = spinnerScanningEngine.getSelectedItem().toString();
                if (selectedMechanism.equalsIgnoreCase(getResources().getString(R.string.scanning_engine_zebra_crossing)))
                {
                    profiles.get(position).setScanningEngine(Profile.ScanningEngine.SCANNING_ENGINE_ZXING);
                }
                else if (selectedMechanism.equalsIgnoreCase(getResources().getString(R.string.scanning_engine_google_vision)))
                {
                    profiles.get(position).setScanningEngine(Profile.ScanningEngine.SCANNING_ENGINE_GOOGLE_VISION);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });

        CheckBox profileBarcodeEnabledCheck = (CheckBox) findViewById(R.id.checkConfigureProfileBarcodeEnabled);
        profileBarcodeEnabledCheck.setChecked(this.profiles.get(position).isBarcodeInputEnabled());
        profileBarcodeEnabledCheck.setOnCheckedChangeListener(this);

        CheckBox profileDecoderUPCACheck = (CheckBox) findViewById(R.id.checkConfigureProfileBarcodeUPCA);
        profileDecoderUPCACheck.setChecked(this.profiles.get(position).isDecoderEnabled(Profile.DECODER_UPCA));
        profileDecoderUPCACheck.setOnCheckedChangeListener(this);

        CheckBox profileDecoderUPCECheck = (CheckBox) findViewById(R.id.checkConfigureProfileBarcodeUPCE);
        profileDecoderUPCECheck.setChecked(this.profiles.get(position).isDecoderEnabled(Profile.DECODER_UPCE));
        profileDecoderUPCECheck.setOnCheckedChangeListener(this);

        CheckBox profileDecoderEAN8Check = (CheckBox) findViewById(R.id.checkConfigureProfileBarcodeEan8);
        profileDecoderEAN8Check.setChecked(this.profiles.get(position).isDecoderEnabled(Profile.DECODER_EAN8));
        profileDecoderEAN8Check.setOnCheckedChangeListener(this);

        CheckBox profileDecoderEAN13Check = (CheckBox) findViewById(R.id.checkConfigureProfileBarcodeEan13);
        profileDecoderEAN13Check.setChecked(this.profiles.get(position).isDecoderEnabled(Profile.DECODER_EAN13));
        profileDecoderEAN13Check.setOnCheckedChangeListener(this);

        CheckBox profileDecoderRSS14Check = (CheckBox) findViewById(R.id.checkConfigureProfileBarcodeRSS14);
        profileDecoderRSS14Check.setChecked(this.profiles.get(position).isDecoderEnabled(Profile.DECODER_RSS14));
        profileDecoderRSS14Check.setOnCheckedChangeListener(this);

        CheckBox profileDecoderCode39Check = (CheckBox) findViewById(R.id.checkConfigureProfileBarcodeCode39);
        profileDecoderCode39Check.setChecked(this.profiles.get(position).isDecoderEnabled(Profile.DECODER_CODE_39));
        profileDecoderCode39Check.setOnCheckedChangeListener(this);

        CheckBox profileDecoderCode93Check = (CheckBox) findViewById(R.id.checkConfigureProfileBarcodeCode93);
        profileDecoderCode93Check.setChecked(this.profiles.get(position).isDecoderEnabled(Profile.DECODER_CODE_93));
        profileDecoderCode93Check.setOnCheckedChangeListener(this);

        CheckBox profileDecoderCode128Check = (CheckBox) findViewById(R.id.checkConfigureProfileBarcodeCode128);
        profileDecoderCode128Check.setChecked(this.profiles.get(position).isDecoderEnabled(Profile.DECODER_CODE_128));
        profileDecoderCode128Check.setOnCheckedChangeListener(this);

        CheckBox profileDecoderITFCheck = (CheckBox) findViewById(R.id.checkConfigureProfileBarcodeITF);
        profileDecoderITFCheck.setChecked(this.profiles.get(position).isDecoderEnabled(Profile.DECODER_ITF));
        profileDecoderITFCheck.setOnCheckedChangeListener(this);

        CheckBox profileDecoderRSSExpandedCheck = (CheckBox) findViewById(R.id.checkConfigureProfileBarcodeRSSExpanded);
        profileDecoderRSSExpandedCheck.setChecked(this.profiles.get(position).isDecoderEnabled(Profile.DECODER_RSS_Expanded));
        profileDecoderRSSExpandedCheck.setOnCheckedChangeListener(this);

        CheckBox profileDecoderQRCodeCheck = (CheckBox) findViewById(R.id.checkConfigureProfileBarcodeQRCode);
        profileDecoderQRCodeCheck.setChecked(this.profiles.get(position).isDecoderEnabled(Profile.DECODER_QR_CODE));
        profileDecoderQRCodeCheck.setOnCheckedChangeListener(this);

        CheckBox profileDecoderDataMatrixCheck = (CheckBox) findViewById(R.id.checkConfigureProfileBarcodeDataMatrix);
        profileDecoderDataMatrixCheck.setChecked(this.profiles.get(position).isDecoderEnabled(Profile.DECODER_DATA_MATRIX));
        profileDecoderDataMatrixCheck.setOnCheckedChangeListener(this);

        EditText editTextIntentAction = (EditText) findViewById(R.id.editIntentAction);
        editTextIntentAction.setText(profiles.get(position).getIntentAction());
        editTextIntentAction.addTextChangedListener(new TextWatcher()
        {
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                profiles.get(position).setIntentAction(s.toString());
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void afterTextChanged(Editable s) {}
        });

        EditText editTextIntentCategory = (EditText) findViewById(R.id.editIntentCategory);
        editTextIntentCategory.setText(profiles.get(position).getIntentCategory());
        editTextIntentCategory.addTextChangedListener(new TextWatcher()
        {
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                profiles.get(position).setIntentCategory(s.toString());
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void afterTextChanged(Editable s) {}
        });

        final Spinner spinnerIntentOutputMechanism = (Spinner) findViewById(R.id.spinnerIntentOutputMechanism);
        switch (profiles.get(position).getIntentDelivery())
        {
            case INTENT_DELIVERY_START_ACTIVITY:
                spinnerIntentOutputMechanism.setSelection(0);
                disableReceiverForegroundFlagUI();
                break;
            case INTENT_DELIVERY_START_SERVICE:
                spinnerIntentOutputMechanism.setSelection(1);
                disableReceiverForegroundFlagUI();
                break;
            case INTENT_DELIVERY_BROADCAST_INTENT:
                spinnerIntentOutputMechanism.setSelection(2);
                enableReceiverForegroundFlagUI();
                break;
        }
        spinnerIntentOutputMechanism.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {

                String selectedMechanism = spinnerIntentOutputMechanism.getSelectedItem().toString();
                if (selectedMechanism.equalsIgnoreCase(getResources().getString(R.string.intent_mechanism_start_activity)))
                {
                    profiles.get(position).setIntentDelivery(Profile.IntentDelivery.INTENT_DELIVERY_START_ACTIVITY);
                    disableReceiverForegroundFlagUI();
                }
                else if (selectedMechanism.equalsIgnoreCase(getResources().getString(R.string.intent_mechanism_start_service)))
                {
                    profiles.get(position).setIntentDelivery(Profile.IntentDelivery.INTENT_DELIVERY_START_SERVICE);
                    disableReceiverForegroundFlagUI();
                }
                else if (selectedMechanism.equalsIgnoreCase(getResources().getString(R.string.intent_mechanism_broadcast_intent)))
                {
                    profiles.get(position).setIntentDelivery(Profile.IntentDelivery.INTENT_DELIVERY_BROADCAST_INTENT);
                    enableReceiverForegroundFlagUI();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });

        CheckBox configureReceiverForegroundFlag = (CheckBox) findViewById(R.id.checkConfigureReceiveForegroundFlag);
        configureReceiverForegroundFlag.setChecked(this.profiles.get(position).getReceiverForegroundFlag());
        configureReceiverForegroundFlag.setOnCheckedChangeListener(this);

    }

    //  The receiver foreground flag checkbox and UI should only be shown if the user has selected sendBroadcast()
    private void disableReceiverForegroundFlagUI()
    {
        TextView receiverForegroundFlag = (TextView) findViewById(R.id.txtConfigureReceiveForegroundFlag);
        TextView receiverForegroundFlagHelp = (TextView) findViewById(R.id.txtConfigureReceiveForegroundFlagHelp);
        CheckBox receiverForegroundFlagCheck = (CheckBox) findViewById(R.id.checkConfigureReceiveForegroundFlag);
        receiverForegroundFlag.setEnabled(false);
        receiverForegroundFlagHelp.setEnabled(false);
        receiverForegroundFlagCheck.setEnabled(false);
    }

    private void enableReceiverForegroundFlagUI()
    {
        TextView receiverForegroundFlag = (TextView) findViewById(R.id.txtConfigureReceiveForegroundFlag);
        TextView receiverForegroundFlagHelp = (TextView) findViewById(R.id.txtConfigureReceiveForegroundFlagHelp);
        CheckBox receiverForegroundFlagCheck = (CheckBox) findViewById(R.id.checkConfigureReceiveForegroundFlag);
        receiverForegroundFlag.setEnabled(true);
        receiverForegroundFlagHelp.setEnabled(true);
        receiverForegroundFlagCheck.setEnabled(true);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onPause()
    {
        super.onPause();
        //  Back up the profiles
        MainActivity.saveProfiles(this.profiles, getApplicationContext());
    }

    @Override
    public void onStop()
    {
        super.onStop();
        //  The profiles are backed up onPause()
    }

    //  Same logic for handling all decoder enabled? checkboxes
    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        switch(compoundButton.getId()){
            case R.id.checkProfileEnabled:
                //  Only one profile can be enabled at a time in this limited implementation of DW profiles,
                //  therefore disable all other profiles
                if (b)
                {
                    for (int i = 0; i < this.profiles.size(); i++)
                        this.profiles.get(i).setProfileEnabled(false);
                }
                this.profiles.get(this.position).setProfileEnabled(b);
                break;
            case R.id.checkConfigureProfileBarcodeEnabled:
                this.profiles.get(this.position).setBarcodeInputEnabled(b);
                break;
            case R.id.checkConfigureProfileBarcodeUPCA:
                this.profiles.get(this.position).setDecodersEnabled(Profile.DECODER_UPCA, b);
                break;
            case R.id.checkConfigureProfileBarcodeUPCE:
                this.profiles.get(this.position).setDecodersEnabled(Profile.DECODER_UPCE, b);
                break;
            case R.id.checkConfigureProfileBarcodeEan8:
                this.profiles.get(this.position).setDecodersEnabled(Profile.DECODER_EAN8, b);
                break;
            case R.id.checkConfigureProfileBarcodeEan13:
                this.profiles.get(this.position).setDecodersEnabled(Profile.DECODER_EAN13, b);
                break;
            case R.id.checkConfigureProfileBarcodeRSS14:
                this.profiles.get(this.position).setDecodersEnabled(Profile.DECODER_RSS14, b);
                break;
            case R.id.checkConfigureProfileBarcodeCode39:
                this.profiles.get(this.position).setDecodersEnabled(Profile.DECODER_CODE_39, b);
                break;
            case R.id.checkConfigureProfileBarcodeCode93:
                this.profiles.get(this.position).setDecodersEnabled(Profile.DECODER_CODE_93, b);
                break;
            case R.id.checkConfigureProfileBarcodeCode128:
                this.profiles.get(this.position).setDecodersEnabled(Profile.DECODER_CODE_128, b);
                break;
            case R.id.checkConfigureProfileBarcodeITF:
                this.profiles.get(this.position).setDecodersEnabled(Profile.DECODER_ITF, b);
                break;
            case R.id.checkConfigureProfileBarcodeRSSExpanded:
                this.profiles.get(this.position).setDecodersEnabled(Profile.DECODER_RSS_Expanded, b);
                break;
            case R.id.checkConfigureProfileBarcodeQRCode:
                this.profiles.get(this.position).setDecodersEnabled(Profile.DECODER_QR_CODE, b);
                break;
            case R.id.checkConfigureProfileBarcodeDataMatrix:
                this.profiles.get(this.position).setDecodersEnabled(Profile.DECODER_DATA_MATRIX, b);
               break;
            case R.id.checkConfigureReceiveForegroundFlag:
                this.profiles.get(this.position).setReceiverForegroundFlag(b);
                break;
        }
    }
}
