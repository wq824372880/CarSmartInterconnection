package com.zeekrlife.connect.core.manager.bluetooth.impl;

import android.bluetooth.IBluetoothA2dp;
import android.content.ComponentName;
import android.content.Context;
import android.os.IBinder;

import com.huawei.authagent.service.utils.LogUtils;

/**
 * 蓝牙耳机实现
 */
public class IBluetoothA2dpImpl extends BaseImpl {
    private IBluetoothA2dp mService;

    public IBluetoothA2dpImpl(Context ctx, IBluetoothManagerImpl bluetoothManagerImpl, CallBack callBack) {
        super(ctx, IBluetoothA2dp.class.getName(), bluetoothManagerImpl, callBack);
    }

    @Override
    public IBluetoothA2dp getService() {
        return mService;
    }

    @Override
    protected void onServiceConnected(ComponentName name, IBinder service) {
        mService = IBluetoothA2dp.Stub.asInterface(service);
        LogUtils.w(TAG, "onServiceConnected mService = " + mService);
    }

    @Override
    protected void onServiceDisconnected(ComponentName name) {
        mService = null;
        LogUtils.w(TAG, "onServiceDisconnected mService = null");
    }

    @Override
    protected void onBluetoothStateChange(boolean on) {
        if (!on) {
            mService = null;
        }
    }
}
