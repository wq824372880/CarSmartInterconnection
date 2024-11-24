package com.zeekrlife.connect.core.manager.bluetooth.listener;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.ext.SubBluetoothDevice;

import com.zeekrlife.connect.core.manager.bluetooth.constant.UnifyBluetoothDevice;

/**
 * @author qiangwang
 * @e-mail qiangwang@ecarx.com.cn
 * @time 2020/09/07
 * @description 蓝牙回调
 */
public interface IBluetoothCallBack {
    /**
     * 连接状态
     *
     * @param type      类型
     * @param connected 是否在连接中, true: 连接成功, false: 断开连接
     */
    void onConnect(int type, boolean connected);

    /**
     * 蓝牙打开状态
     *
     * @param state 蓝牙打开状态
     */
    void onBtOpenStateChange(int state);

    /**
     * 蓝牙连接状态
     *
     * @param oldConnState  连接状态
     * @param device 当前设备
     */
    void onBtConnectStateChange(int oldConnState, int newConnState, UnifyBluetoothDevice device);

    /**
     * 蓝牙profile 连接状态
     *
     * @param newStatus       newStatus
     * @param profileType profileType
     * @param device      device
     */

    void onProfileConnectStateChange(int oldStatus, int newStatus,
                                     int profileType, UnifyBluetoothDevice device);

    /**
     * main蓝牙绑定状态
     *
     * @param state  绑定状态
     * @param device 当前设备
     */
    void onBtMainBondedStateChange(int state, BluetoothDevice device, int reason);

    /**
     * sub蓝牙绑定状态
     *
     * @param state  绑定状态
     * @param device 当前设备
     */
    void onBtSubBondedStateChange(int state, SubBluetoothDevice device, int reason);

    /**
     * 新查找到的设备
     *
     * @param device 查找到的设备
     */
    void onBtNewFondDevice(UnifyBluetoothDevice device);

    /**
     * 查找状态
     *
     * @param started 是否已经开始查找, true: 开始查找, false: 结束查找
     */
    void onBtDiscoveryStateChange(boolean started);

    /**
     * 远程蓝牙设备名称改变
     *
     * @param newName 名称
     * @param device  远程设备
     */
    void onBtRemoteNameChange(String newName, UnifyBluetoothDevice device);

    /**
     * 本机蓝牙名称变更
     */
    void onLocalBtNameChange();

    /**
     * 蓝牙匹配请求
     */
    void onMainPairMatchRequest(String ssp, BluetoothDevice pairingDevice);

    void onSubPairMatchRequest(String ssp, SubBluetoothDevice pairingDevice);
}
