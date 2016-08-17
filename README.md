# DataWedgeLite

###This application is provided without guarantee or warantee

## What is this?

This application implements an Android service that provides a rudementary implementation of [Zebra's DataWedge](http://techdocs.zebra.com/datawedge/5-0/guide/about/) that can be run on non-Zebra devices, doing so allows the same application to scan barcodes on both Zebra and non-Zebra devices.  For more background see the accompanying [Blog](https://darryncampbellblog.wordpress.com/2016/08/16/writing-enterprise-android-applications-that-capture-barcode-data-and-run-on-multiple-devices/)

The diagram below illustrates the goal where this service is illustrated in green.
**Note that there is no support in this service for non-Zebra enterprise mobile computers** 

![Architecture](https://github.com/darryncampbell/DataWedgeLite/blob/master/doc/wider_architecture.png?raw=true)

This service implements the [DataWedge Intent API](http://techdocs.zebra.com/datawedge/5-0/guide/api/) so a user application that makes use of this API can be ported to another device **without modification**.


## Capabilities

This service supports barcode scanning on Android devices through:
- [ZXing](https://github.com/zxing/zxing), pronounced Zebra Crossing.  Note there is no connection between Zebra technologies and Zebra Crossing.
- [Google Barcode API](https://developers.google.com/vision/barcodes-overview) available on GMS devices only i.e. devices that include the Google Play store and runs on Android 2.3 and up (though I only tested on Android N)

## How to use
Much like Zebra's DataWedge, this service relies on preconfigured 'Profiles' which define the scan engine configuration (e.g. which decoders are enabled or whether scanning is allowed).  Only one profile can be Enabled (i.e. active) at any one time.

### Profile Creation
![Screenshot 1](https://github.com/darryncampbell/DataWedgeLite/blob/master/doc/screen1.png?raw=true)

* Add a profile using the action button, on first launch a default profile will be automatically created for you

### Profile Configuration
![Screenshot 2](https://github.com/darryncampbell/DataWedgeLite/blob/master/doc/screen2.png?raw=true)
![Screenshot 3](https://github.com/darryncampbell/DataWedgeLite/blob/master/doc/screen3.png?raw=true)

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
The easiest way to test this application is with the [DataWedge API Exerciser](https://github.com/darryncampbell/DataWedge-API-Exerciser).

## Limitations

Please note the following limitations / differences compared with Zebra's official DataWedge product:
* The source returned will be either camera-zxing or camera-google-vision.
* The names of the decoders will vary from scan engine to scan engine, specifically when the engine reports the decoding of the barcode, e.g. UPC_A, UPCA etc.  No effort has been made to standardize the names across scanning engines.
* This application does not change the profile dynamically when specific apps are shown.  As a result the the DataWedge "default" profile and associated functionality has not been implemented.
* The configured profiles are removed on uninstall and cannot be exported.
