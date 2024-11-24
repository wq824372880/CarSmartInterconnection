package com.zeekrlife.connect.core.manager.bluetooth.ctrl;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;


import com.zeekrlife.common.util.SPUtils;
import com.zeekrlife.connect.core.ConnectServiceImpl;
import com.zeekrlife.connect.core.constants.SPConstants;
import com.zeekrlife.connect.core.manager.bluetooth.MyBluetoothManager;
import com.zeekrlife.connect.core.manager.bluetooth.constant.BluetoothConst;
import com.zeekrlife.connect.core.manager.bluetooth.constant.UnifyBluetoothDevice;
import com.zeekrlife.connect.core.manager.bluetooth.listener.IBluetoothCallBack;
import com.zeekrlife.connect.core.manager.bluetooth.utils.TypeMainUtil;
import com.zeekrlife.connect.core.manager.bluetooth.utils.xLog;
import com.zeekrlife.net.interception.logging.util.LogExtKt;

import java.util.List;

public class BluetoothBroadcastCtrl {
    public final static String TAG = "BT_HiCar:BluetoothBroadcastCtrl***";
    private final Context mContext;
    private final HandlerThread handlerThread;
    private Handler mHandler;

    /**
     * 回调列表
     */
    private IBluetoothCallBack mCallback;

    public BluetoothBroadcastCtrl(Context ctx) {
        mContext = ctx;
        handlerThread = new HandlerThread("BluetoothHandlerThread");
        registerBtStatusReceiver(mContext);
        xLog.d(TAG, "BluetoothBroadcastCtrl init");
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
        mCallback = null;
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

    private void notifyBtConnectStateChange(int oldConnState, int newConnState, BluetoothDevice device) {
        if (mCallback != null) {
            mCallback.onBtConnectStateChange(oldConnState, newConnState,
                    new UnifyBluetoothDevice(BluetoothConst.mainDeviceType, device, null));
        }
    }

    private void notifyBtProfileConnectStateChange(int oldStatus, int newStatus,
                                                   int profileType, BluetoothDevice device) {
        if (mCallback != null) {
            mCallback.onProfileConnectStateChange(oldStatus, newStatus, profileType,
                    new UnifyBluetoothDevice(BluetoothConst.mainDeviceType, device, null));
        }
    }

    private void notifyBtBondedStateChange(int state, BluetoothDevice device, int reason) {
        if (mCallback != null) {
            mCallback.onBtMainBondedStateChange(state, device, reason);
        }
    }

    private void notifyBtDiscoveryStateChange(boolean started) {
        if (mCallback != null) {
            mCallback.onBtDiscoveryStateChange(started);
        }
    }

    private void notifyBtNewFondDevice(BluetoothDevice device) {
        if (mCallback != null) {
            mCallback.onBtNewFondDevice(new UnifyBluetoothDevice(BluetoothConst.mainDeviceType, device, null));
        }
    }

    private void notifyBtRemoteNameChange(String name, BluetoothDevice device) {
        if (mCallback != null) {
            mCallback.onBtRemoteNameChange(name,
                    new UnifyBluetoothDevice(BluetoothConst.mainDeviceType, device, null));
        }
    }

    private void notifyLocalNameChange() {
        if (mCallback != null) {
            mCallback.onLocalBtNameChange();
        }
    }

    private void notifyPairMatchRequest(String ssp, BluetoothDevice device) {
        if (mCallback != null) {
            mCallback.onMainPairMatchRequest(ssp, device);
        }
    }

    public static final String HEADSET_ACTION_CONNECTION_STATE_CHANGED = "android.bluetooth.headsetclient.profile.action.CONNECTION_STATE_CHANGED";
    public static final String A2DP_ACTION_CONNECTION_STATE_CHANGED = "android.bluetooth.a2dp-sink.profile.action.CONNECTION_STATE_CHANGED";


    /**************************************** 广播 Begin **************************/
    private void registerBtStatusReceiver(Context ctx) {
        IntentFilter filter = new IntentFilter();
        // 蓝牙开关状态
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        // 本机的蓝牙连接状态发生变化（连接第一个远程设备与断开最后一个远程设备才触发）
        filter.addAction(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED);
        // 蓝牙扫描
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);

        //self BT name changed
        filter.addAction(BluetoothAdapter.ACTION_LOCAL_NAME_CHANGED);
        // 扫描到设备
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        // 远程设备名称改变
        filter.addAction(BluetoothDevice.ACTION_NAME_CHANGED);
        // 蓝牙绑定状态
        filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);

        //HeadSet 协议状态
        filter.addAction(HEADSET_ACTION_CONNECTION_STATE_CHANGED);
        //A2Dp 协议状态
        filter.addAction(A2DP_ACTION_CONNECTION_STATE_CHANGED);

