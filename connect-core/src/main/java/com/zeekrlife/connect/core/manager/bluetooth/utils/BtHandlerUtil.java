package com.zeekrlife.connect.core.manager.bluetooth.utils;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.MainThread;

import java.util.ArrayList;
import java.util.List;

/**
 * <pre>
 *     author : qiangwang
 *     e-mail : qiangwang@ecarx.com.cn
 *     time   : 2018/11/16
 *     desc   : Handler Util
 *     version: 1.0
 * </pre>
 */
public class BtHandlerUtil {
    private static final String TAG = "BtHandlerUtil";

    /**
     * EventBus 超时事件
     */
    public static class EventHandlerTimeOut {
        public int mWhat = -1;

        public EventHandlerTimeOut(int value) {
            mWhat = value;
        }

        public void setWhat(int value) {
            mWhat = value;
        }
    }

    /**
     * 线程
     */
    private HandlerThread mHandlerThread;
    /**
     * Handler
     */
    private Handler mHandler;

    private final List<HandlerCallBack> mCallBackList = new ArrayList<>();

    public interface HandlerCallBack {
        /**
         * 超时
         *
         * @param what 超时的类型
         */
        void onTimeOut(int what);
    }

    private static volatile BtHandlerUtil inst;

    public static BtHandlerUtil getInstance() {
        if (inst == null) {
            synchronized (BtHandlerUtil.class) {
                if (inst == null) {
                    inst = new BtHandlerUtil();
                }
            }
        }
        return inst;
    }

    private BtHandlerUtil() {
        xLog.d();
        this.mHandlerThread = new HandlerThread(BtHandlerUtil.class.getSimpleName());
        this.mHandlerThread.start();
        mHandler = new MyHandler(mHandlerThread.getLooper());
    }

