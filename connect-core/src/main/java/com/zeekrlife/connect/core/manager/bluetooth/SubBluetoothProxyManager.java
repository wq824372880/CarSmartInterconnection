package com.zeekrlife.connect.core.manager.bluetooth;

import android.bluetooth.ext.BluetoothA2dp;
import android.bluetooth.ext.BluetoothAdapter;
import android.bluetooth.ext.BluetoothHidHost;
import android.bluetooth.ext.BluetoothProfile;
import android.bluetooth.ext.SubBluetoothDevice;
import android.content.Context;
import android.text.TextUtils;

import com.huawei.authagent.service.utils.LogUtils;
import com.zeekrlife.connect.core.manager.bluetooth.constant.BluetoothConst;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class SubBluetoothProxyManager {
    public final static String TAG = "SubBluetoothProxyManager";
    private final BluetoothAdapter mAdapter;
    private BluetoothA2dp mA2dp;
    private BluetoothHidHost mHidHost;

    private final BluetoothProfile.ServiceListener listener = new BluetoothProfile.ServiceListener() {
        @Override
        public void onServiceConnected(int profile, BluetoothProfile proxy) {
            LogUtils.d(TAG, "onServiceConnected profile: " + profile);
            if (profile == BluetoothProfile.A2DP) {
                mA2dp = (BluetoothA2dp) proxy;
            } else if (profile == BluetoothProfile.HID_HOST) {
                mHidHost = (BluetoothHidHost) proxy;
            }
        }

        @Override
        public void onServiceDisconnected(int profile) {
            LogUtils.d(TAG, "onServiceDisconnected profile: " + profile);
            if (profile == BluetoothProfile.A2DP) {
                if (mA2dp != null) mA2dp = null;
            } else if (profile == BluetoothProfile.HID_HOST) {
                if (mHidHost != null) mHidHost = null;
            }

        }
    };

    public SubBluetoothProxyManager(Context ctx) {
        mAdapter = Objects.requireNonNull(BluetoothAdapter.getDefaultAdapter());
        mAdapter.getProfileProxy(ctx, listener, BluetoothProfile.A2DP);
        mAdapter.getProfileProxy(ctx, listener, BluetoothProfile.HID_HOST);
    }

    /**
     * 退出
     */
    public void onExit() {

    }

    /**
     * 检查服务
     */
    public void checkService() {

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

        String btAddress = mAdapter.getName();
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
        return mAdapter.setScanMode(on ?
                BluetoothAdapter.SCAN_MODE_CONNECTABLE : BluetoothAdapter.SCAN_MODE_NONE);
    }

    /**
     * 获取绑定的蓝牙设备列表
     *
     * @return 蓝牙设备列表
     */
    public Set<SubBluetoothDevice> getBondedDevices() {
        return mAdapter.getBondedDevices();
    }

    /**
     * 移除指定的绑定蓝牙设备
     *
     * @param device 蓝牙设备
     * @return true: 执行成功, false: 执行失败
     */
    public boolean removeBond(SubBluetoothDevice device) {
        LogUtils.w(TAG, "removeBond device.name=" + device.getName() +
                ", device.address=" + device.getAddress());
        return device.removeBond();
    }

    /**
     * 指定的蓝牙设备是否已经绑定
     *
     * @param device 蓝牙设备
     * @return true: 已经绑定, false: 没有绑定
     */
    public boolean isDeviceBonded(SubBluetoothDevice device) {
        int state = device.getBondState();
        LogUtils.w(TAG, "isDeviceBonded  device.name=" + device.getName() + ", state=" + state);
        return (state == SubBluetoothDevice.BOND_BONDED);
    }

    /**
     * 指定的蓝牙设备是否已经绑定
     *
     * @param device 蓝牙设备
     * @return true: 已经绑定, false: 没有绑定
     */
    public boolean isDeviceBonding(SubBluetoothDevice device) {
        int state = device.getBondState();
        LogUtils.w(TAG, "isDeviceBonding  device.name=" + device.getName() + ", state=" + state);
        return (state == SubBluetoothDevice.BOND_BONDING);
    }

    public boolean isConnectingHid(SubBluetoothDevice device) {
        if (mHidHost == null) {
            return false;
        }
        int hidState = mHidHost.getConnectionState(device);
        LogUtils.w(TAG, "isConnectingHid  device.name=" + device.getName()
                + ", device.address=" + device.getAddress()
                + ",hidState " + hidState);
        return hidState == BluetoothProfile.STATE_CONNECTING;
    }

    public boolean isConnectingHeadset(SubBluetoothDevice device) {
        if (mA2dp == null) {
            return false;
        }
        int a2dpState = mA2dp.getConnectionState(device);
        LogUtils.w(TAG, "isConnectedHeadset  device.name=" + device.getName()
                + ", device.address=" + device.getAddress()
                + ",a2dpState " + a2dpState);
        return a2dpState == BluetoothProfile.STATE_CONNECTING;
    }

    /**
     * 手柄是否连接
     *
     * @param device 蓝牙设备
     * @return true: 已经连接, false: 没有连接
     */
    public boolean isConnectedHid(SubBluetoothDevice device) {
        if (mHidHost == null) {
            return false;
        }
        int hidState = mHidHost.getConnectionState(device);
        LogUtils.w(TAG, "isConnectedHid  device.name=" + device.getName()
                + ", device.address=" + device.getAddress()
                + ",hidState " + hidState);
        return hidState == BluetoothProfile.STATE_CONNECTED;
    }

    /**
     * 耳机是否连接
     *
     * @param device 蓝牙设备
     * @return true: 已经连接, false: 没有连接
     */
    public boolean isConnectedHeadset(SubBluetoothDevice device) {
        if (mA2dp == null) {
            return false;
        }
        int a2dpState = mA2dp.getConnectionState(device);
        LogUtils.w(TAG, "isConnectedHeadset  device.name=" + device.getName()
                + ", device.address=" + device.getAddress()
                + ",a2dpState " + a2dpState);
        return a2dpState == BluetoothProfile.STATE_CONNECTED;
    }

    /**
     * 连接手柄
     *
     * @param device 蓝牙设备
     * @return true: 执行成功, false: 执行失败
     */
    public boolean connectHID(SubBluetoothDevice device) {
        LogUtils.w(TAG, "connectHID  device.name=" + device.getName() +
                ", device.address=" + device.getAddress());
        if (mHidHost != null) {
            if (mHidHost.getConnectionPolicy(device) < BluetoothProfile.CONNECTION_POLICY_ALLOWED) {
                return mHidHost.setConnectionPolicy(device, BluetoothProfile.CONNECTION_POLICY_ALLOWED);
            }
            return mHidHost.connect(device);
        }
        return false;
    }

    /**
     * 连接耳机
     *
     * @param device 蓝牙设备
     * @return true: 执行成功, false: 执行失败
     */
    public boolean connectHeadset(SubBluetoothDevice device) {
        LogUtils.w(TAG, "connectHeadset  device.name=" + device.getName() +
                ", device.address=" + device.getAddress());
        if (mA2dp != null) {
            if (mA2dp.getConnectionPolicy(device) < BluetoothProfile.CONNECTION_POLICY_ALLOWED) {
                return mA2dp.setConnectionPolicy(device, BluetoothProfile.CONNECTION_POLICY_ALLOWED);
            }
            return mA2dp.connect(device);
        }
        return false;
    }

    /**
     * 断开手柄连接
     *
     * @param device 蓝牙设备
     * @return true: 执行成功, false: 执行失败
     */
    public boolean disconnectHid(SubBluetoothDevice device) {
        LogUtils.w(TAG, "disconnectHid device.name=" + device.getName() +
                ", device.address=" + device.getAddress());
        if (mHidHost != null) {
            return mHidHost.disconnect(device);
        }
        return false;
    }

    /**
     * 断开耳机连接
     *
     * @param device 蓝牙设备
     * @return true: 执行成功, false: 执行失败
     */
    public boolean disconnectHeadset(SubBluetoothDevice device) {
        LogUtils.w(TAG, "disconnectHeadset device.name=" + device.getName() +
                ", device.address=" + device.getAddress());
        if (mA2dp != null) {
            return mA2dp.disconnect(device);
        }
        return false;
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

    /**
     * 设置设备的优先级为自动连接
     *
     * @param device 蓝牙设备
     * @return true: 执行成功, false: 执行失败
     */
    public boolean setPriorityAutoConnect(SubBluetoothDevice device, int profile) {
        if (profile == BluetoothConst.PROFILE_HEADSET) {
            int currPriority = mA2dp.getPriority(device);
            if (currPriority == BluetoothProfile.CONNECTION_POLICY_ALLOWED) {
                return true;
            }
            return mA2dp.setConnectionPolicy(device, BluetoothProfile.CONNECTION_POLICY_ALLOWED);
        } else if (profile == BluetoothConst.PROFILE_HID) {
            int currPriority = mHidHost.getPriority(device);
            if (currPriority == BluetoothProfile.CONNECTION_POLICY_ALLOWED) {
                return true;
            }
            return mHidHost.setConnectionPolicy(device, BluetoothProfile.CONNECTION_POLICY_ALLOWED);
        }
        return false;
    }

    public List<SubBluetoothDevice> getConnectedHidDevices() {
        Set<SubBluetoothDevice> bondedList = getBondedDevices();
        Set<SubBluetoothDevice> setList = new HashSet<>();
        if (bondedList != null && !bondedList.isEmpty()) {
            for (SubBluetoothDevice device : bondedList) {
                if (isConnectedHid(device)) {
                    setList.add(device);
                }
            }
        }
        return new ArrayList<>(setList);
    }

    public List<SubBluetoothDevice> getConnectedHeadSetDevices() {
        Set<SubBluetoothDevice> bondedList = getBondedDevices();
        Set<SubBluetoothDevice> setList = new HashSet<>();
        if (bondedList != null && !bondedList.isEmpty()) {
            for (SubBluetoothDevice device : bondedList) {
                if (isConnectedHeadset(device)) {
                    setList.add(device);
                }
            }
        }
        return new ArrayList<>(setList);
    }
}
