package lab0_202_24.uwaterloo.ca.lab1_202_24;

import android.content.Context;
import android.widget.LinearLayout;

/**
 * Created by patri on 2017-01-18.
 */

public class GeneralSensorHandler extends SensorHandler {
    GeneralSensorHandler(Context applicationContext, LinearLayout layout, String sensorType){
        super(applicationContext, layout, sensorType);
    }
}
