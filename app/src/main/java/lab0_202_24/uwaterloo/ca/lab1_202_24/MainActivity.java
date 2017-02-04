package lab0_202_24.uwaterloo.ca.lab1_202_24;

import android.app.ActionBar;
import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
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

public class MainActivity extends AppCompatActivity implements SensorEventListener, GestureCallback{

    private LineGraphView lineGraphView;
    private LineGraphView lineGraphView2;
    private TextView textViewGestureStatus;
    double[][] accelArray = new double[100][3];     //csv file array


    private SensorManager mSensorManager;

    private Sensor mAccelerometer;


    private AccelerationHandler accelerationHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //sensor stuff
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);



        //reference linear layout
        LinearLayout layout = (LinearLayout)findViewById(R.id.lin_layout);
        layout.setOrientation(LinearLayout.VERTICAL);


        lineGraphView = new LineGraphView(getApplicationContext(), 100, Arrays.asList("x", "y", "z"));
        lineGraphView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        layout.addView(lineGraphView);
        lineGraphView.setVisibility(View.VISIBLE);

        lineGraphView2 = new LineGraphView(getApplicationContext(), 100, Arrays.asList("x", "y", "z"));
        lineGraphView2.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        layout.addView(lineGraphView2);
        lineGraphView2.setVisibility(View.VISIBLE);

        accelerationHandler = new AccelerationHandler(getApplicationContext(), layout, "acceleration", lineGraphView, lineGraphView2, this);

        textViewGestureStatus = new TextView(getApplicationContext());
        textViewGestureStatus.setGravity(Gravity.CENTER_HORIZONTAL);
        textViewGestureStatus.setTextSize(26);
        textViewGestureStatus.setTextColor(Color.WHITE);
        layout.addView(textViewGestureStatus);

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
    public void onGestureDetect(Direction direction){
        if (direction == Direction.RIGHT){
            textViewGestureStatus.setText("RIGHT");
        }else if (direction == Direction.LEFT){
            textViewGestureStatus.setText("LEFT");
        }else if (direction == Direction.UP){
            textViewGestureStatus.setText("UP");
        }else if (direction == Direction.DOWN){
            textViewGestureStatus.setText("DOWN");
        }
    }


    @Override
    protected void onResume(){
        super.onResume();
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_GAME);       //should make SENSOR_DELAY_GAME?
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
        //changes in accelerometer
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
            accelerationHandler.HandleOutput(event.values);
            accelArray = accelerationHandler.GetAccelArray();
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



    }

}
