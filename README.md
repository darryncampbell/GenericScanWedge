*Please be aware that this application / sample is provided as-is for demonstration purposes without any guarantee of support*
=========================================================

# Generic Scan Wedge

## What is this?

This application implements an Android service that provides a rudimentary implementation of [Zebra's DataWedge](http://techdocs.zebra.com/datawedge/5-0/guide/about/) that can be run on non-Zebra devices, doing so allows the same application to scan barcodes on both Zebra and non-Zebra devices.  For more background see the accompanying [Blog](https://darryncampbellblog.wordpress.com/2016/08/16/writing-enterprise-android-applications-that-capture-barcode-data-and-run-on-multiple-devices/)

The diagram below illustrates the goal where this service is illustrated in green.
**Note that there is no support in this service for non-Zebra enterprise mobile computers** 

![Architecture](https://github.com/darryncampbell/GenericScanWedge/blob/master/doc/wider_architecture.png?raw=true)

This service implements the [DataWedge Intent API](http://techdocs.zebra.com/datawedge/5-0/guide/api/) so a user application that makes use of this API can be ported to another device **without modification**.


## Capabilities

This service supports barcode scanning on Android devices through:
- [ZXing](https://github.com/zxing/zxing), pronounced Zebra Crossing.  Note there is no connection between Zebra technologies and Zebra Crossing.
- [Google Barcode API](https://developers.google.com/vision/barcodes-overview) available on GMS devices only i.e. devices that include the Google Play store and runs on Android 2.3 and up (though I only tested on Android N)
- Scanners connected via Bluetooth SPP (Serial Port Profile).  Also known as RFCOMM
- **Only the 6.0 version of the DataWedge API is supported, not anything from 6.2+**

## How to use
Much like Zebra's DataWedge, this service relies on preconfigured 'Profiles' which define the scan engine configuration (e.g. which decoders are enabled or whether scanning is allowed).  Only one profile can be Enabled (i.e. active) at any one time.

### Profile Creation
![Screenshot 1](https://github.com/darryncampbell/GenericScanWedge/blob/master/doc/screen1.png?raw=true)

* Add a profile using the action button, on first launch a default profile will be automatically created for you

### Profile Configuration
![Screenshot 2](https://github.com/darryncampbell/GenericScanWedge/blob/master/doc/screen2.png?raw=true)
![Screenshot 3](https://github.com/darryncampbell/GenericScanWedge/blob/master/doc/screen3.png?raw=true)

* **Profile Name**: Change the default name assigned to the profile
* **Profile Enabled**: Only one profile can be enabled at a time, this profile will be used to configure the scan engine when a scan is performed
* **Scanning Engine**: Choose either ZXing or Google's Barcode API.  
* **(Barcode Input) Enabled**: Whether or not scanning is allowed when this profile is enabled.
* **UPCA**: Whether or not barcodes encoded with the UPCA symbology will be decoded.
* **UPCE...Data Matrix**: Whether or not barcodes encoded with the specified symbology will be decoded.
* **Intent Action**: The action that will be assigned to the intent sent to the calling application when a scan occurs.  To use with the [DataWedge API Exerciser](https://github.com/darryncampbell/DataWedge-API-Exerciser) specify com.zebra.dwapiexerciser.ACTION here.
* **Intent Category**: The category that will be assigned to the intent sent to the calling application when a scan occurs.  To use with the [DataWedge API Exerciser](https://github.com/darryncampbell/DataWedge-API-Exerciser) leave this blank.
* **Intent Delivery**: How to transmit the intent to the calling application when a barcode is scanned.  Either through startActivity(), sendBroadcast() or startService().
* **Foreground flag**: Enabled for sendBroadcast() only, sets the FLAG_RECEIVER_FOREGROUND in the scan intent.

### Testing
The easiest way to test this application is with the [DataWedge API Exerciser](https://github.com/darryncampbell/DataWedge-API-Exerciser).  **Just make sure you are only using the DataWedge 6.0 APIs**

## Limitations

Please note the following limitations / differences compared with Zebra's official DataWedge product:
* The source returned will be either camera-zxing or camera-google-vision.
* The names of the decoders will vary from scan engine to scan engine, specifically when the engine reports the decoding of the barcode, e.g. UPC_A, UPCA etc.  No effort has been made to standardize the names across scanning engines.
* This application does not change the profile dynamically when specific apps are shown.  As a result the the DataWedge "default" profile and associated functionality has not been implemented.
* The configured profiles are removed on uninstall and cannot be exported.
* **Only the 6.0 version of the DataWedge API is supported, not anything from 6.2+**

## Serial Port Profile (SPP) Support

If you want to use a scanner connected over Bluetooth (SPP) you will need to configure that scanner to be in SPP **client** mode, so the mobile device is acting as the master and doing the 'find devices' step.  First follow the instructions for your scanner to put it into SPP mode.  When configuring the profile select the Bluetooth (SPP) engine and the GenericScanWedge application will attempt to connect when the profile is enabled.  The connection is severred when any relevant configuration is changed because the configuration is only sent to the scanner on first connect.  If you need to reconnect to the scanner at any stage you can disable and then re-enable the profile.  I also made it so you can switch to an SPP enabled profile from another app using the SwitchProfile API as long as you had previously connected to a scanner.

I am sure the connection could be made more reliable but this is just a proof of concept, I used a lot of code from the default Android chat app including the dialog to select which BT device to connect to - this influenced the design decisions about how to incorporate SPP scanners.

Communication: Only receiving data from the scanner is supported and it works differently from ZXing and Google's Vision API, with those two you first call the StartScanning API to initiate a scan but with the BT SPP scanner you just press the hardware trigger after connecting to the device (by enabling an SPP profile)

Testing: I have only tested with an RS507 scanner in SPP mode but it should work with other scanners also.

### Video

The following video demonstrates the GenericScanWedge, particularly switching between ZXing and an SPP connected scanner

[![IMAGE ALT TEXT HERE](https://img.youtube.com/vi/OZ9v4kDq8OE/0.jpg)](https://www.youtube.com/watch?v=OZ9v4kDq8OE)
 
