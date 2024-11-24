package com.zeekrlife.connect.core.manager.bluetooth.impl;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.IBluetoothA2dp;
import android.bluetooth.IBluetoothA2dpSink;
import android.bluetooth.IBluetoothAvrcpController;
import android.bluetooth.IBluetoothHeadsetClient;
import android.bluetooth.IBluetoothHidHost;
import android.bluetooth.IBluetoothPbapClient;
import android.bluetooth.IBluetoothStateChangeCallback;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;


import com.huawei.managementsdk.common.LogUtils;
import com.zeekrlife.connect.core.manager.bluetooth.utils.InvokeUtil;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseImpl {
    public final static String TAG = "BaseImpl";
    /**
     * Default priority for devices that we try to auto-connect to and
     * and allow incoming connections for the profile
     **/
    public final static int PRIORITY_AUTO_CONNECT = 1000;

    /**
     * Default priority for devices that allow incoming
     * and outgoing connections for the profile
     **/
    public final static int PRIORITY_ON = 100;

    /**
     * Default priority for devices that does not allow incoming
     * connections and outgoing connections for the profile.
     **/
    public final static int PRIORITY_OFF = 0;

    /**
     * Default priority when not set or when the device is unpaired
     */
    public final static int PRIORITY_UNDEFINED = -1;

    protected Context mContext;

    protected IBluetoothManagerImpl mIBluetoothManagerImpl;
    /**
     * IBinder
     */
    private IBinder mIBinder;
    /**
     * 类名
     */
    private final String mBindClassName;
    /**
     * 是否在绑定中
     */
    private boolean mIsBinding = false;

    /**
     * 线程
     */
    private HandlerThread mHandlerThread;
    private MyHandler mHandler;

    /**
     * 获取绑定的服务
     *
     * @return 绑定的服务
     */
    public abstract android.os.IInterface getService();

    /**
     * 通知连接成功
     *
     * @param name    ComponentName
     * @param service IBinder
     */
    protected abstract void onServiceConnected(ComponentName name, IBinder service);

    /**
     * 通知断开连接
     *
     * @param name ComponentName
     */
    protected abstract void onServiceDisconnected(ComponentName name);

    /**
     * 蓝牙状态改变
     *
     * @param on true:打开, false: 关闭
     */
    protected abstract void onBluetoothStateChange(boolean on);

    /**
     * 回调
     */
    private final CallBack mCallBack;

    /**
     * 回调
     */
    public interface CallBack {
        /**
         * 连接状态
         *
         * @param connected true: 连接成功, false: 断开连接
         */
        void onConnect(boolean connected);
    }

    public BaseImpl(Context ctx, String className, IBluetoothManagerImpl bluetoothManagerImpl, CallBack callBack) {
        mContext = ctx;
        mCallBack = callBack;
        mBindClassName = className;
        mIBluetoothManagerImpl = bluetoothManagerImpl;
        mIBluetoothManagerImpl.registerStateChangeCallback(mBluetoothStateChangeCallback);
        mHandlerThread = new HandlerThread(className);
    }

    /***
     * swe检测出
     * 将启动线程独立出来，放在构造方法中，如果有子类实现该类，可能会导致子类还未完成构造，子线程已经启动
     */
    public void onEnter() {
        if(mHandlerThread != null) {
            mHandlerThread.start();
            mHandler = new MyHandler(mHandlerThread.getLooper());
        }

    }

    /**
     * 退出
     */
    public void onExit() {
        mIBluetoothManagerImpl.unregisterStateChangeCallback(mBluetoothStateChangeCallback);
        stopBindService();
        if (mHandlerThread != null) {
            mHandlerThread.quitSafely();
        }
        try {
            if (mHandlerThread != null) {
                mHandlerThread.join();
                mHandlerThread = null;
            }

            if (mHandler != null) {
                mHandler = null;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 检查服务
     *
     * @return true: 执行成功, false: 执行失败
     */
    public boolean checkBindService() {
        if (mIBinder == null) {
            return doBind();
        }
        return false;
    }

    /**
     * 停止服务
     */
    private void stopBindService() {
        handlerClean();
        doUnBind();
    }

    /**
     * 获取类名
     *
     * @return 类名
     */
    private String getClassName() {
        return getClass().getSimpleName() + " ";
    }

    /**
     * 执行绑定
     *
     * @return true: 执行成功, false: 执行失败
     */
    private boolean doBind() {
        if (!mIsBinding) {
            try {
                Intent intent = new Intent(mBindClassName);
                ComponentName comp = resolveSystemService(intent, mContext.getPackageManager(), 0);
                if (comp != null) {
                    intent.setComponent(comp);
                }

                intent.setPackage("android");
                mContext.bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
                mIsBinding = true;
            } catch (SecurityException se) {
                se.printStackTrace();
            } finally {
                handlerCheckConnect();
            }
            return true;
        }
        return false;
    }

    /**
     * 取消绑定
     */
    private void doUnBind() {
        if (mIBinder != null) {
            mIBinder = null;
            mContext.unbindService(mConnection);
        }
    }

    private void notifyConnect(boolean connected) {
        if (mCallBack != null) {
            mCallBack.onConnect(connected);
        }
    }

    private final ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mIsBinding = false;
            mIBinder = service;
            if (service != null) {
                handlerClean();
                try {
                    service.linkToDeath(mBinderDeath, 1);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
            BaseImpl.this.onServiceConnected(name, service);
            notifyConnect((service != null));
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mIsBinding = false;
            mIBinder = null;
            BaseImpl.this.onServiceDisconnected(name);
            notifyConnect(false);
        }
    };

    private ComponentName resolveSystemService(Intent intent, PackageManager pm, int flags) {
        List<ResolveInfo> results = pm.queryIntentServices(intent, flags);
        if (results == null) {
            return null;
        }
        ComponentName comp = null;
        for (int i = 0; i < results.size(); i++) {
            ResolveInfo ri = results.get(i);
            if ((ri.serviceInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                continue;
            }
            ComponentName foundComp = new ComponentName(ri.serviceInfo.applicationInfo.packageName,
                    ri.serviceInfo.name);
            if (comp != null) {
                throw new IllegalStateException("Multiple system services handle " + this
                        + ": " + comp + ", " + foundComp);
            }
            comp = foundComp;
        }
        return comp;
    }


    /**
     * 死亡代理
     */
    private final IBinder.DeathRecipient mBinderDeath = new IBinder.DeathRecipient() {
        @Override
        public void binderDied() {
            if (mIBinder != null) {
                mIBinder.unlinkToDeath(mBinderDeath, 1);
            }
            mIsBinding = false;
            stopBindService();
            notifyConnect(false);
        }
    };

    /**
     * 重新连接
     */
    private void handlerCheckConnect() {
        if ((mHandler != null) && (mIBinder == null)) {
            mHandler.removeMessages(WHAT_CHECK_BIND);
            Message msg = Message.obtain();
            msg.what = WHAT_CHECK_BIND;
            mHandler.sendMessageDelayed(msg, 10000);
        }
    }

    /**
     * 清除Handler
     */
    private void handlerClean() {
        mIsBinding = false;
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
    }

    /**
     * 检查连接
     */
    private final int WHAT_CHECK_BIND = 10;

    private class MyHandler extends Handler {

        public MyHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            mIsBinding = false;
            if (mIBinder == null) {
                notifyConnect(false);
            }
        }
    }

    /**
     * 获取连接的设备列表
     *
     * @return 设备列表
     */
    public List<BluetoothDevice> getConnectedDevices() {
        checkBindService();
        List<BluetoothDevice> connectedList = new ArrayList<>();

        if (getService() == null) {
            LogUtils.e(TAG, "getConnectedDevices getService() is null");
            return connectedList;
        }
        connectedList = null;
        try {
            if (getService() instanceof IBluetoothA2dpSink) {
                connectedList = ((IBluetoothA2dpSink) getService()).getConnectedDevices(InvokeUtil.getAttributionSource());
            } else if (getService() instanceof IBluetoothAvrcpController) {
                connectedList = ((IBluetoothAvrcpController) getService()).getConnectedDevices(InvokeUtil.getAttributionSource());
            } else if (getService() instanceof IBluetoothHeadsetClient) {
                connectedList = ((IBluetoothHeadsetClient) getService()).getConnectedDevices(InvokeUtil.getAttributionSource());
            } else if (getService() instanceof IBluetoothPbapClient) {
                connectedList = ((IBluetoothPbapClient) getService()).getConnectedDevices(InvokeUtil.getAttributionSource());
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        if (connectedList == null) {
            connectedList = new ArrayList<>();
        }
        return connectedList;
    }

    public int getConnectionState(BluetoothDevice device) {
        int state = BluetoothProfile.STATE_DISCONNECTED;
        checkBindService();
        if (device == null) {
            LogUtils.e(TAG, "isConnected device is null");
            return state;
        }
        if (getService() == null) {
            LogUtils.e(TAG, "isConnected getService() is null");
            return state;
        }

        try {
            if (getService() instanceof IBluetoothA2dpSink) {
                state = ((IBluetoothA2dpSink) getService()).getConnectionState(device, InvokeUtil.getAttributionSource());
            } else if (getService() instanceof IBluetoothAvrcpController) {
                List<BluetoothDevice> list = ((IBluetoothAvrcpController) getService()).getConnectedDevices(InvokeUtil.getAttributionSource());
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).equals(device)) {
                        state = BluetoothProfile.STATE_CONNECTED;
                        break;
                    }
                }
            } else if (getService() instanceof IBluetoothHeadsetClient) {
                state = ((IBluetoothHeadsetClient) getService()).getConnectionState(device, InvokeUtil.getAttributionSource());
            } else if (getService() instanceof IBluetoothPbapClient) {
                state = ((IBluetoothPbapClient) getService()).getConnectionState(device, InvokeUtil.getAttributionSource());
            } else if (getService() instanceof IBluetoothHidHost) {
                state = ((IBluetoothHidHost) getService()).getConnectionState(device, InvokeUtil.getAttributionSource());
            } else if (getService() instanceof IBluetoothA2dp) {
                state = ((IBluetoothA2dp) getService()).getConnectionState(device);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        return state;
    }

    /**
     * 指定设备是否连接
     *
     * @param device 蓝牙设备
     * @return true: 连接, false: 没有连接
     */
    public boolean isConnected(BluetoothDevice device) {
        int state = getConnectionState(device);
        return state == BluetoothProfile.STATE_CONNECTED;
    }

    /**
     * 连接指定的蓝牙设备
     *
     * @param device 蓝牙设备
     * @return true: 执行成功, false: 执行失败
     */
    public boolean connect(BluetoothDevice device) {
        checkBindService();
        if (device == null) {
            LogUtils.e(TAG, "connect device is null");
            return false;
        }
        if (getService() == null) {
            LogUtils.e(TAG, "connect getService() is null");
            return false;
        }
        if (isConnected(device)) {
            return true;
        }
        boolean result = false;
        try {
            if (getService() instanceof IBluetoothA2dpSink) {
                result = ((IBluetoothA2dpSink) getService()).connect(device, InvokeUtil.getAttributionSource());
            } else if (getService() instanceof IBluetoothHeadsetClient) {
                result = ((IBluetoothHeadsetClient) getService()).connect(device, InvokeUtil.getAttributionSource());
            } else if (getService() instanceof IBluetoothPbapClient) {
                result = ((IBluetoothPbapClient) getService()).connect(device, InvokeUtil.getAttributionSource());
            } else if (getService() instanceof IBluetoothHidHost) {
                result = ((IBluetoothHidHost) getService()).connect(device, InvokeUtil.getAttributionSource());
            } else if (getService() instanceof IBluetoothA2dp) {
                result = ((IBluetoothA2dp) getService()).connect(device);
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
        checkBindService();
        if (device == null) {
            LogUtils.e(TAG, "disconnect device is null");
            return false;
        }
        if (getService() == null) {
            LogUtils.e(TAG, "disconnect getService() is null");
            return false;
        }
        setPriority(device, PRIORITY_OFF);
        if (!isConnected(device)) {
            return true;
        }
        boolean result = false;
        try {
            if (getService() instanceof IBluetoothA2dpSink) {
                result = ((IBluetoothA2dpSink) getService()).disconnect(device, InvokeUtil.getAttributionSource());
            } else if (getService() instanceof IBluetoothAvrcpController) {

            } else if (getService() instanceof IBluetoothHeadsetClient) {
                result = ((IBluetoothHeadsetClient) getService()).disconnect(device, InvokeUtil.getAttributionSource());
            } else if (getService() instanceof IBluetoothPbapClient) {
                result = ((IBluetoothPbapClient) getService()).disconnect(device, InvokeUtil.getAttributionSource());
            } else if (getService() instanceof IBluetoothHidHost) {
                result = ((IBluetoothHidHost) getService()).disconnect(device, InvokeUtil.getAttributionSource());
            } else if (getService() instanceof IBluetoothA2dp) {
                result = ((IBluetoothA2dp) getService()).disconnect(device);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 设置蓝牙设备的优先级
     *
     * @param device      蓝牙设备
     * @param newPriority 新的优先级
     * @return true: 执行成功, false: 执行失败
     */
    private boolean setPriority(BluetoothDevice device, int newPriority) {
        checkBindService();
        if (device == null) {
            LogUtils.e(TAG, "setPriority device is null");
            return false;
        }
        if (getService() == null) {
            LogUtils.e(TAG, "setPriority getService() is null");
            return false;
        }
        int currPriority = getPriority(device);
        if (currPriority == newPriority) {
            return true;
        }

        boolean result = false;
//        try {
//            if (getService() instanceof IBluetoothA2dpSink) {
//                result = ((IBluetoothA2dpSink) getService()).setPriority(device, newPriority);
//            } else if (getService() instanceof IBluetoothAvrcpController) {
//
//            } else if (getService() instanceof IBluetoothHeadsetClient) {
//                result = ((IBluetoothHeadsetClient) getService()).setPriority(device, newPriority);
//            } else if (getService() instanceof IBluetoothPbapClient) {
//                result = ((IBluetoothPbapClient) getService()).setPriority(device, newPriority);
//            }
//        } catch (RemoteException e) {
//            e.printStackTrace();
//        }
        return result;
    }

    /**
     * 设置设备的优先级为自动连接
     *
     * @param device 蓝牙设备
     * @return true: 执行成功, false: 执行失败
     */
    public boolean setPriorityAutoConnect(BluetoothDevice device) {
        checkBindService();
        if (device == null) {
            LogUtils.e(TAG, "setPriorityAutoConnect device is null");
            return false;
        }
        if (getService() == null) {
            LogUtils.e(TAG, "setPriorityAutoConnect getService() is null");
            return false;
        }
        return setPriority(device, PRIORITY_AUTO_CONNECT);
    }

    /**
     * 设置设备的优先级为可被远程设备连接
     *
     * @param device 蓝牙设备
     * @return true: 执行成功, false: 执行失败
     */
    public boolean setPriorityRemoteConnect(BluetoothDevice device) {
        checkBindService();
        if (device == null) {
            LogUtils.e(TAG, "setPriorityRemoteConnect device is null");
            return false;
        }
        if (getService() == null) {
            LogUtils.e(TAG, "setPriorityRemoteConnect getService() is null");
            return false;
        }
        return setPriority(device, PRIORITY_ON);
    }

    /**
     * 获取蓝牙设备的优先级
     *
     * @param device 蓝牙设备
     * @return 优先级
     */
    private int getPriority(BluetoothDevice device) {
        checkBindService();
        if (device == null) {
            LogUtils.e(TAG, "getPriority getService() is null");
            return -1;
        }
        if (getService() == null) {
            LogUtils.e(TAG, "getPriority getService() is null");
            return -1;
        }
        int currPriority = -1;
        /*try {
            if (getService() instanceof IBluetoothA2dpSink) {
                currPriority = ((IBluetoothA2dpSink) getService()).getPriority(device);
            } else if (getService() instanceof IBluetoothAvrcpController) {

            } else if (getService() instanceof IBluetoothHeadsetClient) {
                currPriority = ((IBluetoothHeadsetClient) getService()).getPriority(device);
            } else if (getService() instanceof IBluetoothPbapClient) {
                currPriority = ((IBluetoothPbapClient) getService()).getPriority(device);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }*/
        return currPriority;
    }

    private final IBluetoothStateChangeCallback mBluetoothStateChangeCallback = new IBluetoothStateChangeCallback.Stub() {

        @Override
        public void onBluetoothStateChange(boolean on) {
            BaseImpl.this.onBluetoothStateChange(on);
            if (!on) {
                stopBindService();
            } else {
                checkBindService();
            }
        }
    };
}
