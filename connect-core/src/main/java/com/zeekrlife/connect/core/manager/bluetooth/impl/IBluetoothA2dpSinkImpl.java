package com.zeekrlife.connect.core.manager.bluetooth.impl;

import android.bluetooth.IBluetoothA2dpSink;
import android.content.ComponentName;
import android.content.Context;
import android.os.IBinder;

/**
 * @author qiangwang
 * @e-mail qiangwang@ecarx.com.cn
 * @time 2020/09/07
 * @description A2dpSink 实现
 */
public class IBluetoothA2dpSinkImpl extends BaseImpl {
    private IBluetoothA2dpSink mService;

    public IBluetoothA2dpSinkImpl(Context ctx, IBluetoothManagerImpl bluetoothManagerImpl, CallBack callBack) {
        super(ctx, IBluetoothA2dpSink.class.getName(), bluetoothManagerImpl, callBack);
    }

    @Override
    public IBluetoothA2dpSink getService() {
        return mService;
    }

    @Override
    protected void onServiceConnected(ComponentName name, IBinder service) {
        mService = IBluetoothA2dpSink.Stub.asInterface(service);
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
