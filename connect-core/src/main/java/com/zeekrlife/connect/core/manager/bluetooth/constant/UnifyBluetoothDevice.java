package com.zeekrlife.connect.core.manager.bluetooth.constant;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.ext.SubBluetoothDevice;

public class UnifyBluetoothDevice {
    private int currentType;
    private BluetoothDevice mainDevice;
    private SubBluetoothDevice subDevice;

    public UnifyBluetoothDevice(int type, BluetoothDevice mainDevice,
                                SubBluetoothDevice subBluetoothDevice) {
        currentType = type;
        this.mainDevice = mainDevice;
        this.subDevice = subBluetoothDevice;
    }

    public int getType() {
        return currentType;
    }

    public BluetoothDevice getMainDevice() {
        return mainDevice;
    }

    public SubBluetoothDevice getSubDevice() {
        return subDevice;
    }

    public String getName() {
        if (currentType == BluetoothConst.mainDeviceType) {
            return mainDevice.getName();
        } else {
            return subDevice.getName();
        }
    }

    public String getAddress() {
        if (currentType == BluetoothConst.mainDeviceType) {
            return mainDevice.getAddress();
        } else {
            return subDevice.getAddress();
        }
    }
}
