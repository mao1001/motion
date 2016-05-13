package edu.uw.mao1001.motiongame;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private static final String TAG = "Motion";

    private TextView txtX, txtY, txtZ;

    private SensorManager mSensorManager;
    private Sensor mSensor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //views for easy access
        txtX = (TextView)findViewById(R.id.txt_x);
        txtY = (TextView)findViewById(R.id.txt_y);
        txtZ = (TextView)findViewById(R.id.txt_z);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GAME_ROTATION_VECTOR);
        }
        else{
            mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR); //otherwise use the magnetometer-based one
        }

        Log.i(TAG, "Sensors Available: ");
        for(Sensor s : mSensorManager.getSensorList(Sensor.TYPE_ALL))
            Log.i(TAG, s.toString());


        if(mSensor == null) { //we don't have a relevant sensor
            Log.v(TAG, "No sensor");
            finish();
        }
    }

    @Override
    protected void onResume() {
        //register sensor
        mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
        super.onResume();
    }
    @Override
    public void onSensorChanged(SensorEvent event) {
        Log.v(TAG, "Raw: "+ Arrays.toString(event.values));

//        txtX.setText(String.format("%.3f",event.values[0]));
//        txtY.setText(String.format("%.3f",event.values[1]));
//        txtZ.setText(String.format("%.3f",event.values[2]));

        float[] rotationMatrix = new float[16];
        SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values);

        float[] orientation = new float[3];
        SensorManager.getOrientation(rotationMatrix, orientation);

        txtX.setText(String.format("%.3f",Math.toDegrees(orientation[1]))+"\u00B0");
        txtY.setText(String.format("%.3f",Math.toDegrees(orientation[2]))+"\u00B0");
        txtZ.setText(String.format("%.3f",Math.toDegrees(orientation[0]))+"\u00B0");
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        //leave blank for now
    }
}
