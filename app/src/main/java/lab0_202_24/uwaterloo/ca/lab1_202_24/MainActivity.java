package lab0_202_24.uwaterloo.ca.lab1_202_24;

import android.app.ActionBar;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private TextView tv_light, tv_lightreading, tv_light_high, tv_lightreading_high;
    private TextView tv_accel, tv_accelreading, tv_accel_high, tv_accelreading_high;
    private TextView tv_mag, tv_magreading, tv_mag_high, tv_magreading_high;
    private TextView rot_vec, tv_rot_reading, tv_rot_high, tv_rot_reading_high;

    double maxLight = 0, maxAccel_x = 0, maxAccel_y = 0, maxAccel_z = 0, maxMag_x = 0, maxMag_y = 0, maxMag_z = 0, maxVec_x = 0, maxVec_y = 0, maxVec_z = 0;
    private float[] gravity = new float[3];

    private LineGraphView lineGraphView;

    double[][] accelArray = new double[100][3];     //csv file array


    private SensorManager mSensorManager;
    private Sensor mLightSensor;
    private Sensor mAccelerometer;
    //private float[] mGravity = new float[3];
    private Sensor mMagSensor;
    private Sensor mRotSensor;

    private AccelerationHandler accelerationHandler;
    private GeneralSensorHandler lightHandler, mFieldHandler, rotHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //sensor stuff
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mLightSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        mMagSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        mRotSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);


        //reference linear layout
        LinearLayout layout = (LinearLayout)findViewById(R.id.lin_layout);
        layout.setOrientation(LinearLayout.VERTICAL);


        lineGraphView = new LineGraphView(getApplicationContext(), 100, Arrays.asList("x", "y", "z"));
        lineGraphView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        layout.addView(lineGraphView);
        lineGraphView.setVisibility(View.VISIBLE);

        lightHandler = new GeneralSensorHandler(getApplicationContext(), layout, "light");
        accelerationHandler = new AccelerationHandler(getApplicationContext(), layout, "acceleration", lineGraphView);
        mFieldHandler = new GeneralSensorHandler(getApplicationContext(), layout, "magnetic");
        rotHandler = new GeneralSensorHandler(getApplicationContext(), layout, "rotation");

        /*
        /////////////////////////create required textviews and add to linear layout//////////////////////////////
        //light sensor textviews
        tv_light = new TextView(getApplicationContext());
        layout.addView(tv_light);
        tv_light.setText("The light sensor reading is:");
        tv_lightreading = new TextView(getApplicationContext()); //textview for the reading values
        layout.addView(tv_lightreading);
        tv_lightreading.setText("0.00");

        //record high light sensor textviews
        tv_light_high = new TextView(getApplicationContext());
        layout.addView(tv_light_high);
        tv_light_high.setText("The record-high light sensor reading is:");
        tv_lightreading_high = new TextView(getApplicationContext()); //textview for the reading values
        layout.addView(tv_lightreading_high);
        tv_lightreading_high.setText("0.00");

        //accelerometer textviews
        tv_accel = new TextView(getApplicationContext());
        layout.addView(tv_accel);
        tv_accel.setText("The accelerometer reading is:");
        tv_accelreading = new TextView(getApplicationContext()); //textview for the reading values
        layout.addView(tv_accelreading);
        tv_accelreading.setText("(0, 0, 0)");

        //record high accelerometer textviews
        tv_accel_high = new TextView(getApplicationContext());
        layout.addView(tv_accel_high);
        tv_accel_high.setText("The record-high accelerometer reading is:");
        tv_accelreading_high = new TextView(getApplicationContext()); //textview for the reading values
        layout.addView(tv_accelreading_high);
        tv_accelreading_high.setText("(0, 0, 0)");

        //magnetic sensor textviews
        tv_mag = new TextView(getApplicationContext());
        layout.addView(tv_mag);
        tv_mag.setText("The magnetic sensor reading is:");
        tv_magreading = new TextView(getApplicationContext()); //textview for the reading values
        layout.addView(tv_magreading);
        tv_magreading.setText("(0, 0, 0)");

        //record high magnetic sensor textviews
        tv_mag_high = new TextView(getApplicationContext());
        layout.addView(tv_mag_high);
        tv_mag_high.setText("The record-high magnetic sensor reading reading is:");
        tv_magreading_high = new TextView(getApplicationContext()); //textview for the reading values
        layout.addView(tv_magreading_high);
        tv_magreading_high.setText("(0, 0, 0)");


        //Rotation vector
        rot_vec = new TextView(getApplicationContext());
        layout.addView(rot_vec);
        rot_vec.setText("The rotation vector reading is: ");
        tv_rot_reading = new TextView(getApplicationContext());
        layout.addView(tv_rot_reading);
        tv_rot_reading.setText("(0, 0, 0)");

        //rotation vector highest readings
        tv_rot_high = new TextView(getApplicationContext());
        layout.addView(tv_rot_high);
        tv_rot_high.setText("The record-high rotation vector sensor reading is:");
        tv_rot_reading_high = new TextView(getApplicationContext());
        layout.addView(tv_rot_reading_high);
        tv_rot_reading_high.setText("(0, 0, 0)");

        */
        //BUTTONS
        Button resetButton = new Button(getApplicationContext());
        resetButton.setText("Reset Readings");
        layout.addView(resetButton);

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetMax();
            }
        });


        Button csvDeposit = new Button(getApplicationContext());
        csvDeposit.setText("Deposit Acceleration Readings");
        layout.addView(csvDeposit);
        csvDeposit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fileWrite();
            }
        });



    }



    @Override
    protected void onResume(){
        super.onResume();
        mSensorManager.registerListener(this, mLightSensor, SensorManager.SENSOR_DELAY_GAME);
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_GAME);       //should make SENSOR_DELAY_GAME?
        mSensorManager.registerListener(this, mMagSensor, SensorManager.SENSOR_DELAY_GAME);
        mSensorManager.registerListener(this, mRotSensor, SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onPause(){
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy){
        //Do nothing...
    }

    @Override
    public void onSensorChanged(SensorEvent event){

        //changes in light sensor
        if(event.sensor.getType() == Sensor.TYPE_LIGHT){
            float[] values = new float[1];
            values[0] = event.values[0];
            lightHandler.HandleOutput(values);
            /*tv_lightreading.setText(String.format("%.2f",event.values[0]));
            //check if max light achieved
            float currLight = event.values[0];
            if(currLight > maxLight){
                maxLight = currLight;
                tv_lightreading_high.setText(String.format("%.2f",maxLight));
            }*/
        }

        //changes in accelerometer
        else if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
            accelerationHandler.HandleOutput(event.values);
            accelArray = accelerationHandler.GetAccelArray();
            /*float alpha = (float) 0.8;

            gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0];
            gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1];
            gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2];
            
            float[] acc = new float[3];
            acc[0] = event.values[0] - gravity[0];
            acc[1] = event.values[1] - gravity[1];
            acc[2] = event.values[2] - gravity[2];
            
            tv_accelreading.setText("(" + String.format("%.2f",acc[0]) + ", " + String.format("%.2f",acc[1]) + ", " + String.format("%.2f",acc[2]) + ")");
            //check if max acceleration components achieved
            if(Math.abs(acc[0]) > Math.abs(maxAccel_x)){
                maxAccel_x = acc[0];
            }
            if(Math.abs(acc[1]) > Math.abs(maxAccel_y)){
                maxAccel_y = acc[1];
            }
            if(Math.abs(acc[2]) > Math.abs(maxAccel_z)){
                maxAccel_z = acc[2];
            }
            tv_accelreading_high.setText("(" + String.format("%.2f",maxAccel_x) + ", " + String.format("%.2f",maxAccel_y) + ", " + String.format("%.2f",maxAccel_z) + ")");

           //update csv array

                for(int p = 0;  p < 99; p++){           //increment all elements one space over, erasing last element
                    for (int k = 0; k< 3; k++)
                        accelArray[p+1][k] = accelArray[p][k];
                }
            lineGraphView.addPoint(acc);

            for(int i = 0; i<3; i++){                   //store current reading in first spot
                accelArray[0][i] = acc[i];

           }*/

        }

        //changes in magnetic sensor
        else if(event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD){
            mFieldHandler.HandleOutput(event.values);
            /*tv_magreading.setText("(" + String.format("%.2f",event.values[0]) + ", " + String.format("%.2f",event.values[1]) + ", " + String.format("%.2f",event.values[2]) + ")");
            //check if max acceleration components achieved
            if(Math.abs(event.values[0]) > Math.abs(maxMag_x)){
                maxMag_x = event.values[0];
            }
            if(Math.abs(event.values[1]) > Math.abs(maxMag_y)){
                maxMag_y = event.values[1];
            }
            if(Math.abs(event.values[2]) > Math.abs(maxMag_z)){
                maxMag_z = event.values[2];
            }
            tv_magreading_high.setText("(" + String.format("%.2f",maxMag_x) + ", " + String.format("%.2f",maxMag_y) + ", " + String.format("%.2f",maxMag_z) + ")");
            */
        }
        //Changes in Rotation Vector

        else if(event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR){
            rotHandler.HandleOutput(event.values, 4);
            /*
            tv_rot_reading.setText("(" + String.format("%.2f",event.values[0]) + ", " + String.format("%.2f",event.values[1]) + ", " + String.format("%.2f",event.values[2]) + ")");;

            //check for max reading
            if(Math.abs(event.values[0]) > Math.abs(maxVec_x)){
                maxVec_x = event.values[0];
            }
            if(Math.abs(event.values[1]) > Math.abs(maxVec_y)){
                maxVec_y = event.values[1];
            }
            if(Math.abs(event.values[2]) > Math.abs(maxVec_z)){
                maxVec_z = event.values[2];
            }
            tv_rot_reading_high.setText("(" + String.format("%.2f",maxVec_x) + ", " + String.format("%.2f",maxVec_y) + ", " + String.format("%.2f",maxVec_z) + ")");;
            */
        }
    }

    public void fileWrite(){
        File accelRead = null;
        PrintWriter writer1 = null;

        try{
            accelRead = new File(getExternalFilesDir("Lab 1"), "AccelReadings.csv");
            writer1 = new PrintWriter(accelRead);

            for( int i = 0; i< 99; i++) {
                writer1.println(accelArray[i][0] + ", " + accelArray[i][1] + ", " + accelArray[i][2]);
            }
        }
        catch(IOException ex1){
            Log.d("Lab 1", "Failed to Write File: " + ex1.toString());
        }
        finally{
           if(writer1 != null){
               writer1.flush();
               writer1.close();
           }
        }
    }

    public  void resetMax(){
        accelerationHandler.Reset();
        lightHandler.Reset();
        rotHandler.Reset();
        mFieldHandler.Reset();

        /*maxAccel_x = 0; maxAccel_y = 0; maxAccel_z = 0;
        tv_accelreading_high.setText("(0, 0, 0)");
        maxLight = 0;
        tv_lightreading_high.setText("0.00");
        maxMag_x = 0; maxMag_y = 0; maxMag_z = 0;
        tv_magreading_high.setText("(0, 0, 0)");
        maxVec_x = 0; maxVec_y = 0; maxVec_z = 0;
        tv_rot_reading_high.setText("(0, 0, 0)");
        */
    }

}
