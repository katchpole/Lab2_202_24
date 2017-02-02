package lab0_202_24.uwaterloo.ca.lab1_202_24;

import android.content.Context;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Locale;

/**
 * Created by patri on 2017-01-18.
 */

public class AccelerationHandler extends SensorHandler {

    float[] gravity = new float[3];
    LineGraphView mLineGraphView;
    double[][] accelArray = new double[100][3];
    AccelerationHandler(Context applicationContext, LinearLayout layout, String sensorType, LineGraphView lineGraphView){
        super(applicationContext, layout, sensorType);
        mLineGraphView = lineGraphView;
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

        for (int p = 0; p < 99; ++p){
            for (int k = 0; k < 3; k++){
                accelArray[p+1][k] = accelArray[p][k];
            }
        }
        for(int i = 0; i<3; i++) {
            accelArray[0][i] = v[i];
        }
    }

    public double[][] GetAccelArray(){
        return accelArray;
    }

}
