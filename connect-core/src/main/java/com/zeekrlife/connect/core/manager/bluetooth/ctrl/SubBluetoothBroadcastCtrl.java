package com.zeekrlife.connect.core.manager.bluetooth.ctrl;

import android.Manifest;
import android.bluetooth.ext.BluetoothProfile;
import android.bluetooth.ext.BluetoothA2dp;
import android.bluetooth.ext.BluetoothAdapter;
import android.bluetooth.ext.BluetoothHidHost;
import android.bluetooth.ext.SubBluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;

import androidx.core.app.ActivityCompat;

import com.zeekrlife.connect.core.app.App;
import com.zeekrlife.connect.core.manager.bluetooth.constant.BluetoothConst;
import com.zeekrlife.connect.core.manager.bluetooth.constant.UnifyBluetoothDevice;
import com.zeekrlife.connect.core.manager.bluetooth.listener.IBluetoothCallBack;
import com.zeekrlife.connect.core.manager.bluetooth.utils.TypeSubUtil;
import com.zeekrlife.connect.core.manager.bluetooth.utils.xLog;

public class SubBluetoothBroadcastCtrl {
    public final static String TAG = xLog.SUB_TAG_BLUETOOTH;
    private final Context mContext;
    private final HandlerThread handlerThread;
    private Handler mHandler;
    private IBluetoothCallBack mCallback;

    public SubBluetoothBroadcastCtrl(Context ctx) {
        mContext = ctx;
        handlerThread = new HandlerThread("SubBluetoothBroadcastCtrl");
        registerBtStatusReceiver(mContext);
//        xLog.d(TAG, "SubBluetoothBroadcastCtrl init");
    }

    /***
     * swe检测出
     * 将启动线程独立出来，放在构造方法中，如果有子类实现该类，可能会导致子类还未完成构造，子线程已经启动
     */
    public void onEnter() {
        if (handlerThread != null) {
            handlerThread.start();
            mHandler = new BroadcastRunnable(handlerThread.getLooper());
        }
    }

    /**
     * 退出
     */
    public void onExit() {
        unregisterBtStatusReceiver(mContext);
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
        if (handlerThread != null) {
            handlerThread.quit();
        }
        xLog.d(TAG, "onExit");
    }

    /**
     * 注册回调
     *
     * @param callBack   回调
     * @param isRegister true: 注册, false：取消注册
     */
    public void setCallBack(IBluetoothCallBack callBack, boolean isRegister) {
        if (callBack != null) {
            if (isRegister) {
                mCallback = callBack;
            } else {
                mCallback = null;
            }
        }
    }

    private void notifyBtOpenStateChange(int state) {
        if (mCallback != null) {
            mCallback.onBtOpenStateChange(state);
        }
    }

    private void notifyBtConnectStateChange(int oldConnState, int newConnState, SubBluetoothDevice device) {
        if (mCallback != null) {
            mCallback.onBtConnectStateChange(oldConnState, newConnState,
                    new UnifyBluetoothDevice(BluetoothConst.subDeviceType, null, device));
        }
    }

    private void notifyBtProfileConnectStateChange(int oldStatus, int newStatus,
                                                   int profileType, SubBluetoothDevice device) {
        if (mCallback != null) {
            mCallback.onProfileConnectStateChange(oldStatus, newStatus, profileType,
                    new UnifyBluetoothDevice(BluetoothConst.subDeviceType, null, device));
        }
    }

    private void notifyBtBondedStateChange(int state, SubBluetoothDevice device, int reason) {
        if (mCallback != null) {
            mCallback.onBtSubBondedStateChange(state, device, reason);
        }
    }

    private void notifyBtDiscoveryStateChange(boolean started) {
        if (mCallback != null) {
            mCallback.onBtDiscoveryStateChange(started);
        }
    }

    private void notifyBtNewFondDevice(SubBluetoothDevice device) {
        if (mCallback != null) {
            mCallback.onBtNewFondDevice(new UnifyBluetoothDevice(BluetoothConst.subDeviceType, null, device));
        }
    }

    private void notifyBtRemoteNameChange(String name, SubBluetoothDevice device) {
        if (mCallback != null) {
            mCallback.onBtRemoteNameChange(name,
                    new UnifyBluetoothDevice(BluetoothConst.subDeviceType, null, device));
        }
    }

    private void notifyLocalNameChange() {
        if (mCallback != null) {
            mCallback.onLocalBtNameChange();
        }
    }

    private void notifyPairMatchRequest(String ssp, SubBluetoothDevice device) {
        if (mCallback != null) {
            mCallback.onSubPairMatchRequest(ssp, device);
        }
    }

