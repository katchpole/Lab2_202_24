package lab0_202_24.uwaterloo.ca.lab1_202_24;

import android.content.Context;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Locale;

/**
 * Created by patri on 2017-01-18.
 */

public class AccelerationHandler extends SensorHandler {
    enum GestureState{UNDEFINED, RIGHT_PEAK, LEFT_PEAK, LEFT_FALL, RIGHT_FALL, UP_PEAK, UP_FALL, DOWN_PEAK, DOWN_FALL};
    GestureState CurrentState = GestureState.UNDEFINED;
    int GestureTimeout = 0;

    float[] gravity = new float[3];
    private final float C = 12.0f;
    LineGraphView mLineGraphView;
    LineGraphView mLineGraphView2;
    double[][] accelArray = new double[100][3];
    GestureCallback mGestureCallback;

    AccelerationHandler(Context applicationContext, LinearLayout layout, String sensorType, LineGraphView lineGraphView, LineGraphView lineGraphView2, GestureCallback gestureCallback){
        super(applicationContext, layout, sensorType);
        mLineGraphView = lineGraphView;
        mLineGraphView2 = lineGraphView2;
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
        mLineGraphView.addPoint(v);

        for (int i = 1; i < 100; ++i){
            for (int j = 0; j < 3; ++j){
                accelArray[i-1][j] = accelArray[i][j];
            }
        }
        for(int i = 0; i<3; i++) {
            accelArray[99][i] += (v[i] - accelArray[99][i])/C;
            v[i] = (float) accelArray[99][i];
        }
        mLineGraphView2.addPoint(v);
        GestureDetect();
    }

    public void GestureDetect(){
        /*
        * Threshold Data
        * EDGE A THRESH -> SET STATE "PEAK"
        * EDGE B REBOUND THRESH -> SET STATE "FALL"
        * */
        double[] CurrentValues = accelArray[99];
        double[] RIGHT_THRESH = {1.0, -1.0};
        double[] LEFT_THRESH = {-1.0, 1.0};
        double[] UP_THRESH = RIGHT_THRESH;
        double[] DOWN_THRESH = LEFT_THRESH;
        
        switch (CurrentState){
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
            case RIGHT_PEAK:
                if (CurrentValues[0] < RIGHT_THRESH[1]){
                    CurrentState = GestureState.RIGHT_FALL;
                }
                break;
            case RIGHT_FALL:
                if (CurrentValues[0] > RIGHT_THRESH[1]){
                    mGestureCallback.onGestureDetect(GestureCallback.Direction.RIGHT);
                    CurrentState = GestureState.UNDEFINED;
                }
                break;
            case LEFT_PEAK:
                if (CurrentValues[0] > LEFT_THRESH[1]){
                    CurrentState = GestureState.LEFT_FALL;
                }
                break;
            case LEFT_FALL:
                if (CurrentValues[0] < LEFT_THRESH[1]){
                    mGestureCallback.onGestureDetect(GestureCallback.Direction.LEFT);
                    CurrentState = GestureState.UNDEFINED;
                }
                break;
            case UP_PEAK:
                if (CurrentValues[1] < UP_THRESH[1]){
                    CurrentState = GestureState.UP_FALL;
                }
                break;
            case UP_FALL:
                if (CurrentValues[1] > UP_THRESH[1]){
                    mGestureCallback.onGestureDetect(GestureCallback.Direction.UP);
                    CurrentState = GestureState.UNDEFINED;
                }
                break;
            case DOWN_PEAK:
                if (CurrentValues[1] > DOWN_THRESH[1]){
                    CurrentState = GestureState.DOWN_FALL;
                }
                break;
            case DOWN_FALL:
                if (CurrentValues[1] < DOWN_THRESH[1]){
                    mGestureCallback.onGestureDetect(GestureCallback.Direction.DOWN);
                    CurrentState = GestureState.UNDEFINED;
                }
                break;
        }

        if (GestureTimeout++ > 30){
            CurrentState = GestureState.UNDEFINED;
        }
    }

    public double[][] GetAccelArray(){
        return accelArray;
    }

}
