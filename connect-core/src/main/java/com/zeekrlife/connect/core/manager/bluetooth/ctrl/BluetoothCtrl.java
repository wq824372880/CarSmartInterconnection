package com.zeekrlife.connect.core.manager.bluetooth.ctrl;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.ext.SubBluetoothDevice;
import android.content.Context;
import android.os.SystemClock;

import com.zeekrlife.connect.core.manager.bluetooth.constant.BluetoothConst;
import com.zeekrlife.connect.core.manager.bluetooth.constant.UnifyBluetoothDevice;
import com.zeekrlife.connect.core.manager.bluetooth.listener.IBluetoothCallBack;
import com.zeekrlife.connect.core.manager.bluetooth.utils.xLog;

import java.util.ArrayList;
import java.util.List;

/**
 * @author qiangwang
 * @e-mail qiangwang@ecarx.com.cn
 * @time 2020/08/31
 * @description Aosp 的蓝牙管理
 */
public class BluetoothCtrl {
    public final static String TAG = "BT_HiCar";

    /**
     * BluetoothBroadcastCtrl
     */
    private BluetoothBroadcastCtrl mBtBroadcastCtrl;

    /**
     * SubBluetoothBroadcastCtrl
     */
    private SubBluetoothBroadcastCtrl mSubBtBroadcastCtrl;

    /**
     * BluetoothConfigCtrl
     */
    private final BluetoothConfigCtrl mBluetoothConfigCtrl;

    /**
     * 回调
     */
    private final IBluetoothCallBack mBluetoothCallBackImpl;

    /**
     * 扫描到的设备列表
     */
    private final List<UnifyBluetoothDevice> mFondDevices = new ArrayList<>();

    /**
     * 蓝牙名称是否初始化话过
     */
    private boolean mIsBluetoothNameInit = false;

    /**
     * 蓝牙设备打开的时间
     */
    private long mBtOpenedTime = 0;


    public BluetoothCtrl(Context ctx, IBluetoothCallBack callBack) {
        mBluetoothCallBackImpl = callBack;
        mBluetoothConfigCtrl = new BluetoothConfigCtrl(ctx);

        mBtBroadcastCtrl = new BluetoothBroadcastCtrl(ctx);
        mBtBroadcastCtrl.onEnter();
        mBtBroadcastCtrl.setCallBack(mBroadcastCallBack, true);

//        mSubBtBroadcastCtrl = new SubBluetoothBroadcastCtrl(ctx);
//        mSubBtBroadcastCtrl.onEnter();
//        mSubBtBroadcastCtrl.setCallBack(mBroadcastCallBack, true);

        mIsBluetoothNameInit = mBluetoothConfigCtrl.isBluetoothNameInit();
    }

    /**
     * 退出
     */
    public void onExit() {
        if (mBtBroadcastCtrl != null) {
            mBtBroadcastCtrl.setCallBack(mBroadcastCallBack, false);
            mBtBroadcastCtrl.onExit();
        }
//        if (mSubBtBroadcastCtrl != null) {
//            mSubBtBroadcastCtrl.setCallBack(mBroadcastCallBack, false);
//            mSubBtBroadcastCtrl.onExit();
//        }
    }

    /**
     * 检查服务
     */
    public void checkService() {

    }

    /**
     * 停止服务
     */
    public void stopService() {

    }

    /**
     * 获取扫描到的设备列表
     *
     * @return 蓝牙设备列表
     */
    public List<UnifyBluetoothDevice> getFoundDevices() {
        return new ArrayList<>(mFondDevices);
    }

    /**
     * 添加一个设备到扫描列表中
     *
     * @param device 蓝牙设备
     */
    private void setNewFondDevice(UnifyBluetoothDevice device) {
        int type = device.getType();
        String address = device.getAddress();

        if (type == BluetoothConst.subDeviceType) {
            mFondDevices.removeIf(found -> found.getAddress().equals(address));
            mFondDevices.add(device);
        } else if (type == BluetoothConst.mainDeviceType) {
            mFondDevices.removeIf(found -> found.getType() == BluetoothConst.mainDeviceType && found.getAddress().equals(address));
            mFondDevices.add(device);
        }
    }

