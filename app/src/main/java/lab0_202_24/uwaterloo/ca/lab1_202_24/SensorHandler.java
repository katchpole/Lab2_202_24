package lab0_202_24.uwaterloo.ca.lab1_202_24;

import android.content.Context;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Locale;
import java.util.Vector;

/**
 * Created by patri on 2017-01-18.
 */

public abstract class SensorHandler {
    TextView mOutputLabel, mOutputValue, mMaxOutputLabel, mMaxOutputValue;
    Vector<Float> mMaximumValues;

    SensorHandler(Context applicationContext, LinearLayout layout, String sensorType){
        mMaximumValues = new Vector<>();

        mOutputLabel = new TextView(applicationContext);
        mOutputValue = new TextView(applicationContext);
        mMaxOutputLabel = new TextView(applicationContext);
        mMaxOutputValue = new TextView(applicationContext);

        layout.addView(mOutputLabel);
        layout.addView(mOutputValue);
        layout.addView(mMaxOutputLabel);
        layout.addView(mMaxOutputValue);


        mOutputLabel.setText(String.format("The %s sensor reading is: ", sensorType));
        mMaxOutputLabel.setText(String.format("The maximum %s reading is: ", sensorType));
    }

    private void UpdateMaxValues(float[] values){
        for (int i = 0; i < values.length; ++i){
            if (mMaximumValues.size() <= i) {
                mMaximumValues.add(values[i]);
            }else if (Math.abs(values[i]) > Math.abs(mMaximumValues.get(i))) {
                mMaximumValues.set(i, values[i]);
            }
        }
    }

    protected float[] ProcessData(float[] values){
        return values;
    }

    public void HandleOutput(float[] v){
        HandleOutput(v, Integer.MAX_VALUE);
    }
    public void HandleOutput(float[] v, int maxLen){
        float[] values = ProcessData(v);

        UpdateMaxValues(values);

        String outputValueString = "(";
        for (int i = 0; i < Math.min(values.length, maxLen); ++i){
            if (i != 0){
                outputValueString += ",";
            }
            outputValueString += String.format(Locale.CANADA, "%.2f", values[i]);
        }
        outputValueString += ")";
        mOutputValue.setText(outputValueString);

        String outputMaxValueString = "(";
        for (int i = 0; i < Math.min(mMaximumValues.size(), maxLen); ++i){
            if (i != 0){
                outputMaxValueString += ",";
            }
            outputMaxValueString += String.format(Locale.CANADA, "%.2f", mMaximumValues.get(i));
        }
        outputMaxValueString += ")";
        mMaxOutputValue.setText(outputMaxValueString);
    }

    public void Reset(){
        for (int i = 0; i < mMaximumValues.size(); ++i){
            mMaximumValues.set(i, (float) 0);
        }
    }
}
