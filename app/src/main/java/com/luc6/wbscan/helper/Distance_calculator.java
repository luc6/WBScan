package com.luc6.wbscan.helper;

public class Distance_calculator {

    public static double calculateDistance(double levelInDb, double freqInMHz)    {

        long result = -1;

        if (freqInMHz == 0) {
            return -1;
        } else {
            double exp = (27.55 - (20 * Math.log10(freqInMHz)) + Math.abs(levelInDb)) / 20.0;
            // "*0.75", a compensation for signal loss due to obstacles and noise
            result = (Math.round(Math.pow(10.0, exp) * 0.75));
            //return (Math.pow(10.0, exp)); //this was original return statement
        }
        return result;
    }
}
