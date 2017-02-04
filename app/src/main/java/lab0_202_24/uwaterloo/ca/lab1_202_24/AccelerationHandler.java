package lab0_202_24.uwaterloo.ca.lab1_202_24;

import android.content.Context;
import android.widget.LinearLayout;

/**
 * Created by patri on 2017-01-18.
 */

public class AccelerationHandler extends SensorHandler {

    private enum GestureState{UNDEFINED, RIGHT_PEAK, LEFT_PEAK, LEFT_FALL, RIGHT_FALL, UP_PEAK, UP_FALL, DOWN_PEAK, DOWN_FALL};
    private GestureState CurrentState = GestureState.UNDEFINED;
    private int GestureTimeout = 0;

    private float[] gravity = new float[3];
    private final float FILTER_RATE = 12.0f;
    private LineGraphView preFilterLGV, postFilterLGV;
    private double[][] accelArray = new double[100][3];
    private GestureCallback mGestureCallback;

    AccelerationHandler(Context applicationContext, LinearLayout layout, String sensorType, LineGraphView preFilterLGV, LineGraphView postFilterLGV, GestureCallback gestureCallback){
        super(applicationContext, layout, sensorType);
        this.preFilterLGV = preFilterLGV;
        this.postFilterLGV = postFilterLGV;
        mGestureCallback = gestureCallback;
    }

    @Override
    protected float[] ProcessData(float[] values){
        float alpha = (float) 0.8;

        gravity[0] = alpha * gravity[0] + (1 - alpha) * values[0];
        gravity[1] = alpha * gravity[1] + (1 - alpha) * values[1];
        gravity[2] = alpha * gravity[2] + (1 - alpha) * values[2];

        float[] acc = new float[3];
        acc[0] = values[0] - gravity[0];
        acc[1] = values[1] - gravity[1];
        acc[2] = values[2] - gravity[2];

        return acc;
    }

    @Override
    public void HandleOutput(float[] v){
        HandleOutput(v, v.length);
    }
    @Override
    public void HandleOutput(float[] v, int maxLen) {
        super.HandleOutput(v, maxLen);
        v = ProcessData(v);
        preFilterLGV.addPoint(v);

        for (int i = 1; i < 100; ++i){
            for (int j = 0; j < 3; ++j){
                accelArray[i-1][j] = accelArray[i][j];
            }
        }
        for(int i = 0; i<3; i++) {
            accelArray[99][i] += (v[i] - accelArray[99][i])/ FILTER_RATE;
            v[i] = (float) accelArray[99][i];
        }
        postFilterLGV.addPoint(v);
        GestureDetect();
    }

    private void GestureDetect(){

        /*
        * Threshold Data
        * EDGE A PEAK THRESH -> SET STATE "PEAK"
        * EDGE B REBOUND THRESH -> SET STATE "FALL"
        */

        /*
        Ex.
             ---
            -   - <- Peak Thresh     --Neutral--
        ----     -          ---------------
                   -       -
            Rebound->-   -
                       -
        */
        double[] CurrentValues = accelArray[99];
        double[] RIGHT_THRESH = {1.0, -1.0};
        double[] LEFT_THRESH = {-1.0, 1.0};
        double[] UP_THRESH = RIGHT_THRESH;
        double[] DOWN_THRESH = LEFT_THRESH;
        
        switch (CurrentState){
            //Detect initial thresholds when in undefined state
            case UNDEFINED:
                if (CurrentValues[1] > UP_THRESH[0]) {
                    CurrentState = GestureState.UP_PEAK;
                }else if (CurrentValues[1] < DOWN_THRESH[0]){
                    CurrentState = GestureState.DOWN_PEAK;
                }else if (CurrentValues[0] > RIGHT_THRESH[0]){
                    CurrentState = GestureState.RIGHT_PEAK;
                }else if (CurrentValues[0] < LEFT_THRESH[0]){
                    CurrentState = GestureState.LEFT_PEAK;
                }
                GestureTimeout = 0;
                break;
            //When detected peak, check for negative rebound of signal
            case RIGHT_PEAK:
                if (CurrentValues[0] < RIGHT_THRESH[1]){
                    CurrentState = GestureState.RIGHT_FALL;
                }
                break;
            case LEFT_PEAK:
                if (CurrentValues[0] > LEFT_THRESH[1]){
                    CurrentState = GestureState.LEFT_FALL;
                }
                break;
            case UP_PEAK:
                if (CurrentValues[1] < UP_THRESH[1]){
                    CurrentState = GestureState.UP_FALL;
                }
                break;
            case DOWN_PEAK:
                if (CurrentValues[1] > DOWN_THRESH[1]){
                    CurrentState = GestureState.DOWN_FALL;
                }
                break;
            //When in rebound state look for back to less than rebound state
            //When the state completes, send a callback to the passed GestureCallback for further handling
            //Resets to undefined state
            case RIGHT_FALL:
                if (CurrentValues[0] > RIGHT_THRESH[1]){
                    mGestureCallback.onGestureDetect(GestureCallback.Direction.RIGHT);
                    CurrentState = GestureState.UNDEFINED;
                }
                break;
            case LEFT_FALL:
                if (CurrentValues[0] < LEFT_THRESH[1]){
                    mGestureCallback.onGestureDetect(GestureCallback.Direction.LEFT);
                    CurrentState = GestureState.UNDEFINED;
                }
                break;
            case UP_FALL:
                if (CurrentValues[1] > UP_THRESH[1]){
                    mGestureCallback.onGestureDetect(GestureCallback.Direction.UP);
                    CurrentState = GestureState.UNDEFINED;
                }
                break;

            case DOWN_FALL:
                if (CurrentValues[1] < DOWN_THRESH[1]){
                    mGestureCallback.onGestureDetect(GestureCallback.Direction.DOWN);
                    CurrentState = GestureState.UNDEFINED;
                }
                break;
        }

        //The system timesout and resets if in a non-undefined state for more than 30 cycles
        if (GestureTimeout++ > 30){
            CurrentState = GestureState.UNDEFINED;
        }
    }

    public double[][] GetAccelArray(){
        return accelArray;
    }

}
