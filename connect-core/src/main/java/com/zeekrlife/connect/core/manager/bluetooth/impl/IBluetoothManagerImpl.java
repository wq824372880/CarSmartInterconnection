package com.zeekrlife.connect.core.manager.bluetooth.impl;

import android.bluetooth.IBluetooth;
import android.bluetooth.IBluetoothGatt;
import android.bluetooth.IBluetoothManager;
import android.bluetooth.IBluetoothManagerCallback;
import android.bluetooth.IBluetoothStateChangeCallback;
import android.content.Context;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteException;


import com.huawei.managementsdk.common.LogUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author qiangwang
 * @e-mail qiangwang@ecarx.com.cn
 * @time 2020/09/07
 * @description 蓝牙管理
 */
public class IBluetoothManagerImpl {
    public final static String TAG = "IBluetoothManagerImpl";
    private Context mContext;
    private IBluetoothManager mService;
    private final IBinder mToken;

    public IBluetoothManagerImpl(Context ctx) {
        mContext = ctx;
        mToken = new Binder();
        getService();
    }

    /**
     * 退出
     */
    public void onExit() {
    }

    /**
     * 检查服务
     *
     * @return true: 执行成功, false: 执行失败
     */
    public void checkBindService() {
        if (mService == null) {
            getService();
        }
    }

    /**
     * 停止服务
     */
    public void stopBindService() {
    }

    /**
     * 获取绑定的服务
     *
     * @return 绑定的服务
     */
    public IBluetoothManager getService() {
        try {
            ClassLoader classLoader = IBluetoothManagerImpl.class.getClassLoader();
            if (classLoader != null) {
                Class<?> clazz = classLoader.loadClass("android.os.ServiceManager");
                Method method = clazz.getDeclaredMethod("getService", String.class);
                IBinder ibinder = (IBinder) method.invoke(null, "bluetooth_manager");
                mService = IBluetoothManager.Stub.asInterface(ibinder);
            }
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return mService;
    }

    public IBluetooth getIBluetooth(IBluetoothManagerCallback callback) {
        checkBindService();
        if (mService == null) {
            LogUtils.e(TAG, "getIBluetooth mService is null");
            return null;
        }

        IBluetooth iBluetoothService = null;
        try {
            iBluetoothService = mService.registerAdapter(callback);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return iBluetoothService;
    }

    public boolean releaseIBluetooth(IBluetoothManagerCallback callback) {
        checkBindService();
        if (mService == null) {
            LogUtils.d(TAG, "releaseIBluetooth mService is null");
            return false;
        }

        try {
            mService.unregisterAdapter(callback);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return true;
    }


    public boolean registerStateChangeCallback(IBluetoothStateChangeCallback callback) {
        checkBindService();
        if (mService == null) {
            LogUtils.e(TAG, "registerStateChangeCallback mService is null");
            return false;
        }

        try {
            mService.registerStateChangeCallback(callback);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return true;
    }

    public boolean unregisterStateChangeCallback(IBluetoothStateChangeCallback callback) {
        checkBindService();
        if (mService == null) {
            LogUtils.e(TAG, "unregisterStateChangeCallback mService is null");
            return false;
        }

        try {
            mService.unregisterStateChangeCallback(callback);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return true;
    }

    public IBluetoothGatt getIBluetoothGatt() {
        checkBindService();
        if (mService == null) {
            LogUtils.e(TAG, "getIBluetoothGatt mService is null");
            return null;
        }

        IBluetoothGatt iBluetoothGatt = null;
        try {
            iBluetoothGatt = mService.getBluetoothGatt();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return iBluetoothGatt;
    }
}
