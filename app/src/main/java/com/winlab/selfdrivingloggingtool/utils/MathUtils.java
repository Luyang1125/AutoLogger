package com.winlab.selfdrivingloggingtool.utils;

/**
 * Created by Luyang on 9/5/2016.
 */
public class MathUtils {

    public static float[][] multiplyByMatrix(float[][] m1, float[][] m2) {
        int m1ColLength = m1[0].length; // m1 columns length
        int m2RowLength = m2.length;    // m2 rows length
        if(m1ColLength != m2RowLength) return null; // matrix multiplication is not possible
        int mRRowLength = m1.length;    // m result rows length
        int mRColLength = m2[0].length; // m result columns length
        float[][] mResult = new float[mRRowLength][mRColLength];
        for(int i = 0; i < mRRowLength; i++) {         // rows from m1
            for(int j = 0; j < mRColLength; j++) {     // columns from m2
                for(int k = 0; k < m1ColLength; k++) { // columns from m1
                    mResult[i][j] += m1[i][k] * m2[k][j];
                }
            }
        }
        return mResult;
    }

    public static double angleBetweenVector(float[] v1, float[] v2, double lastAngle) {
        double y = v1[0]*v2[1]-v2[0]*v1[1];
        double x = v1[0]*v2[0]-v2[1]*v1[1];
        double angle = Math.atan2(y,x);
        angle = 180-(180/Math.PI * angle % 360);
        double[] possibleAngles = {angle,angle+360,angle-360,angle+720,angle-720};
        int ind = 0;
        double minDif = Math.abs(possibleAngles[ind]-lastAngle);
        for (int i = 1; i < possibleAngles.length; i++){
            if(Math.abs(possibleAngles[i]-lastAngle)<minDif){
                minDif = Math.abs(possibleAngles[i]-lastAngle);
                ind = i;
            }
        }
        return possibleAngles[ind];
    }

    public static double complementryFilter(double angle, float gyro_y, double accelAngle, double samplePeriod, double coeff, double angleError){
        angle = coeff*(angle+gyro_y*samplePeriod) + (1-coeff)*(accelAngle+angleError);
        return angle;
    }

}
