package com.marveldex.seat31.Model;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.util.Log;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.marveldex.seat31.UartService.RX_SERVICE_UUID;
import static com.marveldex.seat31.UartService.TX_CHAR_UUID;

public class NoogiBluetoothGattList {
    private static final String TAG = "NoogiBluetoothGattList";
    List<NoogiBluetoothGatt> noogiBluetoothGattList = new ArrayList<NoogiBluetoothGatt>();

    public int confirmedDeviceServiceCount(){
        int count = 0;
        for (NoogiBluetoothGatt gatt: noogiBluetoothGattList) {
            if(gatt.isServicesConfirm()){
                count++;
            }
        }
        return count;
    }

    public void add(NoogiBluetoothGatt gatt){
        noogiBluetoothGattList.add(gatt);
    }

    public void checkDeviceServices(NoogiBluetoothGatt currentNoogiGatt) {
        BluetoothGattService RxService = currentNoogiGatt.getBluetoothGatt().getService(RX_SERVICE_UUID);
        if(RxService != null){
            BluetoothGattCharacteristic TxChar = RxService.getCharacteristic(TX_CHAR_UUID);
            if (TxChar != null) {
                currentNoogiGatt.setServicesConfirm(true);
            }
        }
    }

    public NoogiBluetoothGatt getNoogiBluetoothgattFromGatt(BluetoothGatt gatt){
        NoogiBluetoothGatt currentNoogiBluetoothGatt= null;
        for (NoogiBluetoothGatt bluetoothGatt: noogiBluetoothGattList) {
            if(gatt == bluetoothGatt.getBluetoothGatt()){
                currentNoogiBluetoothGatt = bluetoothGatt;
            }
        }
        return currentNoogiBluetoothGatt;
    }

    public NoogiBluetoothGatt getNoogiBluetoothGattFromAddress(String address){
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
