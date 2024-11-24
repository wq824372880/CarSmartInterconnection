package com.zeekrlife.connect.core.manager.bluetooth.impl;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.IBluetoothGatt;
import android.content.Context;
import android.os.RemoteException;

import com.huawei.managementsdk.common.LogUtils;
import com.zeekrlife.connect.core.manager.bluetooth.utils.InvokeUtil;


/**
 * @author qiangwang
 * @e-mail qiangwang@ecarx.com.cn
 * @time 2020/09/07
 * @description BluetoothGatt 实现
 */
public class IBluetoothGattImpl {
    public final static String TAG = "IBluetoothGattImpl";

    private IBluetoothGatt mService;
    private IBluetoothManagerImpl mBluetoothManagerImpl;
    private BluetoothManager mBluetoothManager;

    public IBluetoothGattImpl(Context ctx, IBluetoothManagerImpl bluetoothManagerImpl) {
        mBluetoothManagerImpl = bluetoothManagerImpl;
        mBluetoothManager = (BluetoothManager) ctx.getSystemService(Context.BLUETOOTH_SERVICE);
        checkBindService();
    }

    /**
     * 退出
     */
    public void onExit() {
        stopBindService();
    }

    /**
     * 检查服务
     *
     * @return true: 执行成功, false: 执行失败
     */
    public boolean checkBindService() {
        if (mService == null) {
            mService = mBluetoothManagerImpl.getIBluetoothGatt();
            if (mService != null) {
                return true;
            }
        }
        return false;
    }

    /**
     * 停止服务
     */
    private void stopBindService() {
    }

    public IBluetoothGatt getService() {
        checkBindService();
        return mService;
    }

    /**
     * 指定设备是否连接
     *
     * @param device 蓝牙设备
     * @return true: 连接, false: 没有连接
     */
    public boolean isConnected(BluetoothDevice device) {
        checkBindService();
        if (mService == null) {
            LogUtils.e(TAG, "isConnected mService is null");
            return false;
        }
        if (device == null) {
            LogUtils.e(TAG, "isConnected device is null");
            return false;
        }
        int state = mBluetoothManager.getConnectionState(device, BluetoothProfile.GATT);
        return state == BluetoothProfile.STATE_CONNECTED;
    }

    public boolean disconnect(BluetoothDevice device) {
        checkBindService();
        if (mService == null) {
            LogUtils.e(TAG, "disconnect mService is null");
            return false;
        }
        if (device == null) {
            LogUtils.e(TAG, "disconnect device is null");
            return false;
        }
        boolean isConnected = isConnected(device);
        if (!isConnected) {
            return false;
        }
        try {
            mService.disconnectAll(InvokeUtil.getAttributionSource());
            return true;
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }
}
