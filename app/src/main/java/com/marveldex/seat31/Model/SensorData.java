package com.marveldex.seat31.Model;

import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SensorData {
    private static final String TAG = "SensorData";
    //40Bytes
    byte[] rawDataSensor1 = new byte[40];
    //40Bytes
    byte[] rawDataSensor2 = new byte[40];
    // 20 bytes
    byte[] backLeft = new byte[20];
    // 20 bytes
    byte[] backRight = new byte[20];
    // 10 bytes
    byte[] hipLeft = new byte[10];
    // 10 bytes
    byte[] hipRight = new byte[10];
    // 10 bytes
    byte[] thighLeft = new byte[10];
    // 10 bytes
    byte[] thighRight = new byte[10];

    boolean isPackage1Complete = false;
    boolean isPackage2Complete = false;
    byte[] firstIndicator = new byte[3];
    byte[] secondIndicator = new byte[3];
    public SensorData(){
        clearIndicator(1);
        clearIndicator(2);
    }

    public void onReceiveRawBytes(byte[] rawInput){
        if(rawInput.length < 24){
            return;
        }
        ///// if fist indicator is empty then save value to fist indicator.
        if(isIndicatorEmpty(1) || isFistSensor(rawInput)){
            if(rawInput[0] == 'M') {
                System.arraycopy(rawInput, 1, rawDataSensor1, 0, 20); // offset source 1 for indicator 'M', offset destination 0, number of bytes 20 for 'M';
                System.arraycopy(rawInput, 1, backLeft, 0, 20); // offset source 1 for indicator 'M', offset destination 0, number of bytes 20 for 'M'
                System.arraycopy(rawInput, 21, firstIndicator, 0, 3);
                isPackage1Complete = false;
            }
            if(rawInput[0] == 'S'){
                System.arraycopy(rawInput, 1, rawDataSensor1, 20, 20); // offset source 1 for indicator 'S', offset destination 20 since M took 20 bytes, number of bytes 20 for 'S';
                System.arraycopy(rawInput, 1, backRight, 0, 20); // offset source 1 for indicator 'S', offset destination 20 since S took 20 bytes, number of bytes 20 for 'S'
                System.arraycopy(rawInput, 21, firstIndicator, 0, 3);
                isPackage1Complete = true;
   
            }
        } else {
            ///// if fist indicator is not empty then save value to second indicator.
            if(rawInput[0] == 'M') {
                System.arraycopy(rawInput, 1, rawDataSensor2, 0, 20);   // offset source 1 for indicator 'M', offset destination 0, number of bytes 20 for "M";
                System.arraycopy(rawInput, 1, hipLeft, 0, 10);          // o0ffset source 1 for indicator 'M', offset destination 0 since hipleft is first 10 bytes, number of bytes 10
                System.arraycopy(rawInput, 10, hipRight, 0, 10);        // offset source 1 for indicator 'M', offset destination 10 since hipleft is first 10 bytes, number of bytes 10
                System.arraycopy(rawInput, 21, secondIndicator, 0, 3);
                isPackage2Complete = false;
            }
            if(rawInput[0] == 'S'){
                System.arraycopy(rawInput, 1, rawDataSensor2, 20, 20); // offset source 1 for indicator 'S', offset destination 20 since M took 20 bytes, number of bytes 20 for "S";
                System.arraycopy(rawInput, 1, thighLeft, 0, 10);        // offset source 1 for indicator 'S', offset destination 0 since thighleft is first 10 bytes, number of bytes 10
                System.arraycopy(rawInput, 10, thighRight, 0, 10);      // offset source 1 for indicator 'S', offset destination 10 since thighleft is first 10 bytes, number of bytes 10
                System.arraycopy(rawInput, 21, secondIndicator, 0, 3);

                isPackage2Complete = true;

            }
        }
    }

    private boolean isFistSensor(byte[] rawInput) {
        byte[] tempIndicator = new byte[3];
        System.arraycopy(rawInput, 21, tempIndicator, 0, 3);
        return (Arrays.equals(firstIndicator, tempIndicator));
    }

    //// when disconnect device we need to clear indicator.
    public void clearIndicator(int index){
        if(index ==1){
            Arrays.fill( firstIndicator, (byte) 0 );
        } else if (index ==2 ){
            Arrays.fill( secondIndicator, (byte) 0 );
        }
    }

    private boolean isIndicatorEmpty(int index){
        if(index == 1){
            return (firstIndicator[0] != 'G');
        } else if(index == 2){
            return (secondIndicator[0] != 'G');
        } else {
            return false;
        }

    }

    public byte[] getRawDataSensor1() {
        return rawDataSensor1;
    }

    public byte[] getRawDataSensor2() {
        return rawDataSensor2;
    }

    public byte[] getBackLeft() {
        return backLeft;
    }

    public byte[] getBackRight() {
        return backRight;
    }

    public byte[] getHipLeft() {
        return hipLeft;
    }

    public byte[] getHipRight() {
        return hipRight;
    }

    public byte[] getThighLeft() {
        return thighLeft;
    }

    public byte[] getThighRight() {
        return thighRight;
    }

    public boolean isPackage1Complete() {
        return isPackage1Complete;
    }

    public boolean isPackage2Complete() {
        return isPackage2Complete;
    }

    public void printData(){
//        Log.d(TAG, "Raw data 1 : " + Arrays.toString(rawDataSensor1));
//        Log.d(TAG, "Raw data 2 : " + Arrays.toString(rawDataSensor2));
//        Log.d(TAG, "back left : " + Arrays.toString(backLeft));
//        Log.d(TAG, "back right : " +Arrays.toString(backRight));
//        Log.d(TAG, "thigh left : " + Arrays.toString(thighLeft));
//        Log.d(TAG, "thigh right : " + Arrays.toString(thighRight));
        Log.d(TAG, "is Data 1 complete : " + isPackage1Complete);
        Log.d(TAG, "is Data 2 complete : " + isPackage2Complete);
    }
}