    private void registerBtStatusReceiver(Context ctx) {
//        IntentFilter filter = new IntentFilter();
//        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        // 本机的蓝牙连接状态发生变化（连接第一个远程设备与断开最后一个远程设备才触发）
//        filter.addAction(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED);
//        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
//        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
//        filter.addAction(SubBluetoothDevice.ACTION_BOND_STATE_CHANGED);
//        filter.addAction(SubBluetoothDevice.ACTION_FOUND);
//        filter.addAction(SubBluetoothDevice.ACTION_PAIRING_REQUEST);
        // 耳机
//        filter.addAction(BluetoothA2dp.ACTION_CONNECTION_STATE_CHANGED);
        // 手柄
//        filter.addAction(BluetoothHidHost.ACTION_CONNECTION_STATE_CHANGED);
//        ctx.registerReceiver(mBtStatusReceiver, filter);
    }

    private void unregisterBtStatusReceiver(Context ctx) {
        ctx.unregisterReceiver(mBtStatusReceiver);
    }

    private final BroadcastReceiver mBtStatusReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                String action = intent.getAction();
                xLog.e(TAG, "mBtStatusReceiver action=" + action);
//                if (SubBluetoothDevice.ACTION_PAIRING_REQUEST.equals(action)) {
//                    abortBroadcast();
//                    SubBluetoothDevice device = intent.getParcelableExtra(SubBluetoothDevice.EXTRA_DEVICE);
//                    if (device != null) {
//                        xLog.e(TAG, "start to pair, deviceName: " + device.getName() + ", deviceAddress: " + device.getAddress());
//                    } else {
//                        xLog.e(TAG, "device is null, nothing to do");
//                    }
//                }

