package com.example.dogehomeapp.core;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.example.dogehomeapp.Constants;

/**
 * Adapted from https://github.com/dimitrisraptis96/fall-detection-app/blob/master/FallDetector/app/src/main/java/com/example/dimitris/falldetector/core/Accelerometer.java
 */
public class Accelerometer implements SensorEventListener {

    public static final String TAG = "Accelerometer";

    private final Sensor mSensor;
    private final SensorManager mSensorManager;

    private final Handler mHandler;

    private long lastUpdate = -1;

    private float[] last_accel_values;

    private final static int CHECK_INTERVAL = 100; // [msec]

    private final Window mWindow;

    public Accelerometer(SensorManager sm, Sensor s, Handler h) {
        mSensorManager = sm;
        mSensor = s;
        mHandler = h;
        mWindow = new Window(1000 / CHECK_INTERVAL); //sampling for 1 sec
    }

    public void startListening() {
        if (mSensor == null) {
            // Send a failure message back to the Activity
            Message msg = mHandler.obtainMessage(Constants.MESSAGE_TOAST);
            Bundle bundle = new Bundle();
            bundle.putString(Constants.TOAST, "Unable to find accelerometer");
            msg.setData(bundle);
            mHandler.sendMessage(msg);

        } else {
            mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_FASTEST); //sampling every 0.2sec => 5Hz
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) { /*Safe not to implement*/ }


    @Override
    public void onSensorChanged(SensorEvent event) {
        long curTime = System.currentTimeMillis();

        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            // sampling frequency f= 10Hz.
            if ((curTime - lastUpdate) > CHECK_INTERVAL) {

                lastUpdate = curTime;

                float[] accel_values = event.values.clone();

                if (last_accel_values != null) {

                    float mAccelCurrent = (float) Math.sqrt(accel_values[0] * accel_values[0] + accel_values[1] * accel_values[1]
                            + accel_values[2] * accel_values[2]);

                    Message msg = mHandler.obtainMessage(Constants.MESSAGE_CHANGED);
                    Bundle bundle = new Bundle();
                    bundle.putFloat(Constants.VALUE, mAccelCurrent);
                    msg.setData(bundle);
                    mHandler.sendMessage(msg);

                    mWindow.add(mAccelCurrent);
                    if (mWindow.isFull() && mWindow.isFallDetected()) {
                        Log.w(TAG, "Fall detected by window class");
                        mWindow.clear();
                        msg = mHandler.obtainMessage(Constants.MESSAGE_EMERGENCY);
                        mHandler.sendMessage(msg);
                    }
                }

                last_accel_values = accel_values.clone();
            }
        }
    }

}
