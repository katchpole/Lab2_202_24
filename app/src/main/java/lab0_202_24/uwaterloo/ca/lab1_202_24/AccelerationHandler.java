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
    private final float C = 12.0f;
    LineGraphView mLineGraphView;
    LineGraphView mLineGraphView2;
    double[][] accelArray = new double[100][3];
    AccelerationHandler(Context applicationContext, LinearLayout layout, String sensorType, LineGraphView lineGraphView, LineGraphView lineGraphView2){
        super(applicationContext, layout, sensorType);
        mLineGraphView = lineGraphView;
        mLineGraphView2 = lineGraphView2;
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
    }

    public double[][] GetAccelArray(){
        return accelArray;
    }

}
