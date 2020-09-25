package com.marveldex.seat31.Model;

import android.bluetooth.BluetoothGatt;

import java.util.List;

public class NoogiBluetoothGattList {
    List<NoogiBluetoothGatt> noogiBluetoothGattList;

    public NoogiBluetoothGatt getNoogiBluetoothGattFromIndex(int index){
        for (NoogiBluetoothGatt gatt: noogiBluetoothGattList) {
            if(gatt == noogiBluetoothGattList.get(index)){
                return gatt;
            }
        }
        return null;
    }

    private NoogiBluetoothGatt getNoogiBluetoothgattFromGatt(BluetoothGatt gatt){
        NoogiBluetoothGatt currentNoogiBluetoothGatt= null;
        for (NoogiBluetoothGatt bluetoothGatt: noogiBluetoothGattList) {
            if(gatt == bluetoothGatt.getBluetoothGatt()){
                currentNoogiBluetoothGatt = bluetoothGatt;
            }
        }
        return currentNoogiBluetoothGatt;
    }

    private NoogiBluetoothGatt getNoogiBluetoothGattFromAddress(String address){
        if(noogiBluetoothGattList.size() == 0){
            return null;
        }
        for (NoogiBluetoothGatt gatt: noogiBluetoothGattList) {
            if(gatt.getBluetoothDeviceAddress().equals(address)){
                return gatt;
            }
        }
        return null;
    }
}
