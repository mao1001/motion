package edu.uw.mao1001.motiongame;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.TextView;

import java.util.Arrays;
import java.util.Random;

public class MainActivity extends Activity implements SensorEventListener {

    private static final String TAG = "Motion";

    private TextView txtX, txtY, txtZ;

    private SensorManager mSensorManager;
    private Sensor mSensor;

    private DrawingSurfaceView view;
    private GestureDetectorCompat mDetector;

    private static final int PANEL_COUNT = 3;


    private int xTarget;
    private int yTarget;
    private int zTarget;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        view = (DrawingSurfaceView)findViewById(R.id.drawingView);
        mDetector = new GestureDetectorCompat(this, new MyGestureListener());

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

        generateRandom();


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
    protected void onPause() {
        //unregister sensor
        mSensorManager.unregisterListener(this, mSensor);
        super.onPause();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Log.v(TAG, "Raw: "+ Arrays.toString(event.values));


        float[] rotationMatrix = new float[16];
        SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values);

        float[] orientation = new float[3];
        SensorManager.getOrientation(rotationMatrix, orientation);

        int count = 0;
        int required = PANEL_COUNT;

        for (int i = 0; i < PANEL_COUNT; i++) {
            Panel temp = view.panels.get(i);
            temp.rotationDegree = Math.toDegrees(orientation[i]);
            if (temp.isMatched() && temp.enabled) {
                //Log.d("TAG", "Matched! Target: " + temp.target + " Current: " + temp.rotationDegree);
                count++;
            } else if (!temp.enabled) {
                required--;
            }
        }

        if (count == required) {
            mSensorManager.unregisterListener(this, mSensor);
            view.gameFinished = true;
        }
    }

    public void restart() {
        Log.d(TAG, "Telling surface to restart");
        mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
        generateRandom();
        view.gameFinished = false;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        //leave blank for now
    }

    public void generateRandom() {
        Random rand = new Random();

        zTarget = view.panels.get(0).target = rand.nextInt(180);
        xTarget = view.panels.get(1).target = rand.nextInt(90);
        yTarget = view.panels.get(2).target = rand.nextInt(180);
        Log.d(TAG, "Random generated stuff: " + xTarget + "   " + yTarget + "   " + zTarget);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mDetector.onTouchEvent(event);
        return true;
    }

    private class MyGestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onDown(MotionEvent e) {
            Log.v(TAG, "On down");
            if (view.gameFinished) {
                restart();
            }

            return true; //we're processing this event
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

            //fling!

            for (int i = 0; i < PANEL_COUNT; i++) {
                Panel temp = view.panels.get(i);
                if (temp.canvas.contains(Math.round(e1.getX()), Math.round(e1.getY()))) {
                    Log.v(TAG, "Location is in box #" + (i + 1));
                    temp.enabled = !temp.enabled;
                }
            }

            Log.v(TAG, "Fling! "+ e1.getX() + ", " + e1.getY());

            return true; //we got this
        }
    }
}
