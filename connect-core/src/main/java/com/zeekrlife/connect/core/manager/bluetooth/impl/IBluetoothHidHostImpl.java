package com.zeekrlife.connect.core.manager.bluetooth.impl;

import android.bluetooth.IBluetoothHidHost;
import android.content.ComponentName;
import android.content.Context;
import android.os.IBinder;

/**
 * 蓝牙hid实现
 */
public class IBluetoothHidHostImpl extends BaseImpl {
    private IBluetoothHidHost mService;

    public IBluetoothHidHostImpl(Context ctx, IBluetoothManagerImpl bluetoothManagerImpl, CallBack callBack) {
        super(ctx, IBluetoothHidHost.class.getName(), bluetoothManagerImpl, callBack);
    }

    @Override
    public IBluetoothHidHost getService() {
        return mService;
    }

    @Override
    protected void onServiceConnected(ComponentName name, IBinder service) {
        mService = IBluetoothHidHost.Stub.asInterface(service);
    }

    @Override
    protected void onServiceDisconnected(ComponentName name) {
        mService = null;
    }

    @Override
    protected void onBluetoothStateChange(boolean on) {
        if (!on) {
            mService = null;
        }
    }
}
