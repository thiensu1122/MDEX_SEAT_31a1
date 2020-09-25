package com.marveldex.seat31.Model;

import android.bluetooth.BluetoothGatt;

import java.util.ArrayList;
import java.util.List;

public class NoogiBluetoothGattList {
    List<NoogiBluetoothGatt> noogiBluetoothGattList = new ArrayList<NoogiBluetoothGatt>();

    public int size(){
        return noogiBluetoothGattList.size();
    }

    public void add(NoogiBluetoothGatt gatt){
        noogiBluetoothGattList.add(gatt);
    }

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


    public List<NoogiBluetoothGatt> getNoogiBluetoothGattList() {
        return noogiBluetoothGattList;
    }

    public void setNoogiBluetoothGattList(List<NoogiBluetoothGatt> noogiBluetoothGattList) {
        this.noogiBluetoothGattList = noogiBluetoothGattList;
    }

    public void clear() {
        noogiBluetoothGattList.clear();
    }

    public NoogiBluetoothGatt get(int index) {
        return noogiBluetoothGattList.get(index);
    }

    public void remove(NoogiBluetoothGatt currentNoogiGatt) {
        noogiBluetoothGattList.remove(currentNoogiGatt);
    }
}
