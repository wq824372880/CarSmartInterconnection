package com.zeekrlife.connect.core.manager.bluetooth.impl;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.IBluetooth;
import android.bluetooth.IBluetoothManagerCallback;
import android.content.ComponentName;
import android.content.Context;
import android.os.IBinder;
import android.os.RemoteException;


import com.huawei.managementsdk.common.LogUtils;
import com.zeekrlife.connect.core.manager.bluetooth.utils.InvokeUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @author qiangwang
 * @e-mail qiangwang@ecarx.com.cn
 * @time 2020/09/07
 * @description Bluetooth 实现
 */
public class IBluetoothImpl {
    public final static String TAG = "IBluetoothImpl";

    private IBluetooth mService;
    private IBluetoothManagerImpl mBluetoothManagerImpl;

    public IBluetoothImpl(Context ctx, IBluetoothManagerImpl bluetoothManagerImpl) {
        mBluetoothManagerImpl = bluetoothManagerImpl;
        checkBindService();
    }

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
            mService = mBluetoothManagerImpl.getIBluetooth(mManagerCallback);
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
        if (mService != null) {
            mBluetoothManagerImpl.releaseIBluetooth(mManagerCallback);
        }
    }

    public IBluetooth getService() {
        checkBindService();
        return mService;
    }

    protected void onServiceConnected(ComponentName name, IBinder service) {
        mService = IBluetooth.Stub.asInterface(service);
    }

    protected void onServiceDisconnected(ComponentName name) {
        mService = null;
    }

    protected void onBluetoothStateChange(boolean on) {
        if (!on) {
            mService = null;
        }
    }

    /**
     * 获取绑定的蓝牙设备列表
     *
     * @return 蓝牙设备列表
     */
    public List<BluetoothDevice> getBondedDevices() {
        List<BluetoothDevice> bondedList = new ArrayList<>();
        if (mService == null) {
            LogUtils.e(TAG, "getBondedDevices mService is null");
            return bondedList;
        }
        try {
            BluetoothDevice[] bondedArray = mService.getBondedDevices(InvokeUtil.getAttributionSource());
            if (bondedArray != null) {
                for (int index = 0; index < bondedArray.length; index++) {
                    if (bondedArray[index] != null) {
                        bondedList.add(bondedArray[index]);
                    }
                }
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return bondedList;
    }

    /**
     * 移除指定的绑定蓝牙设备
     *
     * @param device 蓝牙设备
     * @return true: 执行成功, false: 执行失败
     */
    public boolean removeBond(BluetoothDevice device) {
        if (device == null) {
            LogUtils.e(TAG, "removeBond device is null");
            return false;
        }
        if (mService == null) {
            LogUtils.e(TAG, "removeBond mService is null");
            return false;
        }
        boolean result = false;
        try {
            result = mService.removeBond(device, InvokeUtil.getAttributionSource());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 获取设备绑定状态
     *
     * @param device 蓝牙设备
     * @return 绑定状态
     */
    public int getBondState(BluetoothDevice device) {
        if (device == null) {
            LogUtils.e(TAG, "getBondState device is null");
            return BluetoothDevice.BOND_NONE;
        }
        if (mService == null) {
            LogUtils.e(TAG, "getBondState mService is null");
            return BluetoothDevice.BOND_NONE;
        }
        int result = BluetoothDevice.BOND_NONE;
        try {
            result = mService.getBondState(device, InvokeUtil.getAttributionSource());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 设置不自动连接
     *
     * @return true: 执行成功, false: 执行失败
     */
    public boolean enableNoAutoConnect() {
        if (mService == null) {
            LogUtils.e(TAG, "enableNoAutoConnect mService is null");
            return false;
        }
        boolean result = false;
        /* 注释于2022-12-15，方法丢失
            try {
            result = mService.enableNoAutoConnect();
        } catch (RemoteException e) {
            e.printStackTrace();
        }*/
        return result;
    }

    /**
     * 获取远程蓝牙设备名称
     *
     * @return 名称
     */
    public String getRemoteName(BluetoothDevice device) {
        if (mService == null) {
            LogUtils.e(TAG, "getRemoteName mService is null");
            return "";
        }
        if (device == null) {
            LogUtils.e(TAG, "getRemoteName device is null");
            return "";
        }
        String result = "";
        try {
            result = mService.getRemoteName(device, InvokeUtil.getAttributionSource());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        if (result == null) {
            result = "";
        }
        return result;
    }

    /**
     * 设置扫描模式
     *
     * @param on true: 可被扫描到, false: 不能被扫描到
     * @return true: 执行成功, false: 执行失败
     */
    private boolean setScanMode(boolean on) {
        if (mService == null) {
            LogUtils.e(TAG, "setScanMode mService is null");
            return false;
        }
        boolean result = false;
        try {
            if (on) {
                result = mService.setScanMode(BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE, 120, InvokeUtil.getAttributionSource());
            } else {
                result = mService.setScanMode(BluetoothAdapter.SCAN_MODE_CONNECTABLE, 120, InvokeUtil.getAttributionSource());
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 是否在可被扫描到中
     *
     * @return true: 可被扫描到, false: 不可被扫描到
     */
    private boolean isScanModeConnectableDiscoverable() {
        if (mService == null) {
            LogUtils.e(TAG, " isScanModeConnectableDiscoverable mService is null");
            return false;
        }
        int scanMode = 0;
        try {
            scanMode = mService.getScanMode(InvokeUtil.getAttributionSource());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        LogUtils.w(TAG, "isScanModeConnectableDiscoverable scanMode=" + scanMode);
        if (BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE == scanMode) {
            return true;
        }
        return false;
    }

    /**
     * 设置扫描模式
     *
     * @param on true: 可被扫描到, false: 不能被扫描到
     * @return true: 执行成功, false: 执行失败
     */
    public boolean setDiscoveryByRemote(boolean on) {
        if (mService == null) {
            LogUtils.e(TAG, "setDiscoveryByRemote mService is null");
            return false;
        }
        return setScanMode(on);
    }

    /**
     * 是否在可被扫描到中
     *
     * @return true: 可被扫描到, false: 不可被扫描到
     */
    public boolean isDiscoveryByRemoteOn() {
        if (mService == null) {
            LogUtils.e(TAG, "isDiscoveryByRemoteOn mService is null");
            return false;
        }
        return isScanModeConnectableDiscoverable();
    }

    private IBluetoothManagerCallback mManagerCallback = new IBluetoothManagerCallback.Stub() {
        @Override
        public void onBluetoothServiceUp(IBluetooth bluetoothService) {
            mService = bluetoothService;
        }

        @Override
        public void onBluetoothServiceDown() {
            mService = null;
        }

        @Override
        public void onBrEdrDown() {
        }
    };
}
