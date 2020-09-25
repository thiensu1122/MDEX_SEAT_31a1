package com.marveldex.seat31.Model;

import android.bluetooth.BluetoothGatt;

public class NoogiBluetoothGatt {

    BluetoothGatt bluetoothGatt;
    String BluetoothDeviceAddress;
    int index;

    public NoogiBluetoothGatt(BluetoothGatt bluetoothGatt, String bluetoothDeviceAddress, int index) {
        this.bluetoothGatt = bluetoothGatt;
        BluetoothDeviceAddress = bluetoothDeviceAddress;
        this.index = index;
    }

    public BluetoothGatt getBluetoothGatt() {
        return bluetoothGatt;
    }

    public void setBluetoothGatt(BluetoothGatt bluetoothGatt) {
        this.bluetoothGatt = bluetoothGatt;
    }

    public String getBluetoothDeviceAddress() {
        return BluetoothDeviceAddress;
    }

    public void setBluetoothDeviceAddress(String bluetoothDeviceAddress) {
        BluetoothDeviceAddress = bluetoothDeviceAddress;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
