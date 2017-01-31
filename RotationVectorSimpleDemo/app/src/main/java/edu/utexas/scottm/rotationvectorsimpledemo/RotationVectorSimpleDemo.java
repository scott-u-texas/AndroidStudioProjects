package edu.utexas.scottm.rotationvectorsimpledemo;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;

public class RotationVectorSimpleDemo extends Activity {

    private SensorManager mSensorManager;
    private Sensor mRotationVectorSensor;
    private TextView[] mSensorValues;
    private int mSensorInUse;
    private SensorEventListener mRotationListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rotation_vector_simple_demo);

        // Get an instance of the SensorManager
        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        getTextViews();
    }

    private void getTextViews() {
        int[] ids = {R.id.x_value, R.id.y_value, R.id.z_value,
                R.id.cos_value, R.id.heading_value};
        mSensorValues = new TextView[5];
        for(int i = 0; i < mSensorValues.length; i++)
            mSensorValues[i] = (TextView) this.findViewById(ids[i]);
    }


    @Override
    protected void onResume() {
        // Ideally a game should implement onResume() and onPause()
        // to take appropriate action when the activity loses focus
        super.onResume();
        if (mSensorManager.getDefaultSensor(Sensor.TYPE_GAME_ROTATION_VECTOR) != null) {
            mSensorInUse = Sensor.TYPE_GAME_ROTATION_VECTOR;
        } else {
            mSensorInUse = Sensor.TYPE_ROTATION_VECTOR;
        }
        mRotationVectorSensor = mSensorManager.getDefaultSensor(
                mSensorInUse);
        mRotationListener = new RotationListener();
        mSensorManager.registerListener(mRotationListener,
                mRotationVectorSensor, 200000);
    }

    @Override
    protected void onPause() {
        // Ideally a game should implement onResume() and onPause()
        // to take appropriate action when the activity loses focus
        super.onPause();
        mSensorManager.unregisterListener(mRotationListener);
    }

    private  class RotationListener implements SensorEventListener {


        public void onSensorChanged(SensorEvent event) {
            // we received a sensor event. it is a good practice to check
            // that we received the proper event
            if (event.sensor.getType() == mSensorInUse) {
                for(int i = 0; i < event.values.length; i++) {
                    float value = event.values[i];
                    value = ((int) (value * 100)) / 100f;
                    mSensorValues[i].setText("" + value);
                }

//                // convert the rotation-vector to a 4x4 matrix. the matrix
//                // is interpreted by Open GL as the inverse of the
//                // rotation-vector, which is what we want.
//                SensorManager.getRotationMatrixFromVector(
//                        mRotationMatrix , event.values);

            }
        }

        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    }
}
