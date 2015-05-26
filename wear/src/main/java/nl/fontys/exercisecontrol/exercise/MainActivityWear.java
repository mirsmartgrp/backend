package nl.fontys.exercisecontrol.exercise;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Sensor;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;

import nl.fontys.exercisecontrol.exercise.collector.DataEntry;
import nl.fontys.exercisecontrol.exercise.collector.ExerciseData;
import nl.fontys.exercisecontrol.exercise.collector.JsonMeasurementCollector;
import nl.fontys.exercisecontrol.exercise.recorder.MeasurementCollector;
import nl.fontys.exercisecontrol.exercise.recorder.MeasurementException;
import nl.fontys.exercisecontrol.exercise.recorder.MeasurementRecorder;

public class MainActivityWear extends Activity {

    private ConnectionHandler handler;
    private MeasurementRecorder recorder;
    private MeasurementCollector collector;
    private Chronometer chronometer;
    private TextView headerLbl;
    private String exerciseName="unknown exercise";
    private String exerciseGUID ="";
    private final static String TAG="LOG";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        handler = new ConnectionHandler(this);

        collector = new JsonMeasurementCollectorImpl();
        recorder = new MeasurementRecorder(this,initSensors(), 1, collector);
        recorder.initialize();

    }

    private String getExerciseName() {
        Intent intent = getIntent();
        return intent.getStringExtra(SelectExerciseActivityWear.EXERCISE_NAME);
    }
    private String getExerciseGUID() {
        Intent intent = getIntent();
        return intent.getStringExtra(SelectExerciseActivityWear.EXERCISE_GUID);
    }

    /**
     * setting the name of the exerccise to the header
     */
    private void setExerciseNameToHeaderLbl() {
        headerLbl = (TextView ) findViewById(R.id.headerLbl);
        exerciseName = getExerciseName();
        headerLbl.setText(exerciseName);
    }
    /**
     * create sensor data
     */
    private int[] initSensors() {
        int[] sensors = new int[2];
        sensors[0]=Sensor.TYPE_GYROSCOPE;
        sensors[1]=Sensor.TYPE_LINEAR_ACCELERATION;
        return sensors;
    }
    @Override
    protected void onResume(){
        super.onResume();
      //  recorder.start();
    }
    @Override
    public void onDestroy() {
    super.onDestroy();
    handler.disconnectGoogleClient();
    recorder.stop();
    recorder.terminate();
    }

    /**
     * start the measurement
     * start chronometer
     * set the Exercise nameS
     * @param view
     */
    public void start(View view) {
        if(isConnectedToPhone()) {
            exerciseGUID = getExerciseGUID();
            chronometer = (Chronometer) findViewById(R.id.chronometer);
            chronometer.setBase(SystemClock.elapsedRealtime()); //reset to 0
            recorder.start(exerciseGUID);
            chronometer.start();
            setExerciseNameToHeaderLbl();
        }
        else {
            showToast("could not establish connection to phone", Toast.LENGTH_LONG);
        }

    }

    /**
     * TODO: enable, disabled during debug
     * check if phone and watch are connected
     * @return
     */
    private boolean isConnectedToPhone() {
     /*
        if(!handler.isConnected()) {
            handler.connectGogleClient();
        }
        return handler.isConnected();
        */
        return true;
    }
    /**
     * stop the measurement
     * stop chronometer
     * @param view
     */
    public void stop(View view) {
        chronometer = (Chronometer) findViewById(R.id.chronometer);
        chronometer.stop();
        recorder.stop();
    }

    /**
     * display a toast message
     * @param message text to display
     * @param length time how long the display is show
     */
    private void showToast(String message, int length) {
        Toast toast = Toast.makeText(this, message, length);
        toast.show();
    }
    private class JsonMeasurementCollectorImpl extends JsonMeasurementCollector {

        @Override
        public void startCollecting(String exerciseName) throws MeasurementException {
            Log.d(TAG, "Started collecting measurements.");
            super.startCollecting(exerciseGUID);
        }

        @Override
        public void stopCollecting(double time) throws MeasurementException {
            super.stopCollecting(time);
            Log.d(TAG, "Stopped collecting measurements. Recorded length: " + time);
        }

        @Override
        public void collectionComplete(ExerciseData data) {
            Log.d("log","measurement completed");
             Log.d(TAG,"guid: "+data.getGuid());
            for(DataEntry d:data.getData()) {
                Log.d(TAG,"dataEntry: "+d);
            }
            //sendMessage(gson.toString());
        }

        /**
         * send message with text and show toast
         * @param text text of message
         **/
        private void sendMessage(String text) {
             if(isConnectedToPhone()) {
                handler.sendMessage(text);
                showToast("data send to phone", Toast.LENGTH_LONG);
            }
            else {
                    showToast("no connection to phone.", Toast.LENGTH_LONG);
            }

        }
        @Override
        public void collectionFailed(MeasurementException ex) {
            Log.d(TAG, "Measurement failed: " + ex.getMessage());
            showToast("Measurement failed", Toast.LENGTH_LONG);
        }
    }
}
