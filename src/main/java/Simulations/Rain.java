package Simulations;

import java.util.*;

/**
 * Created by jklei on 6/11/2017.
 */
public class Rain {
    Map<Date, Double> simVals;


    Rain(String code) {
        simVals = new HashMap<Date, Double>();
    }

    Rain(int total, int time, char peak) {
        simVals = new HashMap<Date, Double>();
    }

    private void setSimVals() {

    }

    //01    10.50   75  v
    //02    10.50   75  a
    //03    14.40   75  v
    //04    14.40   75  a
    //05    16.80   75  v
    //06    16.80   75  a
    //07    19.80   60  v
    //08    19.80   60  a
    //09    29.40   60  v
    //10    35.70   45  v

    private void getSimulation(int code) {
        Map<Date, Double> simulationVals = new TreeMap<Date, Double>();
        double[] precipitationValues = new double[24];
        Calendar now = Calendar.getInstance();
        switch(code) {
            case 1:
                precipitationValues = new double[]{5.0, 15.0, 5.0, 10.0, 10.0, 5.0, 15.0, 10.0, 10.0, 5.0, 10.0, 0.0, 0.0, 0.0, 5.0, 5.0, 10.0, 5.0, 15.0, 10.0, 10.0, 5.0, 5.0, 0.0};
                for(int i = 0; i < 24; i++) {
                    now.add(Calendar.MINUTE, 5);
                    simulationVals.put(now.getTime(), precipitationValues[i]);
                }

                break;
            case 2:

                break;

            default:

                break;
        }
    }

    private void calcSimVals(int total, int time, char peak) {
        int[] times;
        if(peak=='v') {
            times = new int[]{0, (1/6)*time, (2/6)*time, time};
        } else if(peak=='a') {
            times = new int[]{0, (4/6)*time, (5/6)*time, time};
        } else {
            times = new int[]{0, (1/3)*time, (2/3)*time, time};
        }

        int[] fallenWater = {0, (1/4)*total, (3/4)*total, total};
    }

}
