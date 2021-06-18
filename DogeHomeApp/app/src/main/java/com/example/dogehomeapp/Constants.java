package com.example.dogehomeapp;

/**
 * Adapted from https://github.com/dimitrisraptis96/fall-detection-app/blob/master/FallDetector/app/src/main/java/com/example/dimitris/falldetector/Constants.java
 */
public interface Constants {

    // Message types sent from the Accelerometer Handler
    int MESSAGE_CHANGED = 1;
    int MESSAGE_EMERGENCY = 2;
    int MESSAGE_TOAST = 3;

    // Key names received from the Accelerometer Handler
    String VALUE = "value";
    String TOAST = "toast";

    // Messages to send to the smart hub over tcp
    String OPEN_DOOR = "0";
    String TOGGLE_DOOR = "1";

}