    public void onDestroy() {
        mCallBackList.clear();
      //  mHandler.removeCallbacksAndMessages(null);

        if (mHandlerThread != null) {
            mHandlerThread.quitSafely();
        }
        try {
            if (mHandlerThread != null) {
                mHandlerThread.join();
                mHandlerThread = null;
            }

            if (mHandler != null) {
                mHandler.removeCallbacksAndMessages(null);
                mHandler = null;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void setCallBack(HandlerCallBack callBack, boolean isRegister) {
        if (callBack != null) {
            if (isRegister) {
                if (!mCallBackList.contains(callBack)) {
                    mCallBackList.add(callBack);
                }
            } else {
                mCallBackList.remove(callBack);
            }
        }
    }

    public static String getName(int id) {
        return xLog.getName(id, BtHandlerUtil.class);
    }

    @MainThread
    public void onEventHandlerTimeOut(EventHandlerTimeOut event) {
        notifyTimeOut(event.mWhat, false);
    }

    private void notifyTimeOut(int what, boolean useMain) {
        if (useMain) {
//            EventBus.getDefault().post(new EventHandlerTimeOut(what));
        } else {
            for (int i = 0; i < mCallBackList.size(); i++) {
                if (mCallBackList.get(i) == null) {
                    mCallBackList.remove(i);
                } else {
                    mCallBackList.get(i).onTimeOut(what);
                }
            }
        }
    }

    /**
     * 设置蓝牙名称
     */
    public void handlerSetBluetoothName() {
        if (mHandler != null) {
            mHandler.removeMessages(WHAT_SET_BLUETOOTH_NAME);
            Message msg = Message.obtain();
            msg.what = WHAT_SET_BLUETOOTH_NAME;
            mHandler.sendMessageDelayed(msg, 2000);
        }
    }

    /**
     * 更新蓝牙界面
     */
    public void handlerUpdateBluetoothView(boolean longTime) {
        if (mHandler != null) {
            mHandler.removeMessages(WHAT_UPDATE_BLUETOOTH_VIEW);
            Message msg = Message.obtain();
            msg.what = WHAT_UPDATE_BLUETOOTH_VIEW;
            if (longTime) {
                mHandler.sendMessageDelayed(msg, 10000);
            } else {
                mHandler.sendMessageDelayed(msg, 5000);
            }
        }
    }

    /**
     * 打开蓝牙超时
     */
    public void handlerSetBluetoothOpenTimeOut() {
        if (mHandler != null) {
            mHandler.removeMessages(WHAT_SET_BLUETOOTH_OPEN_TIME_OUT);
            Message msg = Message.obtain();
            msg.what = WHAT_SET_BLUETOOTH_OPEN_TIME_OUT;
            mHandler.sendMessageDelayed(msg, 10000);
        }
    }

    /**
     * 移除蓝牙打开关闭超时
     */
    public void removeBluetoothOpenTimeOut() {
        if (mHandler != null) {
            mHandler.removeMessages(WHAT_SET_BLUETOOTH_OPEN_TIME_OUT);
        }
    }

    /**
     * 开始扫描
     */
    public void handlerStartDiscovery(boolean longTime) {
        xLog.d(TAG, "handlerStartDiscovery");
        if (mHandler != null) {
            mHandler.removeMessages(WHAT_START_DISCOVERY);
            Message msg = Message.obtain();
            msg.what = WHAT_START_DISCOVERY;
            if (longTime) {
                mHandler.sendMessageDelayed(msg, 1000);
            } else {
                mHandler.sendMessageDelayed(msg, 300);
            }
        }
    }

    /**
     * 停止扫描
     *
     * @param delayTime 多少时间后停止扫描
     */
    public void handlerStopDiscovery(long delayTime) {
        xLog.d(TAG, "handlerStopDiscovery");
        if (mHandler != null) {
            mHandler.removeMessages(WHAT_STOP_DISCOVERY);
            Message msg = Message.obtain();
            msg.what = WHAT_STOP_DISCOVERY;
            mHandler.sendMessageDelayed(msg, delayTime);
        }
    }


    /**
     * 移除开始扫描
     */
    public void stopHandlerStartDiscovery() {
        if (mHandler != null) {
            mHandler.removeMessages(WHAT_START_DISCOVERY);
        }
    }

    /**
     * 重新扫描超时
     */
    public void handlerReStartDiscoveryTimeOut() {
        if (mHandler != null) {
            mHandler.removeMessages(WHAT_RE_START_DISCOVERY_TIME_OUT);
            Message msg = Message.obtain();
            msg.what = WHAT_RE_START_DISCOVERY_TIME_OUT;
            mHandler.sendMessageDelayed(msg, 50000);
        }
    }

    /**
     * 断开设备连接
     */
    public void handlerDisconnectDevice() {
        if (mHandler != null) {
            mHandler.removeMessages(WHAT_DISCONNECT_DEVICE);
            Message msg = Message.obtain();
            msg.what = WHAT_DISCONNECT_DEVICE;
            mHandler.sendMessageDelayed(msg, 500);
        }
    }

    /**
     * 设置蓝牙名称
     */
    public final static int WHAT_SET_BLUETOOTH_NAME = 10;
    /**
     * 更新蓝牙界面
     */
    public final static int WHAT_UPDATE_BLUETOOTH_VIEW = 11;

    /**
     * 打开蓝牙超时
     */
    public final static int WHAT_SET_BLUETOOTH_OPEN_TIME_OUT = 12;

    /**
     * 开始扫描
     */
    public final static int WHAT_START_DISCOVERY = 13;

    /**
     * 开始扫描
     */
    public final static int WHAT_STOP_DISCOVERY = 14;

    /**
     * 断开设备连接
     */
    public final static int WHAT_DISCONNECT_DEVICE = 15;

    /**
     * 重新扫描超时
     */
    public final static int WHAT_RE_START_DISCOVERY_TIME_OUT = 17;

    private class MyHandler extends Handler {
        public MyHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case WHAT_SET_BLUETOOTH_NAME:
                    notifyTimeOut(WHAT_SET_BLUETOOTH_NAME, true);
                    break;
                case WHAT_UPDATE_BLUETOOTH_VIEW:
                    notifyTimeOut(WHAT_UPDATE_BLUETOOTH_VIEW, true);
                    break;
                case WHAT_SET_BLUETOOTH_OPEN_TIME_OUT:
                    notifyTimeOut(WHAT_SET_BLUETOOTH_OPEN_TIME_OUT, true);
                    break;
                case WHAT_START_DISCOVERY:
                    notifyTimeOut(WHAT_START_DISCOVERY, false);
                    break;
                case WHAT_STOP_DISCOVERY:
                    notifyTimeOut(WHAT_STOP_DISCOVERY, false);
                    break;
                case WHAT_DISCONNECT_DEVICE:
                    notifyTimeOut(WHAT_DISCONNECT_DEVICE, false);
                    break;
                case WHAT_RE_START_DISCOVERY_TIME_OUT:
                    notifyTimeOut(WHAT_RE_START_DISCOVERY_TIME_OUT, false);
                    break;
                default:
                    break;
            }
        }
    }
}