        filter.addAction(BluetoothDevice.ACTION_PAIRING_REQUEST);
        filter.setPriority(1000008);
        ctx.registerReceiver(mBtStatusReceiver, filter);
    }

    private void unregisterBtStatusReceiver(Context ctx) {
        ctx.unregisterReceiver(mBtStatusReceiver);
    }

    private final BroadcastReceiver mBtStatusReceiver = new BroadcastReceiver() {
        @SuppressLint("MissingPermission")
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                String action = intent.getAction();
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
//                xLog.e(TAG, "BT_HiCar:BluetoothBroadcastCtrl*** BroadcastReceiver onReceive action: " + action + ", device: " + device);
                if (BluetoothDevice.ACTION_PAIRING_REQUEST.equals(action) && ConnectServiceImpl.getInstance().getCurrentEventType() == 101
                    && device != null && device.getAddress().contentEquals(ConnectServiceImpl.getInstance().getConnectedDeviceAddress())) {
                    abortBroadcast();
                    device.setPairingConfirmation(true);
                    xLog.e(TAG, "start to pair, deviceName: " + device.getName() + ", deviceAddress: " + device.getAddress() + "and device.setPairingConfirmation(true);");
                }

                if (device != null) {
                    SPUtils.getInstance().put(SPConstants.DEVICE_NAME,device.getName(),true);
                }

                if (mHandler != null) {
                    Message msg = Message.obtain();
                    msg.obj = intent;
                    mHandler.sendMessage(msg);
                }

            }
        }
    };


    /**************************************** 广播 End **************************/

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
                        xLog.d(TAG, "mBtStatusReceiver ACTION_STATE_CHANGED state == " + state);
                        notifyBtOpenStateChange(state);
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
                        BluetoothDevice device = mIntent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                        xLog.d(TAG, "mBtStatusReceiver ACTION_CONNECTION_STATE_CHANGED [" + device + "]" +
                                ",newConnState " + newConnState + ",oldConnState " + oldConnState);
                        notifyBtConnectStateChange(oldConnState, newConnState, device);
                        break;
                    }
                    case BluetoothDevice.ACTION_BOND_STATE_CHANGED: {
                        BluetoothDevice device = mIntent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                        // 0 成功，1，失败，4，超时
                        int reason = mIntent.getIntExtra("android.bluetooth.device.extra.REASON", -1);
                        int state = mIntent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, -1);
                        xLog.d(TAG, "mBtStatusReceiver ACTION_BOND_STATE_CHANGED [" + device + "]" +
                                ",state " + state + ",reason " + reason);
                        notifyBtBondedStateChange(state, device, reason);
                        break;
                    }

                    case BluetoothAdapter.ACTION_DISCOVERY_STARTED:
                        xLog.d(TAG, "mBtStatusReceiver ACTION_DISCOVERY_STARTED");
                        notifyBtDiscoveryStateChange(true);
                        break;
                    case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:
                        xLog.d(TAG, "mBtStatusReceiver ACTION_DISCOVERY_FINISHED");
                        notifyBtDiscoveryStateChange(false);
                        break;
                    case BluetoothDevice.ACTION_FOUND: {
                        BluetoothDevice device = mIntent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                        xLog.d(TAG, "mBtStatusReceiver ACTION_FOUND [" + device.getName() + " == " + device.getAddress() + "]");
                        if (TextUtils.isEmpty(device.getName())) {
                            return;
                        }
                        if (TypeMainUtil.isHeadSet(device) || TypeMainUtil.isHid(device)) {
                            return;
                        }
                        notifyBtNewFondDevice(device);
                        break;
                    }
                    case BluetoothDevice.ACTION_NAME_CHANGED: {
                        // 远程蓝牙设备的名称被发现改变 或者 第一次发现远程蓝牙设备的名称的时候发出该广播
                        String newName = mIntent.getStringExtra(BluetoothDevice.EXTRA_NAME);
                        BluetoothDevice device = mIntent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                        xLog.d(TAG, "mBtStatusReceiver ACTION_NAME_CHANGED [" + device.getName() + " == " + device.getAddress() + "]");
                        if (TypeMainUtil.isHeadSet(device) || TypeMainUtil.isHid(device)) {
                            return;
                        }
                        notifyBtRemoteNameChange(newName, device);
                        break;
                    }
                    case BluetoothAdapter.ACTION_LOCAL_NAME_CHANGED:
                        xLog.d(TAG, "mBtStatusReceiver ACTION_LOCAL_NAME_CHANGED [" + BluetoothAdapter.getDefaultAdapter().getName() + "]");
                        notifyLocalNameChange();
                        break;
                    case BluetoothDevice.ACTION_PAIRING_REQUEST: {
                        BluetoothDevice device = mIntent
                                .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                        String ssp = String.format("%06d", mIntent.getIntExtra(BluetoothDevice.EXTRA_PAIRING_KEY, 0));
                        xLog.e(TAG, "mBtStatusReceiver ACTION_PAIRING_REQUEST [" + ssp + "]");
                        notifyPairMatchRequest(ssp, device);
//                        if (device != null) {
//                            if (ActivityCompat.checkSelfPermission(App.application, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
//                                return;
//                            }
//                            device.setPairingConfirmation(true);
//                        }
                        break;
                    }
                    case HEADSET_ACTION_CONNECTION_STATE_CHANGED: {
                        BluetoothDevice device = mIntent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                        int newState = mIntent.getIntExtra(BluetoothProfile.EXTRA_STATE, 0);
                        int oldState = mIntent.getIntExtra(BluetoothProfile.EXTRA_PREVIOUS_STATE, 0);

                        xLog.d(TAG, "mBtStatusReceiver HEADSET_ACTION_CONNECTION_STATE_CHANGED [" + device + "]" +
                                ",newState " + newState + ",oldState " + oldState);
                        notifyBtProfileConnectStateChange(oldState, newState, BluetoothProfile.HEADSET, device);
                        break;
                    }
                    case A2DP_ACTION_CONNECTION_STATE_CHANGED: {
                        BluetoothDevice device = mIntent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                        int newState = mIntent.getIntExtra(BluetoothProfile.EXTRA_STATE, 0);
                        int oldState = mIntent.getIntExtra(BluetoothProfile.EXTRA_PREVIOUS_STATE, 0);
                        xLog.d(TAG, "mBtStatusReceiver A2DP_ACTION_CONNECTION_STATE_CHANGED [" + device + "]" +
                                ",newState " + newState + ",oldState " + oldState);
                        notifyBtProfileConnectStateChange(oldState, newState, BluetoothProfile.A2DP, device);
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