                if (mHandler != null) {
                    Message msg = Message.obtain();
                    msg.obj = intent;
                    mHandler.sendMessage(msg);
                }

            }
        }
    };

    /**
     * 广播线程
     */
    private class BroadcastRunnable extends Handler {
        public BroadcastRunnable(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            try {
                if (msg == null) {
                    xLog.d(TAG, "BroadcastRunnable msg=null!");
                    return;
                }
                Intent mIntent = (Intent) msg.obj;
                if (mIntent == null) {
                    xLog.d(TAG, "BroadcastRunnable mIntent=null!");
                    return;
                }
                String action = mIntent.getAction();
                if (action == null) {
                    xLog.d(TAG, "mBtStatusReceiver action=null!");
                    return;
                }

                switch (action) {
                    case BluetoothAdapter.ACTION_STATE_CHANGED: {
                        int state = mIntent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
                                BluetoothAdapter.ERROR);
//                        xLog.d(TAG, "mBtStatusReceiver ACTION_STATE_CHANGED state == " + state);
                        // notifyBtOpenStateChange(state);
                        break;
                    }
                    case BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED: {
                        /*
                         * 本机的蓝牙连接状态发生变化
                         *
                         * 特指“无任何连接”→“连接任意远程设备”，以及“连接任一或多个远程设备”→“无任何连接”的状态变化，
                         * 即“连接第一个远程设备”与“断开最后一个远程设备”时才会触发该Action
                         */
                        //获取蓝牙广播中的蓝牙连接新状态
                        int oldConnState = mIntent.getIntExtra(BluetoothAdapter.EXTRA_PREVIOUS_CONNECTION_STATE, 0);
                        int newConnState = mIntent.getIntExtra(BluetoothAdapter.EXTRA_CONNECTION_STATE, 0);
                        // 当前远程蓝牙设备
                        SubBluetoothDevice device = mIntent.getParcelableExtra(SubBluetoothDevice.EXTRA_DEVICE);
//                        xLog.d(TAG, "mBtStatusReceiver ACTION_CONNECTION_STATE_CHANGED [" + device + "]" +
//                                ",oldConnState " + oldConnState + ", newConnState " + newConnState);
//                        notifyBtConnectStateChange(oldConnState, newConnState, device);
                        break;
                    }
                    case SubBluetoothDevice.ACTION_BOND_STATE_CHANGED: {
                        SubBluetoothDevice device = mIntent.getParcelableExtra(SubBluetoothDevice.EXTRA_DEVICE);
                        // 0 成功，1，失败，4，超时
                        int reason = mIntent.getIntExtra("android.bluetooth.ext.device.extra.REASON", -1);
                        int state = mIntent.getIntExtra(SubBluetoothDevice.EXTRA_BOND_STATE, -1);
//                        xLog.d(TAG, "mBtStatusReceiver ACTION_BOND_STATE_CHANGED [" + device + "]" +
//                                ",state " + state + ",reason " + reason);
//                        notifyBtBondedStateChange(state, device, reason);
                        break;
                    }

                    case BluetoothAdapter.ACTION_DISCOVERY_STARTED:
//                        xLog.d(TAG, "mBtStatusReceiver ACTION_DISCOVERY_STARTED");
//                        notifyBtDiscoveryStateChange(true);
                        break;
                    case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:
//                        xLog.d(TAG, "mBtStatusReceiver ACTION_DISCOVERY_FINISHED");
//                        notifyBtDiscoveryStateChange(false);
                        break;
                    case SubBluetoothDevice.ACTION_FOUND: {
                        SubBluetoothDevice device = mIntent.getParcelableExtra(SubBluetoothDevice.EXTRA_DEVICE);
                        xLog.d(TAG, "mBtStatusReceiver ACTION_FOUND [" + device.getName() + " == " + device.getAddress() + "]");
                        if (TextUtils.isEmpty(device.getName())) {
                            return;
                        }
                        if (TypeSubUtil.isHeadSet(device) || TypeSubUtil.isHid(device)) {
                            notifyBtNewFondDevice(device);
                        }
                        break;
                    }
                    case SubBluetoothDevice.ACTION_NAME_CHANGED: {
                        // 远程蓝牙设备的名称被发现改变 或者 第一次发现远程蓝牙设备的名称的时候发出该广播
                        String newName = mIntent.getStringExtra(SubBluetoothDevice.EXTRA_NAME);
                        SubBluetoothDevice device = mIntent.getParcelableExtra(SubBluetoothDevice.EXTRA_DEVICE);
//                        xLog.d(TAG, "mBtStatusReceiver ACTION_NAME_CHANGED [" + device.getName() + " == " + device.getAddress() + "]");
//                        if (TypeSubUtil.isHeadSet(device) || TypeSubUtil.isHid(device)) {
//                            notifyBtRemoteNameChange(newName, device);
//                        }
                        break;
                    }
                    case BluetoothAdapter.ACTION_LOCAL_NAME_CHANGED:
//                        xLog.d(TAG, "mBtStatusReceiver ACTION_LOCAL_NAME_CHANGED [" + BluetoothAdapter.getDefaultAdapter().getName() + "]");
//                        notifyLocalNameChange();
                        break;
                    case SubBluetoothDevice.ACTION_PAIRING_REQUEST: {
//                        SubBluetoothDevice device = mIntent
//                                .getParcelableExtra(SubBluetoothDevice.EXTRA_DEVICE);
//                        String ssp = String.format("%06d", mIntent.getIntExtra(SubBluetoothDevice.EXTRA_PAIRING_KEY, 0));
//                        xLog.e(TAG, "mBtStatusReceiver ACTION_PAIRING_REQUEST [" + ssp + "]");
//                        if (device != null) {
//                            if (ActivityCompat.checkSelfPermission(App.application, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
//                                return;
//                            }
//                            device.setPairingConfirmation(true);
//                        }
//                        notifyPairMatchRequest(ssp, device);
                        break;
                    }
                    case BluetoothA2dp.ACTION_CONNECTION_STATE_CHANGED: {
                        SubBluetoothDevice device = mIntent.getParcelableExtra(SubBluetoothDevice.EXTRA_DEVICE);
                        int newState = mIntent.getIntExtra(BluetoothProfile.EXTRA_STATE, 0);
                        int oldState = mIntent.getIntExtra(BluetoothProfile.EXTRA_PREVIOUS_STATE, 0);
//                        xLog.d(TAG, "mBtStatusReceiver BLUETOOTH_HEADSET_ACTION_CONNECTION_STATE_CHANGED [" + device + "]" +
//                                ",newState " + newState + ",oldState " + oldState);
//                        notifyBtProfileConnectStateChange(oldState, newState, BluetoothConst.PROFILE_HEADSET, device);
                        break;
                    }
                    case BluetoothHidHost.ACTION_CONNECTION_STATE_CHANGED: {
                        SubBluetoothDevice device = mIntent.getParcelableExtra(SubBluetoothDevice.EXTRA_DEVICE);
                        int newState = mIntent.getIntExtra(BluetoothProfile.EXTRA_STATE, 0);
                        int oldState = mIntent.getIntExtra(BluetoothProfile.EXTRA_PREVIOUS_STATE, 0);
//                        xLog.d(TAG, "mBtStatusReceiver HID_ACTION_CONNECTION_STATE_CHANGED [" + device + "]" +
//                                ",newState " + newState + ",oldState " + oldState);
//                        notifyBtProfileConnectStateChange(oldState, newState, BluetoothConst.PROFILE_HID, device);
                        break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                xLog.d(TAG, "mBtStatusReceiver process error");
            }
        }
    }

}
