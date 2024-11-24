package com.zeekrlife.connect.core.manager.bluetooth.impl;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.IBluetoothAvrcpController;
import android.content.ComponentName;
import android.content.Context;
import android.os.IBinder;

import com.zeekrlife.connect.core.manager.bluetooth.utils.xLog;

/**
 * @author qiangwang
 * @e-mail qiangwang@ecarx.com.cn
 * @time 2020/09/07
 * @description Avrcp 实现
 */
public class IBluetoothAvrcpControlImpl extends BaseImpl {
    private IBluetoothAvrcpController mService;

    public IBluetoothAvrcpControlImpl(Context ctx, IBluetoothManagerImpl bluetoothManagerImpl, CallBack callBack) {
        super(ctx, IBluetoothAvrcpController.class.getName(), bluetoothManagerImpl, callBack);
    }

    @Override
    public IBluetoothAvrcpController getService() {
        return mService;
    }

    @Override
    protected void onServiceConnected(ComponentName name, IBinder service) {
        mService = IBluetoothAvrcpController.Stub.asInterface(service);
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

    @Override
    public boolean connect(BluetoothDevice device) {
        if (mService == null) {
            xLog.d(TAG, "return !! (mService == null)");
            return false;
        }

        if (device == null) {
            xLog.d(TAG, "return !! (device == null)");
            return false;
        }
        return false;
    }

    @Override
    public boolean disconnect(BluetoothDevice device) {
        if (mService == null) {
            xLog.d(TAG, "return !! (mService == null)");
            return false;
        }

        if (device == null) {
            xLog.d(TAG, "return !! (device == null)");
            return false;
        }
        return false;
    }
}
