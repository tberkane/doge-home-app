
package com.example.dogehomeapp.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.dogehomeapp.Constants;
import com.example.dogehomeapp.R;
import com.example.dogehomeapp.TcpClient;
import com.example.dogehomeapp.core.Accelerometer;

/**
 * Adapted from https://github.com/dimitrisraptis96/fall-detection-app/blob/master/FallDetector/app/src/main/java/com/example/dimitris/falldetector/ui/StartActivity.java
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String DEFAULT_HOST = "0.tcp.eu.ngrok.io";
    private static final int DEFAULT_PORT = 18307;
    private String phoneNumber = "33629172811"; // the phone number to call in case of an emergency

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnStart = findViewById(R.id.btn_toggle_door);
        btnStart.setOnClickListener(this);

        SensorManager mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        Sensor mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        Button buttonDone = findViewById(R.id.button_done);
        EditText editTextPhone = findViewById(R.id.editTextPhone);
        buttonDone.setOnClickListener(v -> { // save phone number
            phoneNumber = editTextPhone.getText().toString();
            editTextPhone.setText("");
        });

        final Accelerometer accelerometer = new Accelerometer(mSensorManager, mSensor, mHandler);

        accelerometer.startListening();

        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CALL_PHONE}, 1);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_toggle_door) {
            TcpClient client = new TcpClient(DEFAULT_HOST, DEFAULT_PORT);
            new Thread(() -> client.sendMessage(Constants.TOGGLE_DOOR)).start(); // send message to smart hub to toggle doorlock
        }
    }

    @SuppressLint("HandlerLeak")
    private final Handler mHandler = new Handler() {
        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constants.MESSAGE_CHANGED:
                    break;
                case Constants.MESSAGE_EMERGENCY:
                    TcpClient client = new TcpClient(DEFAULT_HOST, DEFAULT_PORT);
                    new Thread(() -> client.sendMessage(Constants.OPEN_DOOR)).start(); // send message to smart hub to open doorlock
                    Toast.makeText(getApplicationContext(), "Fall detected", Toast.LENGTH_LONG).show();
                    Intent callIntent = new Intent(android.content.Intent.ACTION_CALL, Uri.parse("tel:" + phoneNumber));
                    callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(callIntent); // launch call to health assistant
                    break;
                case Constants.MESSAGE_TOAST:
                    Toast.makeText(getApplicationContext(), msg.getData().getString(Constants.TOAST),
                            Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

}
