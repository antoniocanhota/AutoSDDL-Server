AutoSDDL is an application to detect possible vehicle crash based on deacceleration values and a requirement for "Introduction of Ubiquitous and Mobile Computing" course at departament of informatics at PUC-RIO.

This project is based on ContextNet (http://www.lac-rio.com/dokuwiki/doku.php).

#Overview

The AutoSDDL are composed of 3 applications and one lib.
* Pub-Android - Application that run on Android devices (version 4.2 or greater) and connects to an OBD-2 bluetooth adapter. It sends peridically the speed, localization and acceleration of the vehicle to Server application
* Server - The server itself. It receives vehicle data from "Pub-Android" and process them using Esper (http://www.espertech.com/).
* Sub-Android - Android (> 4.2) application responsable for receiving alerts from a vehicle reported by the "Server".
* Commons - Common classes for the applications above.
These applications were mainly based on HelloAndroid example (http://www.lac-rio.com/dokuwiki/doku.php?id=helloandroid) and also used OBD-Java-Api (https://github.com/pires/obd-java-api).

#How to run

## Server

1. On Eclipse IDE, import the applications and common-lib from Github
2. Export a runnable jar from AutoSDDL-Server
3. You should have OpenSplice installed before: see instructions here http://www.lac-rio.com/dokuwiki/doku.php?id=installingdds
4. Run gateway.jar (see http://www.lac-rio.com/dokuwiki/doku.php?id=download)
5. Run the exported runnable jar file

## Pub-Android

6. Generate APK from Pub-Android and install it on Android device
7. Connect a bluetooth adapter to vehicle OBD-2
8. Open 'AutoSDDL Pub' and select the OBD-2 adapter at launch
9. Fill-in the 'License plate' field
10. Click on 'Start'
11. Drive

## Sub-Android

12. Generate APK from Sub-Android and install it on another Android device
13. Open 'AutoSDDL Sub'
14. Fill-in the 'License plate' field
15. Click on 'Start'
16. An alert will be displayed if the selected vehicle (based on license plate) deaccelerates bellow -3m/s2.



