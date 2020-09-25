package com.marveldex.seat31.Model;

import android.bluetooth.BluetoothGatt;

public class NoogiBluetoothGatt {

    BluetoothGatt bluetoothGatt;
    String bluetoothDeviceAddress;

    boolean servicesConfirm = false;


    public boolean isServicesConfirm() {
        return servicesConfirm;
    }

    public void setServicesConfirm(boolean servicesConfirm) {
        this.servicesConfirm = servicesConfirm;
    }

    public NoogiBluetoothGatt(BluetoothGatt bluetoothGatt, String bluetoothDeviceAddress) {
        this.bluetoothGatt = bluetoothGatt;
        this.bluetoothDeviceAddress = bluetoothDeviceAddress;

    }

    public BluetoothGatt getBluetoothGatt() {
        return bluetoothGatt;
    }

    public void setBluetoothGatt(BluetoothGatt bluetoothGatt) {
        this.bluetoothGatt = bluetoothGatt;
    }

    public String getBluetoothDeviceAddress() {
        return bluetoothDeviceAddress;
    }

    public void setBluetoothDeviceAddress(String bluetoothDeviceAddress) {
        this.bluetoothDeviceAddress = bluetoothDeviceAddress;
    }


}
