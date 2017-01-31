package scottm.examples;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.List;


public class SensorTest extends Activity {


    private static final String TAG = "SensorTest";
    private static final int UPDATE_INTERVAL = 200;
    
    private TextView[] sensorValues;
    private SensorManager sensorManager;

    private int eventNumber;
    private boolean displayCurrent;
    private float[] maxVals;

    private long lastTime;
    private boolean useAccelerator;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "in onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        getTextViews();
        maxVals = new float[3];
        displayCurrent = true;

        useAccelerator = true;
        sensorManager =
                (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        lastTime = System.currentTimeMillis() - 1000;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(useAccelerator)
            selectAccelerometer(null);
        else
            selectLinearAcceleration(null);
    }

    //    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "In on pause, unregister sensor");
        sensorManager.unregisterListener(sensorEventListener);
    }

    private void showSensors() {

        List<Sensor> sensors
                = sensorManager.getSensorList(Sensor.TYPE_ALL);

        Log.d(TAG, sensors.toString());

        for(Sensor s : sensors)
            Log.d(TAG, s.getName() + " " + s.getVendor() );

                for(Sensor s : sensors) {
                    Log.d(TAG, s.getName() + " - minDelay: "
                            + s.getMinDelay() + ", power: " + s.getPower());
                    Log.d(TAG, "max range: " + s.getMaximumRange()
                            + ", resolution: " + s.getResolution());
                }
    }

    private void getTextViews() {
        int[] ids = {R.id.x_axis_value, R.id.y_axis_value, R.id.z_axis_value};
        sensorValues = new TextView[3];
        for(int i = 0; i < sensorValues.length; i++)
            sensorValues[i] = (TextView) this.findViewById(ids[i]);
    }

    public void selectAccelerometer(View v) {
        sensorManager.unregisterListener(sensorEventListener);
        useAccelerator = true;

        Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        sensorManager.registerListener(sensorEventListener,
                accelerometer,
                SensorManager.SENSOR_DELAY_UI);

//                sensorManager.registerListener(sensorEventListener,
//                accelerometer,
//                1000000);
    }

    public void selectLinearAcceleration(View v) {
        sensorManager.unregisterListener(sensorEventListener);
        useAccelerator = false;

        Sensor linAclSensor = null;

        // Pick the Google LA Sensor if it exists
        List<Sensor> linearAcceleationSensors = sensorManager.getSensorList(Sensor.TYPE_LINEAR_ACCELERATION);
        for(Sensor s : linearAcceleationSensors)
            if(s.getVendor().toLowerCase().contains("google"))
                linAclSensor = s;


        // pick the default Linear acceleration Sensor if didn't find google sensor
        if(linAclSensor == null) {
            linAclSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        }

        Log.d(TAG, linAclSensor.toString());

        final int FIVE_SECONDS_IN_MICROSECONDS = 5000000;
        sensorManager.registerListener(sensorEventListener,
                linAclSensor,
                500000);
    }


    public void setCurrentOrMax(View v) {
        ToggleButton tb = (ToggleButton) v;
        boolean on = tb.isChecked();
        displayCurrent = !on;
    }


    public void resetMax(View v) {
        for(int i = 0; i < maxVals.length; i++)
            maxVals[i] = 0.0f;
    }

    private void displayMax(SensorEvent event) {
        for(int i = 0; i < maxVals.length; i++)
            if(Math.abs(event.values[i]) > maxVals[i]) {
                maxVals[i] = (float) Math.abs(event.values[i]);
                float value = ((int) (maxVals[i] * 1000)) / 1000f;
                sensorValues[i].setText("" + value);
            }
    }

    // no zeroing
    private void displayCurrent(SensorEvent event) {
//        for(int i = 0; i < sensorValues.length; i++) {
//            float value = event.values[i];
//            value = ((int) (value * 1000)) / 1000f;
//            sensorValues[i].setText("" + value);
//        }

       long currentTime = System.currentTimeMillis();
       if(currentTime - this.lastTime > UPDATE_INTERVAL) {

            for(int i = 0; i < sensorValues.length; i++) {
                float value = event.values[i];
                value = ((int) (value * 1000)) / 1000f;
                sensorValues[i].setText("" + value);
            }
           lastTime = currentTime;
       }
    }

    private SensorEventListener sensorEventListener =
            new SensorEventListener() {

        @Override
        public void onSensorChanged(SensorEvent event) {
            // Log.d(TAG, event.toString());

            // accelerationValues[0].setText("" + event.values[0]);
            if(displayCurrent)
                displayCurrent(event);
            else
                displayMax(event);

            // displayCurrentRotation(event);
        }

        //        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            // nothing to do!
        }

        public void stop() {
            // make sure to turn our sensor off when the activity is paused
            sensorManager.unregisterListener(this);
        }


    };
}

/* Code we were experienting with for rotation sensors:
//              // zeroing
//              private void displayCurrent(SensorEvent event) {
//                  if(!zeroingComplete)
//                      gatherZeroData(event);
//
//
//                      for(int i = 0; i < accelerationValues.length; i++) {
//                          float value = event.values[i];
//                          value = ((int) (value * 1000)) / 1000f;
//                          accelerationValues[i].setText("" + value);
//                      }
//              }

private void gatherZeroData(SensorEvent event) {
    eventNumber++;
    // Log.d(TAG, "gather data, eventNum: " + eventNumber);
    if(eventNumber > SKIP_NUMBER && eventNumber < STOP_NUMBER) {
        for(int i = 0; i < 3; i++) {
            firstVals[i].add(event.values[i]);
        }
    }
    else if(eventNumber == STOP_NUMBER) {
        zeroingComplete = true;
        zeroValues = new float[3];
        for(int i = 0; i < 3; i++) {
            for(float f : firstVals[i]) {
                zeroValues[i] += f;
            }
            zeroValues[i] /= firstVals[i].size();
            Log.d(TAG, "i: " + i + ", zerovalue: " + zeroValues[i]);
        }
        firstVals = null;
    }

}

        private void displayCurrentRotation(SensorEvent event) {
            if(eventNumber % 25 == 0) {
                SensorManager.getRotationMatrixFromVector(
                        rotationMatrix , event.values);
                orientation = SensorManager.getOrientation(rotationMatrix, orientation);

                for(int i = 0; i < sensorValues.length; i++) {
                    float value = orientation[i];
                    // ??????
                    value = ((int) (value * 1000 * 57.2957795)) / 1000f;
                    sensorValues[i].setText("" + value);
                }
            }
            eventNumber++;
        }

        // for onCreate
        rotationMatrix = new float[9];
        orientation = new float[3];
            private float[] rotationMatrix;
    private float[] orientation;

 */