    /**
     * 广播回调
     */
    private final IBluetoothCallBack mBroadcastCallBack = new IBluetoothCallBack() {

        @Override
        public void onConnect(int type, boolean connected) {
            if (mBluetoothCallBackImpl != null) {
                mBluetoothCallBackImpl.onConnect(type, connected);
            }
        }

        @Override
        public void onBtOpenStateChange(int state) {
            mFondDevices.clear();

            if (state == BluetoothAdapter.STATE_ON) {
                mBtOpenedTime = SystemClock.elapsedRealtime();
            }

            if (mBluetoothCallBackImpl != null) {
                mBluetoothCallBackImpl.onBtOpenStateChange(state);
            }
        }

        @Override
        public void onBtConnectStateChange(int oldConnState, int newConnState, UnifyBluetoothDevice device) {
            if (mBluetoothCallBackImpl != null) {
                mBluetoothCallBackImpl.onBtConnectStateChange(oldConnState, newConnState, device);
            }
        }

        @Override
        public void onProfileConnectStateChange(int oldStatus, int newStatus,
                                                int profileType, UnifyBluetoothDevice device) {
            if (mBluetoothCallBackImpl != null) {
                mBluetoothCallBackImpl.onProfileConnectStateChange(oldStatus, newStatus, profileType, device);
            }
        }

        @Override
        public void onBtMainBondedStateChange(int state, BluetoothDevice device, int reason) {
            if (mBluetoothCallBackImpl != null) {
                mBluetoothCallBackImpl.onBtMainBondedStateChange(state, device, reason);
            }
        }

        @Override
        public void onBtSubBondedStateChange(int state, SubBluetoothDevice device, int reason) {
            if (mBluetoothCallBackImpl != null) {
                mBluetoothCallBackImpl.onBtSubBondedStateChange(state, device, reason);
            }
        }

        @Override
        public void onBtDiscoveryStateChange(boolean started) {
            long between = (SystemClock.elapsedRealtime() - mBtOpenedTime);
            int maxDiscoveryTime = 60 * 1000;
            xLog.d(TAG, "between=" + between + ", started=" + started);
            if (started && (between > maxDiscoveryTime)) {
                mFondDevices.clear();
            }
            if (mBluetoothCallBackImpl != null) {
                mBluetoothCallBackImpl.onBtDiscoveryStateChange(started);
            }
        }

        @Override
        public void onBtNewFondDevice(UnifyBluetoothDevice device) {
            setNewFondDevice(device);
            if (mBluetoothCallBackImpl != null) {
                mBluetoothCallBackImpl.onBtNewFondDevice(device);
            }
        }

        @Override
        public void onBtRemoteNameChange(String newName, UnifyBluetoothDevice device) {
            setNewFondDevice(device);
            if (mBluetoothCallBackImpl != null) {
                mBluetoothCallBackImpl.onBtRemoteNameChange(newName, device);
            }
        }

        @Override
        public void onLocalBtNameChange() {
            if (mBluetoothCallBackImpl != null) {
                mBluetoothCallBackImpl.onLocalBtNameChange();
            }
        }

        @Override
        public void onMainPairMatchRequest(String ssp, BluetoothDevice pairingDevice) {
            if (mBluetoothCallBackImpl != null) {
                mBluetoothCallBackImpl.onMainPairMatchRequest(ssp, pairingDevice);
            }
        }

        @Override
        public void onSubPairMatchRequest(String ssp, SubBluetoothDevice pairingDevice) {
            if (mBluetoothCallBackImpl != null) {
                mBluetoothCallBackImpl.onSubPairMatchRequest(ssp, pairingDevice);
            }
        }
    };
}
