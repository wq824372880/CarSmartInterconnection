package com.zeekrlife.connect.core.manager.bluetooth.impl;

import android.bluetooth.IBluetoothHeadsetClient;
import android.content.ComponentName;
import android.content.Context;
import android.os.IBinder;

/**
 * @author qiangwang
 * @e-mail qiangwang@ecarx.com.cn
 * @time 2020/09/07
 * @description HeadsetClient 实现
 */
public class IBluetoothHeadsetClientImpl extends BaseImpl {
    private IBluetoothHeadsetClient mService;

    public IBluetoothHeadsetClientImpl(Context ctx, IBluetoothManagerImpl bluetoothManagerImpl, CallBack callBack) {
        super(ctx, IBluetoothHeadsetClient.class.getName(), bluetoothManagerImpl, callBack);
    }

    @Override
    public IBluetoothHeadsetClient getService() {
        return mService;
    }

    @Override
    protected void onServiceConnected(ComponentName name, IBinder service) {
        mService = IBluetoothHeadsetClient.Stub.asInterface(service);
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
