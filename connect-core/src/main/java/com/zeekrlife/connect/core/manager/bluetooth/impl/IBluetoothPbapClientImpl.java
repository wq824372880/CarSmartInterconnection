package com.zeekrlife.connect.core.manager.bluetooth.impl;

import android.bluetooth.IBluetoothPbapClient;
import android.content.ComponentName;
import android.content.Context;
import android.os.IBinder;

public class IBluetoothPbapClientImpl extends BaseImpl {
    private IBluetoothPbapClient mService;

    public IBluetoothPbapClientImpl(Context ctx, IBluetoothManagerImpl bluetoothManagerImp, CallBack callBack) {
        super(ctx, IBluetoothPbapClient.class.getName(), bluetoothManagerImp, callBack);
    }

    @Override
    public IBluetoothPbapClient getService() {
        return mService;
    }

    @Override
    protected void onServiceConnected(ComponentName name, IBinder service) {
        mService = IBluetoothPbapClient.Stub.asInterface(service);
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
