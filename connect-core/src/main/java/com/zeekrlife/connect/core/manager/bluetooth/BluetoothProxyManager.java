package com.zeekrlife.connect.core.manager.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.os.RemoteException;
import android.text.TextUtils;


import com.huawei.managementsdk.common.LogUtils;
import com.zeekrlife.connect.core.manager.bluetooth.constant.BluetoothConst;
import com.zeekrlife.connect.core.manager.bluetooth.impl.BaseImpl;
import com.zeekrlife.connect.core.manager.bluetooth.impl.IBluetoothA2dpImpl;
import com.zeekrlife.connect.core.manager.bluetooth.impl.IBluetoothA2dpSinkImpl;
import com.zeekrlife.connect.core.manager.bluetooth.impl.IBluetoothAvrcpControlImpl;
import com.zeekrlife.connect.core.manager.bluetooth.impl.IBluetoothGattImpl;
import com.zeekrlife.connect.core.manager.bluetooth.impl.IBluetoothHeadsetClientImpl;
import com.zeekrlife.connect.core.manager.bluetooth.impl.IBluetoothHidHostImpl;
import com.zeekrlife.connect.core.manager.bluetooth.impl.IBluetoothImpl;
import com.zeekrlife.connect.core.manager.bluetooth.impl.IBluetoothManagerImpl;
import com.zeekrlife.connect.core.manager.bluetooth.impl.IBluetoothPbapClientImpl;
import com.zeekrlife.connect.core.manager.bluetooth.listener.IBluetoothCallBack;
import com.zeekrlife.connect.core.manager.bluetooth.utils.InvokeUtil;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class BluetoothProxyManager {
    public final static String TAG = "BluetoothProxyManager_HiCar";
    private BluetoothAdapter mAdapter;
    private final IBluetoothManagerImpl mBluetoothManagerImpl;
    private final IBluetoothImpl mBluetoothImpl;
    private final IBluetoothGattImpl mBluetoothGattImpl;
    private final IBluetoothA2dpSinkImpl mBluetoothA2dpSinkImpl;
    private final IBluetoothAvrcpControlImpl mBluetoothAvrcpControlImpl;
    private final IBluetoothHeadsetClientImpl mBluetoothHeadsetClientImpl;
    private final IBluetoothPbapClientImpl mBluetoothPbapClientImpl;
    private final IBluetoothHidHostImpl mBluetoothHidImpl;
    private final IBluetoothA2dpImpl mBluetoothA2dpImpl;

    private final IBluetoothCallBack mBluetoothCallBackImpl;

    public BluetoothProxyManager(Context ctx, IBluetoothCallBack callBack) {
        mAdapter = Objects.requireNonNull(BluetoothAdapter.getDefaultAdapter());

        mBluetoothCallBackImpl = callBack;
        mBluetoothManagerImpl = new IBluetoothManagerImpl(ctx);
        mBluetoothImpl = new IBluetoothImpl(ctx, mBluetoothManagerImpl);
        mBluetoothGattImpl = new IBluetoothGattImpl(ctx, mBluetoothManagerImpl);
        mBluetoothA2dpSinkImpl = new IBluetoothA2dpSinkImpl(ctx, mBluetoothManagerImpl, mBaseCallBack);
        mBluetoothAvrcpControlImpl = new IBluetoothAvrcpControlImpl(ctx, mBluetoothManagerImpl, mBaseCallBack);
        mBluetoothHeadsetClientImpl = new IBluetoothHeadsetClientImpl(ctx, mBluetoothManagerImpl, mBaseCallBack);
        mBluetoothPbapClientImpl = new IBluetoothPbapClientImpl(ctx, mBluetoothManagerImpl, mBaseCallBack);
        mBluetoothHidImpl = new IBluetoothHidHostImpl(ctx, mBluetoothManagerImpl, mBaseCallBack);
        mBluetoothA2dpImpl = new IBluetoothA2dpImpl(ctx, mBluetoothManagerImpl, mBaseCallBack);
        onEnter();
    }

    public void onEnter() {
        mBluetoothA2dpSinkImpl.onEnter();
        mBluetoothAvrcpControlImpl.onEnter();
        mBluetoothHeadsetClientImpl.onEnter();
        mBluetoothPbapClientImpl.onEnter();
        mBluetoothHidImpl.onEnter();
        mBluetoothA2dpImpl.onEnter();
    }

    /**
     * 退出
     */
    public void onExit() {
        mBluetoothImpl.onExit();
        mBluetoothGattImpl.onExit();
        mBluetoothA2dpSinkImpl.onExit();
        mBluetoothAvrcpControlImpl.onExit();
        mBluetoothHeadsetClientImpl.onExit();
        mBluetoothPbapClientImpl.onExit();
        mBluetoothManagerImpl.onExit();
        mBluetoothHidImpl.onExit();
        mBluetoothA2dpImpl.onExit();
    }

    /**
     * 检查服务
     */
    public void checkService() {
        mBluetoothManagerImpl.checkBindService();
        mBluetoothImpl.checkBindService();
        mBluetoothGattImpl.checkBindService();
        mBluetoothA2dpSinkImpl.checkBindService();
        mBluetoothAvrcpControlImpl.checkBindService();
        mBluetoothHeadsetClientImpl.checkBindService();
        mBluetoothPbapClientImpl.checkBindService();
        mBluetoothHidImpl.checkBindService();
        mBluetoothA2dpImpl.checkBindService();
    }

    /**
     * 判断蓝牙是否存在
     *
     * @return true: 存在，false: 不存在
     */
    public boolean isBtExist() {
        if (mAdapter == null) {
            LogUtils.e(TAG, "Bluetooth not exist !!");
        }
        return (mAdapter != null);
    }

    /**
     * 设置蓝牙名称
     *
     * @param name 名称
     * @return true: 执行成功, false: 执行失败
     */
    public boolean setBtName(String name) {
        if (!isBtExist()) {
            return false;
        }

        if (TextUtils.isEmpty(name)) {
            return false;
        }

        if (TextUtils.isEmpty(name.trim())) {
            return false;
        }
        LogUtils.w(TAG, "setName " + name.trim());
        return mAdapter.setName(name.trim());
    }

    /**
     * 获取蓝牙名称
     *
     * @return 蓝牙名称
     */
    public String getBtName() {
        if (!isBtExist()) {
            return "";
        }

        String btName = mAdapter.getName();
        LogUtils.w(TAG, "getBtName " + btName);
        if (TextUtils.isEmpty(btName)) {
            return BluetoothConst.DEF_BT_NAME;
        }
        return btName;
    }

    public String getBtAddress() {
        if (!isBtExist()) {
            return "";
        }

        String btAddress = mAdapter.getAddress();
        LogUtils.w(TAG, "getBtAddress " + btAddress);
        if (TextUtils.isEmpty(btAddress)) {
            return "";
        }
        return btAddress;
    }


    public boolean isBtOpened() {
        if (!isBtExist()) {
            return false;
        }
        return mAdapter.isEnabled();
    }

    /**
     * 打开/关闭蓝牙
     *
     * @param open true: 打开, false: 关闭
     * @return true: 执行成功, false: 执行失败
     */
    public boolean setBtOpen(boolean open) {
        if (!isBtExist()) {
            return false;
        }
        LogUtils.w(TAG, "setBtOpen =>" + open);
        if (open) {
            return mAdapter.enable();
        } else {
            return mAdapter.disable();
        }
    }


    /**
     * 设置扫描模式
     *
     * @param on true: 可被扫描到, false: 不能被扫描到
     * @return true: 执行成功, false: 执行失败
     */
    public boolean setDiscoveryByRemote(boolean on) {
        return mBluetoothImpl.setDiscoveryByRemote(on);
    }

    /**
     * 获取绑定的蓝牙设备列表
     *
     * @return 蓝牙设备列表
     */
    public List<BluetoothDevice> getBondedDevices() {
        return mBluetoothImpl.getBondedDevices();
    }

    /**
     * 移除指定的绑定蓝牙设备
     *
     * @param device 蓝牙设备
     * @return true: 执行成功, false: 执行失败
     */
    public boolean removeBond(BluetoothDevice device) {
        if (device == null) {
            return false;
        }

        LogUtils.w(TAG, "removeBond device.name=" + device.getName() + ", device.address=" + device.getAddress());
        return mBluetoothImpl.removeBond(device);
    }

    public int getDeviceBondState(BluetoothDevice device) {
        int state = BluetoothDevice.BOND_NONE;
        if (device == null) {
            return state;
        }
        state = mBluetoothImpl.getBondState(device);
        return state;
    }

    /**
     * 指定的蓝牙设备是否已经绑定
     *
     * @param device 蓝牙设备
     * @return true: 已经绑定, false: 没有绑定
     */
    public boolean isDeviceBonded(BluetoothDevice device) {
        if (device == null) {
            return false;
        }

        int state = mBluetoothImpl.getBondState(device);
        return (state == BluetoothDevice.BOND_BONDED);
    }

    /**
     * 指定的蓝牙设备是否已经绑定
     *
     * @param device 蓝牙设备
     * @return true: 已经绑定, false: 没有绑定
     */
    public boolean isDeviceBonding(BluetoothDevice device) {
        if (device == null) {
            return false;
        }

        int state = mBluetoothImpl.getBondState(device);
        return (state == BluetoothDevice.BOND_BONDING);
    }

    public boolean isConnectingHFP(BluetoothDevice device) {
        if (device == null) {
            return false;
        }

        int state = mBluetoothHeadsetClientImpl.getConnectionState(device);
        return state == BluetoothProfile.STATE_CONNECTING;
    }

    public boolean isConnectingA2dp(BluetoothDevice device) {
        if (device == null) {
            return false;
        }

        int state = mBluetoothA2dpSinkImpl.getConnectionState(device);
        return state == BluetoothProfile.STATE_CONNECTING;
    }

    /**
     * 是否连接HFP
     *
     * @param device 蓝牙设备
     * @return true: 已经连接, false: 没有连接
     */
    public boolean isConnectedHFP(BluetoothDevice device) {
        if (device == null) {
            return false;
        }

        boolean hfpResult = mBluetoothHeadsetClientImpl.isConnected(device);
        LogUtils.w(TAG, "hfpResult=="+hfpResult+" device="+device);
        return hfpResult;
    }

    /**
     * 是否连接A2dp
     *
     * @param device 蓝牙设备
     * @return true: 已经连接, false: 没有连接
     */
    public boolean isConnectedA2dp(BluetoothDevice device) {
        if (device == null) {
            return false;
        }

        boolean a2dpResult = mBluetoothA2dpSinkImpl.isConnected(device);
        LogUtils.w(TAG, "a2dpResult=="+a2dpResult+" device="+device);
        return a2dpResult;
    }

    /**
     * 连接HFP
     *
     * @param device 蓝牙设备
     * @return true: 执行成功, false: 执行失败
     */
    public boolean connectHFP(BluetoothDevice device) {
        LogUtils.w(TAG, "connectHFP  device.name=" + device.getName() + ", device.address=" + device.getAddress());

        if (!isDeviceBonded(device)) {
            boolean createBond = device.createBond();
            LogUtils.w(TAG, "return !! Not bonded yet !! createBond=" + createBond);
            return createBond;
        }
        boolean result = false;
        try {
            int hfpstate = mBluetoothHeadsetClientImpl.getService().getConnectionState(device, InvokeUtil.getAttributionSource());
            LogUtils.w(TAG, "HFP is Connecting: " + hfpstate);
            if (hfpstate != BluetoothProfile.STATE_CONNECTING) {
                result = mBluetoothHeadsetClientImpl.connect(device);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 连接A2DP
     *
     * @param device 蓝牙设备
     * @return true: 执行成功, false: 执行失败
     */
    public boolean connectA2dp(BluetoothDevice device) {
            LogUtils.w(TAG, "connectA2dp  device.name=" + device.getName() + ", device.address=" + device.getAddress());

        if (!isDeviceBonded(device)) {
            boolean createBond = device.createBond();
            LogUtils.w(TAG, "return !! Not bonded yet !! createBond=" + createBond);
            return createBond;
        }
        boolean result = false;
        try {
            int state = mBluetoothA2dpSinkImpl.getService().getConnectionState(device, InvokeUtil.getAttributionSource());
            LogUtils.w(TAG, "A2dp is Connecting: " + state);
            if (state != BluetoothProfile.STATE_CONNECTING) {
                result = mBluetoothA2dpSinkImpl.connect(device);
            }

        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 断开指定的蓝牙设备连接
     *
     * @param device 蓝牙设备
     * @return true: 执行成功, false: 执行失败
     */
    public boolean disconnect(BluetoothDevice device) {
        if (device == null) {
            return false;
        }

        LogUtils.w(TAG, "disconnect device.name=" + device.getName() + ", device.address=" + device.getAddress());

        boolean result = mBluetoothA2dpSinkImpl.disconnect(device);
        result |= mBluetoothAvrcpControlImpl.disconnect(device);
        result |= mBluetoothHeadsetClientImpl.disconnect(device);
        result |= mBluetoothPbapClientImpl.disconnect(device);
        result |= mBluetoothGattImpl.disconnect(device);
        return result;
    }

    /**
     * 断开HFP
     *
     * @param device 蓝牙设备
     * @return true: 执行成功, false: 执行失败
     */
    public boolean disconnectHFP(BluetoothDevice device) {
        if (device == null) {
            return false;
        }

        LogUtils.w(TAG, "disconnectHFP device.name=" + device.getName() + ", device.address=" + device.getAddress());

        return mBluetoothHeadsetClientImpl.disconnect(device);
    }

    /**
     * 断开A2DP
     *
     * @param device 蓝牙设备
     * @return true: 执行成功, false: 执行失败
     */
    public boolean disconnectA2dp(BluetoothDevice device) {
        if (device == null) {
            return false;
        }

        LogUtils.e(TAG, "disconnectA2dp device.name=" + device.getName() + ", device.address=" + device.getAddress());

        return mBluetoothA2dpSinkImpl.disconnect(device);
    }

    /**
     * 是否在扫描中
     *
     * @return true: 扫描中, false: 非扫描中
     */
    public boolean isDiscovering() {
        if (!isBtExist()) {
            return false;
        }
        boolean discovering = mAdapter.isDiscovering();
        LogUtils.w(TAG, "Bluetooth isDiscovering=>" + discovering);
        return discovering;
    }

    /**
     * 开始扫描
     *
     * @return true: 执行成功, false: 执行失败
     */
    public boolean startDiscovery() {
        if (!isBtExist()) {
            return false;
        }

        if (isDiscovering()) {
            LogUtils.w(TAG, "Bluetooth startDiscovery=>isDiscovering");
            return true;
        }
        boolean startDiscovery = mAdapter.startDiscovery();
        LogUtils.w(TAG, "Bluetooth startDiscovery=>" + startDiscovery);
        return startDiscovery;
    }

    /**
     * 取消扫描
     *
     * @return true: 执行成功, false: 执行失败
     */
    public boolean cancelDiscovery() {
        if (!isBtExist()) {
            return false;
        }

        if (!isDiscovering()) {
            LogUtils.w(TAG, "Bluetooth not discovery !!");
            return false;
        }
        boolean cancelDiscovery = mAdapter.cancelDiscovery();
        LogUtils.w(TAG, "Bluetooth discovery=>cancelDiscovery !!cancelDiscovery=>" + cancelDiscovery);
        return cancelDiscovery;
    }

    public boolean setPriorityAutoConnect(BluetoothDevice device, int profile) {
        if (profile == BluetoothProfile.HEADSET) {
            return mBluetoothHeadsetClientImpl.setPriorityAutoConnect(device);
        } else if (profile == BluetoothProfile.A2DP) {
            return mBluetoothA2dpSinkImpl.setPriorityAutoConnect(device);
        }
        return false;
    }

    private final BaseImpl.CallBack mBaseCallBack = new BaseImpl.CallBack() {

        @Override
        public void onConnect(boolean connected) {
            if (mBluetoothCallBackImpl != null) {
                mBluetoothCallBackImpl.onConnect(0, connected);
            }
        }
    };

    /**
     * 获取连接的设备列表
     *
     * @return 设备列表
     */
    public List<BluetoothDevice> getConnectedPhoneDevices() {
        List<BluetoothDevice> bondedList = getBondedDevices();
        Set<BluetoothDevice> setList = new HashSet<>();
        for (BluetoothDevice device : bondedList) {
            if (isConnectedHFP(device) || isConnectedA2dp(device)) {
                setList.add(device);
            }
        }
        return new ArrayList<>(setList);
    }
}
