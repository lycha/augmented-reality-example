package com.lycha.example.augmentedreality;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

/**
 * Created by krzysztofjackowski on 24/09/15.
 */
public class MyCurrentAzimuth implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor sensor;
    private int azimuthFrom = 0;
    private int azimuthTo = 0;
    private OnAzimuthChangedListener mAzimuthListener;
    Context mContext;

    public MyCurrentAzimuth(OnAzimuthChangedListener azimuthListener, Context context) {
        mAzimuthListener = azimuthListener;
        mContext = context;
    }

    public void start(){
        sensorManager = (SensorManager) mContext.getSystemService(mContext.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        sensorManager.registerListener(this, sensor,
                SensorManager.SENSOR_DELAY_UI);
    }

    public void stop(){
        sensorManager.unregisterListener(this);
    }

    public void setOnShakeListener(OnAzimuthChangedListener listener) {
        mAzimuthListener = listener;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        azimuthFrom = azimuthTo;

        float[] orientation = new float[3];
        float[] rMat = new float[9];
        SensorManager.getRotationMatrixFromVector(rMat, event.values);
        azimuthTo = (int) ( Math.toDegrees( SensorManager.getOrientation( rMat, orientation )[0] ) + 360 ) % 360;

        mAzimuthListener.onAzimuthChanged(azimuthFrom, azimuthTo);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
