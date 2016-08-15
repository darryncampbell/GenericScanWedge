package com.zebra.datawedgelite.datawedgelite;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by darry on 13/08/2016.
 */
public class Profile implements Serializable {

    final static String DECODER_UPCA = "UPCA";
    final static String DECODER_UPCE = "UPCE";
    final static String DECODER_EAN8= "EAN 8";
    final static String DECODER_EAN13 = "EAN 13";
    final static String DECODER_RSS14 = "RSS 14";
    final static String DECODER_CODE_39 = "CODE 39";
    final static String DECODER_CODE_93 = "CODE 93";
    final static String DECODER_CODE_128 = "CODE 128";
    final static String DECODER_ITF = "ITF";
    final static String DECODER_RSS_Expanded = "RSS Expanded";
    final static String DECODER_QR_CODE = "QR Code";
    final static String DECODER_DATA_MATRIX = "Data Matrix";

    public Profile(String name, boolean enabledByDefault)
    {
        this.name = name;
        this.profileEnabled = enabledByDefault;
        this.scanningEngine = ScanningEngine.SCANNING_ENGINE_ZXING;
        this.defaultProfile = enabledByDefault;
        this.barcodeInputEnabled = true;
        this.decodersEnabled = new HashMap<String, Boolean>();
        this.decodersEnabled.put(DECODER_UPCA, true);
        this.decodersEnabled.put(DECODER_UPCE, true);
        this.decodersEnabled.put(DECODER_EAN8, true);
        this.decodersEnabled.put(DECODER_EAN13, true);
        this.decodersEnabled.put(DECODER_RSS14, true);
        this.decodersEnabled.put(DECODER_CODE_39, true);
        this.decodersEnabled.put(DECODER_CODE_93, true);
        this.decodersEnabled.put(DECODER_CODE_128, true);
        this.decodersEnabled.put(DECODER_ITF, true);
        this.decodersEnabled.put(DECODER_RSS_Expanded, true);
        this.decodersEnabled.put(DECODER_QR_CODE, true);
        this.decodersEnabled.put(DECODER_DATA_MATRIX, true);
        this.intentOutputEnabled = true;
        this.intentAction = "";
        this.intentCategory = "";
        this.intentDelivery = IntentDelivery.INTENT_DELIVERY_START_ACTIVITY;
        this.receiverForegroundFlag = false;
    }

    public enum IntentDelivery {
        INTENT_DELIVERY_START_ACTIVITY,
        INTENT_DELIVERY_START_SERVICE,
        INTENT_DELIVERY_BROADCAST_INTENT
    }

    public enum ScanningEngine {
        SCANNING_ENGINE_ZXING,
        SCANNING_ENGINE_GOOGLE_VISION
    }

    private String name;
    private boolean profileEnabled;
    private ScanningEngine scanningEngine;
    private boolean defaultProfile; //  Not used
    private boolean barcodeInputEnabled;
    private Map<String, Boolean> decodersEnabled;
    private boolean intentOutputEnabled;
    private String intentAction;
    private String intentCategory;
    private IntentDelivery intentDelivery;
    private boolean receiverForegroundFlag;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean getProfileEnabled() {
        return profileEnabled;
    }

    public void setProfileEnabled(boolean profileEnabled) {
        this.profileEnabled = profileEnabled;
    }

    public boolean getDefaultProfile() {
        return defaultProfile;
    }

    public void setDefaultProfile(boolean defaultProfile) {
        this.defaultProfile = defaultProfile;
    }

    public boolean isBarcodeInputEnabled() {
        return barcodeInputEnabled;
    }

    public void setBarcodeInputEnabled(boolean barcodeInputEnabled) {
        this.barcodeInputEnabled = barcodeInputEnabled;
    }

    public boolean isIntentOutputEnabled() {
        return intentOutputEnabled;
    }

    public void setIntentOutputEnabled(boolean intentOutputEnabled) {
        this.intentOutputEnabled = intentOutputEnabled;
    }

    public String getIntentAction() {
        return intentAction;
    }

    public void setIntentAction(String intentAction) {
        this.intentAction = intentAction;
    }

    public String getIntentCategory() {
        return intentCategory;
    }

    public void setIntentCategory(String intentCategory) {
        this.intentCategory = intentCategory;
    }

    public IntentDelivery getIntentDelivery() {
        return intentDelivery;
    }

    public void setIntentDelivery(IntentDelivery intentDelivery) {
        this.intentDelivery = intentDelivery;
    }

    public ScanningEngine getScanningEngine() {
        return scanningEngine;
    }

    public void setScanningEngine(ScanningEngine scanningEngine) {
        this.scanningEngine = scanningEngine;
    }

    public Map<String, Boolean> getDecodersEnabled() {
        return decodersEnabled;
    }

    public void setDecodersEnabled(String decoder, boolean isEnabled) {
        this.decodersEnabled.put(decoder, isEnabled);
    }
    public Boolean isDecoderEnabled(String decoder)
    {
        return this.decodersEnabled.get(decoder);
    }

    public Boolean getReceiverForegroundFlag() {return this.receiverForegroundFlag;}
    public void setReceiverForegroundFlag(Boolean flag) {this.receiverForegroundFlag = flag;}
}
