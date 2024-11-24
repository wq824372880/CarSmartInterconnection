package com.zeekrlife.connect.core.manager.bluetooth;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.ext.SubBluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.SystemClock;
import android.util.Log;


import androidx.core.app.ActivityCompat;

import com.huawei.managementsdk.common.LogUtils;
import com.zeekrlife.connect.core.ConnectServiceImpl;
import com.zeekrlife.connect.core.app.App;
import com.zeekrlife.connect.core.manager.bluetooth.constant.BluetoothConst;
import com.zeekrlife.connect.core.manager.bluetooth.constant.UnifyBluetoothDevice;
import com.zeekrlife.connect.core.manager.bluetooth.ctrl.BluetoothCtrl;
import com.zeekrlife.connect.core.manager.bluetooth.listener.IBluetoothCallBack;
import com.zeekrlife.connect.core.manager.bluetooth.utils.BtHandlerUtil;
import com.zeekrlife.connect.core.manager.bluetooth.utils.xLog;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class MyBluetoothManager {
    //暂时先弄个标志位判断
    public static boolean isQuickSettingClick;
    public final static String TAG = xLog.SUB_TAG_BLUETOOTH;

    private Context mContext;
    /**
     * BluetoothProxyManager
     */
    private BluetoothProxyManager mProxyManager;
    /**
     * SubBluetoothProxyManager
     */
    private SubBluetoothProxyManager mSubProxyManager;
    /**
     * BluetoothCtrl
     */
    private BluetoothCtrl mBluetoothCtrl;
    /**
     * 回调列表
     */
    private final List<IBluetoothCallBack> mCallBackList = new ArrayList<>();

    /**
     * 绑定中的设备
     */
    private BluetoothDevice mMainBondingDevice = null;

    private SubBluetoothDevice mSubBondingDevice = null;

    /**
     * 连接中的设备
     */
    private UnifyBluetoothDevice mConnectingDevice = null;

    public UnifyBluetoothDevice getConnectingDevice() {
        return mConnectingDevice;
    }

    /**
     * loading
     * 电话协议的设备
     */
    private BluetoothDevice mLoadingHFPDevice = null;

    public BluetoothDevice getLoadingHFPDevice() {
        return mLoadingHFPDevice;
    }

    /**
     * loading
     * 媒体协议的设备
     */
    private BluetoothDevice mLoadingA2DPDevice = null;

    public BluetoothDevice getLoadingA2DPDevice() {
        return mLoadingA2DPDevice;
    }

    /**
     * loading
     * 蓝牙手柄
     */
    private SubBluetoothDevice mLoadingHIDDevice = null;

    public SubBluetoothDevice getLoadingHIDDevice() {
        return mLoadingHIDDevice;
    }

    /**
     * 蓝牙耳机协议连接中的设备
     */
    private SubBluetoothDevice mLoadingHeadSetDevice = null;

    public SubBluetoothDevice getLoadingHeadSetDevice() {
        return mLoadingHeadSetDevice;
    }

    /**
     * 手动连接和配对执行过程中，其它操作都不可用；
     * 点击新的连接、配对、断开、忽略、搜索，会有toast提示：设备正忙，请稍后再试
     */

    public boolean isBusy() {
        LogUtils.w(TAG, "isBusy::mMainBondingDevice=" + mMainBondingDevice
                + ",mSubBondingDevice=" + mSubBondingDevice
                + ",mLoadingHFPDevice=" + mLoadingHFPDevice
                + ",mLoadingA2DPDevice=" + mLoadingA2DPDevice
                + ",mLoadingHIDDevice=" + mLoadingHIDDevice
                + ",mLoadingHeadSetDevice=" + mLoadingHeadSetDevice);

        boolean isMainBonding = mMainBondingDevice != null &&
                mProxyManager.isDeviceBonding(mMainBondingDevice);

        boolean isSubBonding = mSubBondingDevice != null &&
                mSubProxyManager.isDeviceBonding(mSubBondingDevice);

        boolean isLoadingHfp = mLoadingHFPDevice != null &&
                mProxyManager.isConnectingHFP(mLoadingHFPDevice);

        boolean isLoadingA2dp = mLoadingA2DPDevice != null &&
                mProxyManager.isConnectingA2dp(mLoadingA2DPDevice);

        boolean isLoadingHid = mLoadingHIDDevice != null &&
                mSubProxyManager.isConnectingHid(mLoadingHIDDevice);

        boolean isLoadingHeadset = mLoadingHeadSetDevice != null &&
                mSubProxyManager.isConnectingHeadset(mLoadingHeadSetDevice);

        LogUtils.w(TAG, "isBusy::isMainBonding=" + isMainBonding
                + ",isSubBonding=" + isSubBonding
                + ",isLoadingHfp=" + isLoadingHfp
                + ",isLoadingA2dp=" + isLoadingA2dp
                + ",isLoadingHid=" + isLoadingHid
                + ",isLoadingHeadset=" + isLoadingHeadset);

        return isMainBonding ||
                isSubBonding ||
                isLoadingHfp ||
                isLoadingA2dp ||
                isLoadingHid ||
                isLoadingHeadset;
    }

    private static volatile MyBluetoothManager inst;

    //开关的回调标记
    private static final int ECARX_BT_STATUS_ABLE_STATUS = 0;
    //配对中
    private static final int ECARX_BT_STATUS_BONDING = 1;
    //配对结束
    private static final int ECARX_BT_STATUS_BOND_END = 2;
    //连接中
    private static final int ECARX_BT_STATUS_CONNECTING = 3;
    //连接结束
    private static final int ECARX_BT_STATUS_CONNECT_END = 4;

    public static volatile int ECARX_BT_STATUS = ECARX_BT_STATUS_ABLE_STATUS;

    /**
     * 蓝牙设备打开的时间
     */
    private long mBtOpenedTime = 0;

    /**
     * 线程
     */
    private HandlerThread mHandlerThread;
    private Handler mHandler;

    /**
     * 打开蓝牙设备后是否需要执行两次扫描
     */
    private boolean mIsNeedReStartDiscovery = false;

    public static MyBluetoothManager getManager() {
        if (inst == null) {
            synchronized (MyBluetoothManager.class) {
                if (inst == null) {
                    inst = new MyBluetoothManager();
                }
            }
        }
        return inst;
    }

    private MyBluetoothManager() {
        init(App.application);
    }

    /**
     * 初始化
     *
     * @param ctx Context
     */
    private void init(Context ctx) {
        mContext = ctx;
        mHandlerThread = new HandlerThread(MyBluetoothManager.class.getSimpleName());
        mHandlerThread.start();
        mHandler = new Handler(mHandlerThread.getLooper());

        mProxyManager = new BluetoothProxyManager(ctx, mBluetoothCallBackImpl);
        mSubProxyManager = new SubBluetoothProxyManager(ctx);
        mBluetoothCtrl = new BluetoothCtrl(ctx, mBluetoothCallBackImpl);

        BtHandlerUtil.getInstance().setCallBack(mHandlerCallBack, true);
//        EventBus.getDefault().register(this);
        checkManager();
        LogUtils.e(TAG, "MyBluetoothManager init()");
    }

    /**
     * 退出
     */
    public void onExit() {
        BtHandlerUtil.getInstance().onDestroy();
        mProxyManager.onExit();
        mSubProxyManager.onExit();
        mBluetoothCtrl.onExit();
        handlerPriorityClear();
//        EventBus.getDefault().unregister(this);
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
        inst = null;
    }

    /**
     * 检查
     */
    public void checkManager() {
        if (!isBtOpened()) {
            return;
        }
        mProxyManager.checkService();
    }

    /**
     * 检查 Profile
     */
    public void checkProfile() {

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
                if (!mCallBackList.contains(callBack)) {
                    mCallBackList.add(callBack);
                }
            } else {
                mCallBackList.remove(callBack);
            }
        }
    }

    /**
     * 设置扫描模式
     *
     * @param on true: 可被扫描到, false: 不能被扫描到
     * @return true: 执行成功, false: 执行失败
     */
    public boolean setDiscoveryByRemote(boolean on) {
        if (!isBtOpened()) {
            return false;
        }
        return mProxyManager.setDiscoveryByRemote(on) ||
                mSubProxyManager.setDiscoveryByRemote(on);
    }

    /**
     * 获取扫描到的设备列表
     *
     * @return 蓝牙设备列表
     */
    public List<UnifyBluetoothDevice> getFoundDevices() {
        if (!isBtOpened()) {
            return new ArrayList<>();
        }
        List<UnifyBluetoothDevice> list = mBluetoothCtrl.getFoundDevices();
        // 屏蔽本机地址
        String mainAddress = mProxyManager.getBtAddress();
        String subAddress = mSubProxyManager.getBtAddress();
        Iterator<UnifyBluetoothDevice> iterator = list.iterator();
        while (iterator.hasNext()) {
            UnifyBluetoothDevice unifyBluetoothDevice = iterator.next();
            String address = unifyBluetoothDevice.getAddress();
            if (address.equals(mainAddress) || address.equals(subAddress)) {
                iterator.remove();
            }
        }
        xLog.w(TAG, "getFoundDevices resultList " + list.size());
        return new ArrayList<>(list);
    }

    /**
     * 获取绑定的蓝牙设备列表
     *
     * @return 蓝牙设备列表
     */
    public List<UnifyBluetoothDevice> getBondedDevices() {
        if (!isBtOpened()) {
            return new ArrayList<>();
        }
        List<UnifyBluetoothDevice> resultList = new ArrayList<>();

        List<BluetoothDevice> mainList = mProxyManager.getBondedDevices();
        Set<SubBluetoothDevice> subList = mSubProxyManager.getBondedDevices();

        for (BluetoothDevice main : mainList) {
            resultList.add(new UnifyBluetoothDevice(BluetoothConst.mainDeviceType, main, null));
        }
        if (subList != null && !subList.isEmpty()) {
            for (SubBluetoothDevice sub : subList) {
                resultList.add(new UnifyBluetoothDevice(BluetoothConst.subDeviceType, null, sub));
            }
        }
        return resultList;
    }

    /**
     * 指定的蓝牙设备是否已经绑定
     *
     * @param device 蓝牙设备
     * @return true: 已经绑定, false: 没有绑定
     */
    public boolean isDeviceBonded(UnifyBluetoothDevice device) {
        if (!isBtOpened()) {
            return false;
        }
        if (device.getType() == BluetoothConst.mainDeviceType) {
            return mProxyManager.isDeviceBonded(device.getMainDevice());
        }
        return mSubProxyManager.isDeviceBonded(device.getSubDevice());
    }

    /**
     * 指定的蓝牙设备正在绑定
     *
     * @param device 蓝牙设备
     * @return true: 已经绑定, false: 没有绑定
     */
    public boolean isDeviceBonding(UnifyBluetoothDevice device) {
        if (!isBtOpened()) {
            return false;
        }
        if (device.getType() == BluetoothConst.mainDeviceType) {
            return mProxyManager.isDeviceBonding(device.getMainDevice());
        }
        return mSubProxyManager.isDeviceBonding(device.getSubDevice());
    }

    /**
     * 绑定指定的蓝牙设备
     *
     * @param device 蓝牙设备
     * @return true: 执行成功, false: 执行失败
     */
    public boolean createBond(UnifyBluetoothDevice device) {
        if (!isBtOpened()) {
            return false;
        }
        if (isDiscovering()) {
            cancelDiscovery();
        }
        ECARX_BT_STATUS = ECARX_BT_STATUS_BONDING;
        //notifyManualConnect(device);

        if (device.getType() == BluetoothConst.mainDeviceType) {
            return device.getMainDevice().createBond();
        }
        return device.getSubDevice().createBond();
    }

    /**
     * 移除指定的绑定蓝牙设备
     *
     * @param device 蓝牙设备
     * @return true: 执行成功, false: 执行失败
     */
    public boolean removeBond(UnifyBluetoothDevice device) {
        if (!isBtOpened()) {
            return false;
        }
        //setPriorityRemoteConnect(device);

        if (device.getType() == BluetoothConst.mainDeviceType) {
            return mProxyManager.removeBond(device.getMainDevice());
        }
        return mSubProxyManager.removeBond(device.getSubDevice());
    }

    private void notifyManualConnect(BluetoothDevice dev) {
        Intent intent = new Intent("android.btsettings.action.START_MANUAL_CON");
        intent.putExtra(BluetoothDevice.EXTRA_DEVICE, dev);
        App.application.sendBroadcast(intent, android.Manifest.permission.BLUETOOTH);
        Log.d(TAG, "notifyManualConnect, start manual connect");
    }

    /**
     * 是否能连接A2dp
     * @return
     */
    public boolean canConnectA2dp() {
        long count = getConnectedPhoneDevices().stream().filter(this::isConnectedA2dp).count();
        return count < 1;
    }

    /**
     * 获取已连接hfp和a2dp数量
     *
     * @return 设备列表
     */
    public int[] getProfileConNum() {
        int hfp = 0;
        int a2dp = 0;
        List<BluetoothDevice> connectedPhoneDevices = getConnectedPhoneDevices();
        for (BluetoothDevice connectedPhoneDevice : connectedPhoneDevices) {
            if (isConnectedHFP(connectedPhoneDevice)) {
                hfp++;
            }
            if (isConnectedA2dp(connectedPhoneDevice)) {
                a2dp++;
            }
        }
        LogUtils.w(TAG, "connectedPhoneDevices = " + connectedPhoneDevices +
                ", hfp = " + hfp +
                ", a2dp = " + a2dp);
        return new int[]{hfp, a2dp};
    }


    /**
     * 获取连接的电话设备列表
     *
     * @return 设备列表
     */
    public List<BluetoothDevice> getConnectedPhoneDevices() {
        if (!isBtOpened()) {
            return new ArrayList<>();
        }
        return mProxyManager.getConnectedPhoneDevices();
    }

    /**
     * 获取连接的手柄设备列表
     *
     * @return 设备列表
     */
    public List<SubBluetoothDevice> getConnectedHidDevices() {
        if (!isBtOpened()) {
            return new ArrayList<>();
        }
        return mSubProxyManager.getConnectedHidDevices();
    }

    /**
     * 获取连接的耳机设备列表
     *
     * @return 设备列表
     */
    public List<SubBluetoothDevice> getConnectedHeadSetDevices() {
        if (!isBtOpened()) {
            return new ArrayList<>();
        }
        return mSubProxyManager.getConnectedHeadSetDevices();
    }

    public boolean isConnectingHFP(BluetoothDevice device) {
        if (!isBtOpened()) {
            return false;
        }
        return mProxyManager.isConnectingHFP(device);
    }

    public boolean isConnectingA2dp(BluetoothDevice device) {
        if (!isBtOpened()) {
            return false;
        }
        return mProxyManager.isConnectingA2dp(device);
    }

    public boolean isConnectingHid(SubBluetoothDevice device) {
        if (!isBtOpened()) {
            return false;
        }
        return mSubProxyManager.isConnectingHid(device);
    }

    public boolean isConnectingHeadset(SubBluetoothDevice device) {
        if (!isBtOpened()) {
            return false;
        }
        return mSubProxyManager.isConnectingHeadset(device);
    }

    /**
     * 是否连接HFP
     *
     * @param device 蓝牙设备
     * @return true: 已经连接, false: 没有连接
     */
    public boolean isConnectedHFP(BluetoothDevice device) {
        if (!isBtOpened()) {
            return false;
        }
        return mProxyManager.isConnectedHFP(device);
    }

    /**
     * 是否连接A2DP
     *
     * @param device 蓝牙设备
     * @return true: 已经连接, false: 没有连接
     */
    public boolean isConnectedA2dp(BluetoothDevice device) {
        if (!isBtOpened()) {
            return false;
        }
        return mProxyManager.isConnectedA2dp(device);
    }

    /**
     * 是否连接HID
     *
     * @param device 蓝牙设备
     * @return true: 已经连接, false: 没有连接
     */
    public boolean isConnectedHid(SubBluetoothDevice device) {
        if (!isBtOpened()) {
            return false;
        }
        return mSubProxyManager.isConnectedHid(device);
    }

    /**
     * 是否连接耳机
     *
     * @param device 蓝牙设备
     * @return true: 已经连接, false: 没有连接
     */
    public boolean isConnectedHeadset(SubBluetoothDevice device) {
        if (!isBtOpened()) {
            return false;
        }
        return mSubProxyManager.isConnectedHeadset(device);
    }

    /**
     * 连接指定的蓝牙设备
     *
     * @param device 蓝牙设备
     * @return true: 执行成功, false: 执行失败
     */
    public boolean connectHFP(BluetoothDevice device) {
        if (!isBtOpened()) {
            return false;
        }
        if (isDiscovering()) {
            cancelDiscovery();
        }
        setPriorityAutoConnect(device, BluetoothProfile.HEADSET);
        ECARX_BT_STATUS = ECARX_BT_STATUS_CONNECTING;
        notifyManualConnect(device);
        return mProxyManager.connectHFP(device);
    }

    /**
     * 连接指定的蓝牙设备
     *
     * @param device 蓝牙设备
     * @return true: 执行成功, false: 执行失败
     */
    public boolean connectA2dp(BluetoothDevice device) {
        if (!isBtOpened()) {
            return false;
        }
        if (isDiscovering()) {
            cancelDiscovery();
        }
        setPriorityAutoConnect(device, BluetoothProfile.A2DP);
        ECARX_BT_STATUS = ECARX_BT_STATUS_CONNECTING;
        notifyManualConnect(device);
        return mProxyManager.connectA2dp(device);
    }

    /**
     * 连接指定的蓝牙设备HID
     *
     * @param device 蓝牙设备
     * @return true: 执行成功, false: 执行失败
     */
    public boolean connectHid(SubBluetoothDevice device) {
        if (!isBtOpened()) {
            return false;
        }
        if (isDiscovering()) {
            cancelDiscovery();
        }
        setPriorityAutoConnect(device, BluetoothConst.PROFILE_HID);
        ECARX_BT_STATUS = ECARX_BT_STATUS_CONNECTING;
        //notifyManualConnect(device);
        return mSubProxyManager.connectHID(device);
    }


    /**
     * 连接蓝牙耳机设备
     *
     * @param device 蓝牙设备
     * @return true: 执行成功, false: 执行失败
     */
    public boolean connectHeadset(SubBluetoothDevice device) {
        if (!isBtOpened()) {
            return false;
        }
        if (isDiscovering()) {
            cancelDiscovery();
        }
        setPriorityAutoConnect(device, BluetoothConst.PROFILE_HEADSET);
        ECARX_BT_STATUS = ECARX_BT_STATUS_CONNECTING;
        //notifyManualConnect(device);
        return mSubProxyManager.connectHeadset(device);
    }

    /**
     * 断开指定的蓝牙设备连接
     *
     * @param device 蓝牙设备
     * @return true: 执行成功, false: 执行失败
     */
    public void disconnect(UnifyBluetoothDevice device) {
        if (!isBtOpened()) {
            return;
        }
        if (device.getType() == BluetoothConst.mainDeviceType) {
            mProxyManager.disconnect(device.getMainDevice());
        } else {
            mSubProxyManager.disconnectHeadset(device.getSubDevice());
            mSubProxyManager.disconnectHid(device.getSubDevice());
        }
        //notifyManualConnect(device);
    }

    /**
     * 断开主芯片蓝牙设备
     * 手机设备
     *
     * @param device 蓝牙设备
     * @return true: 执行成功, false: 执行失败
     */
    public void disconnect(BluetoothDevice device) {
        if (!isBtOpened()) {
            return;
        }
        mProxyManager.disconnect(device);
    }

    /**
     * 断开副芯片蓝牙设备
     * 手机设备
     *
     * @param device 蓝牙设备
     * @return true: 执行成功, false: 执行失败
     */
    public void disconnect(SubBluetoothDevice device) {
        if (!isBtOpened()) {
            return;
        }
        mSubProxyManager.disconnectHeadset(device);
        mSubProxyManager.disconnectHid(device);
    }

    /**
     * 断开HFP
     *
     * @param device 蓝牙设备
     * @return true: 执行成功, false: 执行失败
     */
    public void disconnectHFP(BluetoothDevice device) {
        notifyManualConnect(device);
        boolean result = mProxyManager.disconnectHFP(device);
    }

    /**
     * 断开A2DP
     *
     * @param device 蓝牙设备
     * @return true: 执行成功, false: 执行失败
     */
    public void disconnectA2ap(BluetoothDevice device) {
        notifyManualConnect(device);
        boolean result = mProxyManager.disconnectA2dp(device);
    }

    /**
     * 断开手柄
     *
     * @param device 蓝牙设备
     * @return true: 执行成功, false: 执行失败
     */
    public void disconnectHid(SubBluetoothDevice device) {
        //notifyManualConnect(device);
        boolean result = mSubProxyManager.disconnectHid(device);
    }

    /**
     * 断开蓝牙耳机设备连接
     *
     * @param device 蓝牙设备
     * @return true: 执行成功, false: 执行失败
     */
    public void disconnectHeadset(SubBluetoothDevice device) {
        //notifyManualConnect(device);
        boolean result = mSubProxyManager.disconnectHeadset(device);
    }

    /**
     * 设置设备的优先级为自动连接
     *
     * @param device 蓝牙设备
     * @return true: 执行成功, false: 执行失败
     */
    public boolean setPriorityAutoConnect(BluetoothDevice device, int priority) {
        if (device == null) {
            return false;
        }
        return mProxyManager.setPriorityAutoConnect(device, priority);
    }

    public boolean setPriorityAutoConnect(SubBluetoothDevice device, int priority) {
        if (device == null) {
            return false;
        }
        return mSubProxyManager.setPriorityAutoConnect(device, priority);
    }

    /**
     * 设置蓝牙名称
     *
     * @param name 名称
     * @return true: 执行成功, false: 执行失败
     */
    public boolean setBtName(String name) {
        if (!isBtOpened()) {
            return false;
        }
        return mProxyManager.setBtName(name) &&
                mSubProxyManager.setBtName(name);
    }

    /**
     * 获取蓝牙名称
     *
     * @return 蓝牙名称
     */
    public String getBtName() {
        return mProxyManager.getBtName();
    }

    /**
     * 获取蓝牙打开状态
     *
     * @return true: 打开, false: 关闭
     */
    public boolean isBtOpened() {
        return mProxyManager.isBtOpened() &&
                mSubProxyManager.isBtOpened();
    }

    /**
     * 打开/关闭蓝牙
     *
     * @param open true: 打开, false: 关闭
     */
    public boolean setBtOpen(boolean open) {
        xLog.w(TAG, "setBtOpen " + open);
        if (!open) {
            cancelDiscovery();
        }
        return mProxyManager.setBtOpen(open) &&
                mSubProxyManager.setBtOpen(open);
    }

    /**
     * 是否在扫描中
     *
     * @return true: 扫描中, false: 非扫描中
     */
    public boolean isDiscovering() {
        if (!isBtOpened()) {
            return false;
        }
        return (mProxyManager.isDiscovering() || mIsNeedReStartDiscovery);
    }

    /**
     * 手动点击扫描
     *
     * @return true: 执行成功, false: 执行失败
     */
    public boolean handStartDiscovery() {
        if (!isBtOpened()) {
            return false;
        }
        xLog.w(TAG, "handStartDiscovery");
        return mProxyManager.startDiscovery() &&
                mSubProxyManager.startDiscovery();
    }

    /**
     * 开始扫描
     *
     * @return true: 执行成功, false: 执行失败
     */
    private boolean realStartDiscovery() {
        if (!isBtOpened()) {
            return false;
        }
        xLog.w(TAG, "realStartDiscovery,current status is:" + ECARX_BT_STATUS);

        if (ECARX_BT_STATUS == ECARX_BT_STATUS_BONDING
                || ECARX_BT_STATUS == ECARX_BT_STATUS_CONNECTING) {
            xLog.e(TAG, "realStartDiscovery not allow,current status is:" + ECARX_BT_STATUS);
            return false;
        }
        mProxyManager.startDiscovery();
        mSubProxyManager.startDiscovery();
        return true;
    }

    /**
     * 取消扫描
     *
     * @return true: 执行成功, false: 执行失败
     */
    public boolean cancelDiscovery() {
        if (!isBtOpened()) {
            return false;
        }
        BtHandlerUtil.getInstance().stopHandlerStartDiscovery();
        return mProxyManager.cancelDiscovery();
    }


    private final IBluetoothCallBack mBluetoothCallBackImpl = new IBluetoothCallBack() {

        @Override
        public void onConnect(int type, boolean connected) {
            if (!connected) {
                checkManager();
            } else {
                for (int i = 0; i < mCallBackList.size(); i++) {
                    mCallBackList.get(i).onConnect(type, true);
                }
            }
        }

        @Override
        public void onBtOpenStateChange(int state) {
            ECARX_BT_STATUS = ECARX_BT_STATUS_ABLE_STATUS;
            mMainBondingDevice = null;
            mSubBondingDevice = null;
            mLoadingHFPDevice = null;
            mLoadingA2DPDevice = null;
            mLoadingHIDDevice = null;
            mLoadingHeadSetDevice = null;
            if (state == BluetoothAdapter.STATE_ON) {
                mBtOpenedTime = SystemClock.elapsedRealtime();
                checkManager();
                setDiscoveryByRemote(true);
            }

            if (state != BluetoothAdapter.STATE_ON) {
                mIsNeedReStartDiscovery = false;
            }

            if (state == BluetoothAdapter.STATE_OFF) {
                handlerPriorityClear();
            }
//            notifyEventBtOpenStateChange(state);
            for (int i = 0; i < mCallBackList.size(); i++) {
                mCallBackList.get(i).onBtOpenStateChange(state);
            }
        }

        @Override
        public void onBtConnectStateChange(int oldConnState, int newConnState, UnifyBluetoothDevice device) {
//            Log.e(TAG, "onBtConnectStateChange oldConnState:" + oldConnState + " newConnState:" + newConnState);
            if (newConnState == BluetoothAdapter.STATE_CONNECTING) {
                mConnectingDevice = device;
            } else {
                mConnectingDevice = null;
            }

            if (newConnState == BluetoothAdapter.STATE_CONNECTED || newConnState == BluetoothAdapter.STATE_CONNECTING) {
                checkManager();
            }

            if (newConnState == BluetoothAdapter.STATE_DISCONNECTED) {
                handlerCheckPriority(device);
            }

            if (newConnState == BluetoothAdapter.STATE_CONNECTING) {
                ECARX_BT_STATUS = ECARX_BT_STATUS_CONNECTING;
            } else {
                ECARX_BT_STATUS = ECARX_BT_STATUS_CONNECT_END;
            }
//            notifyEventBtConnectStateChange(oldConnState, newConnState, device);
            for (int i = 0; i < mCallBackList.size(); i++) {
                mCallBackList.get(i).onBtConnectStateChange(oldConnState, newConnState, device);
            }
            if(newConnState == BluetoothAdapter.STATE_CONNECTED){
                ConnectServiceImpl.getInstance().satisfyBluetoothRecommend(device.getMainDevice());
            }

        }

        @Override
        public void onProfileConnectStateChange(int oldStatus, int newStatus,
                                                int profileType, UnifyBluetoothDevice device) {
            if (newStatus == BluetoothAdapter.STATE_CONNECTED) {
                Log.e(TAG, "onProfileConnectStateChange profileType:" + profileType + "newConnState:" + newStatus + "deviceAddress" + device.getMainDevice().getAddress() + "deviceName:" + device.getName());
            }

            if (profileType == BluetoothProfile.HEADSET) {
                if (newStatus == BluetoothAdapter.STATE_CONNECTING || newStatus == BluetoothAdapter.STATE_DISCONNECTING) {
                    mLoadingHFPDevice = device.getMainDevice();
                } else {
                    mLoadingHFPDevice = null;
                }
            }
            if (profileType == BluetoothProfile.A2DP) {
                if (newStatus == BluetoothAdapter.STATE_CONNECTING || newStatus == BluetoothAdapter.STATE_DISCONNECTING) {
                    mLoadingA2DPDevice = device.getMainDevice();
                } else {
                    mLoadingA2DPDevice = null;
                }
            }
            if (profileType == BluetoothConst.PROFILE_HID) {
                if (newStatus == BluetoothAdapter.STATE_CONNECTING || newStatus == BluetoothAdapter.STATE_DISCONNECTING) {
                    mLoadingHIDDevice = device.getSubDevice();
                } else {
                    mLoadingHIDDevice = null;
                }
            }
            if (profileType == BluetoothConst.PROFILE_HEADSET) {
                if (newStatus == BluetoothAdapter.STATE_CONNECTING || newStatus == BluetoothAdapter.STATE_DISCONNECTING) {
                    mLoadingHeadSetDevice = device.getSubDevice();
                } else {
                    mLoadingHeadSetDevice = null;
                }
            }
            if (newStatus == BluetoothAdapter.STATE_CONNECTING || newStatus == BluetoothAdapter.STATE_DISCONNECTING) {
                ECARX_BT_STATUS = ECARX_BT_STATUS_CONNECTING;
            } else {
                ECARX_BT_STATUS = ECARX_BT_STATUS_CONNECT_END;
            }
//            notifyEventBtProfileConnectStateChange(oldStatus, newStatus, profileType, device);
            for (int i = 0; i < mCallBackList.size(); i++) {
                mCallBackList.get(i).onProfileConnectStateChange(oldStatus, newStatus, profileType, device);
            }

            if (profileType == BluetoothProfile.HEADSET) {

                if (newStatus == BluetoothAdapter.STATE_CONNECTED) {
                    ConnectServiceImpl.getInstance().satisfyBluetoothConnectCondition(device.getMainDevice());
                } else if (newStatus == BluetoothAdapter.STATE_DISCONNECTED && ConnectServiceImpl.getInstance().getCurrentEventType() == 101) {
//                    MyBluetoothManager.getManager().connectHFP(device.getMainDevice());
                }

            } else if (profileType == BluetoothProfile.A2DP) {

                if (ConnectServiceImpl.getInstance().getCurrentEventType() == 101) {

                    if (ConnectServiceImpl.getInstance().getConnectedDeviceAddress().contentEquals(device.getMainDevice().getAddress())) {
                        if(MyBluetoothManager.getManager().isConnectedA2dp(device.getMainDevice())){
                            MyBluetoothManager.getManager().disconnectA2ap(device.getMainDevice());
                        }
                    }

                }

            }


        }

        @Override
        public void onBtMainBondedStateChange(int state, BluetoothDevice device, int reason) {
            if (state == BluetoothDevice.BOND_BONDING) {
                mMainBondingDevice = device;
                //bonding 态，取消 discovery
                ECARX_BT_STATUS = ECARX_BT_STATUS_BONDING;
                cancelDiscovery();
            } else {
                mMainBondingDevice = null;
                ECARX_BT_STATUS = ECARX_BT_STATUS_BOND_END;
            }

//            notifyEventBtMainBondedStateChange(state, device, reason);
            for (int i = 0; i < mCallBackList.size(); i++) {
                mCallBackList.get(i).onBtMainBondedStateChange(state, device, reason);
            }
            LogUtils.e(TAG, ("MyBluetoothManager_HiCar onBtMainBondedStateChange state:" + state + "device:" + device));
        }

        @Override
        public void onBtSubBondedStateChange(int state, SubBluetoothDevice device, int reason) {
            if (state == BluetoothDevice.BOND_BONDING) {
                mSubBondingDevice = device;
                //bonding 态，取消 discovery
                ECARX_BT_STATUS = ECARX_BT_STATUS_BONDING;
                cancelDiscovery();
            } else {
                mSubBondingDevice = null;
                ECARX_BT_STATUS = ECARX_BT_STATUS_BOND_END;
            }

//            notifyEventBtSubBondedStateChange(state, device, reason);
            for (int i = 0; i < mCallBackList.size(); i++) {
                mCallBackList.get(i).onBtSubBondedStateChange(state, device, reason);
            }
//            if(state == BluetoothDevice.BOND_BONDED){
//                ConnectServiceImpl.getInstance().satisfyBluetoothRecommend(null);
//            }
        }

        @Override
        public void onBtDiscoveryStateChange(boolean started) {
            for (int i = 0; i < mCallBackList.size(); i++) {
                mCallBackList.get(i).onBtDiscoveryStateChange(started);
            }

            long between = (SystemClock.elapsedRealtime() - mBtOpenedTime);
            xLog.e(TAG,
                    "started=" + started + ", between=" + between + ", mIsNeedReStartDiscovery=" + mIsNeedReStartDiscovery);
            if (!started && mIsNeedReStartDiscovery) {
                int bondedDevicesSize = getBondedDevices().size();
                if (bondedDevicesSize == 0) {
                    mIsNeedReStartDiscovery = false;
                } else {
                    BtHandlerUtil.getInstance().handlerStartDiscovery(true);
                }
            }
            if (started) {
                BtHandlerUtil.getInstance().handlerStopDiscovery(30000);
            }
        }

        @Override
        public void onBtNewFondDevice(UnifyBluetoothDevice device) {
//            notifyEventBtNewFondDevice(device);
            for (int i = 0; i < mCallBackList.size(); i++) {
                mCallBackList.get(i).onBtNewFondDevice(device);
            }
        }

        @Override
        public void onBtRemoteNameChange(String newName, UnifyBluetoothDevice device) {
//            notifyEventBtRemoteNameChange(newName, device);
            for (int i = 0; i < mCallBackList.size(); i++) {
                mCallBackList.get(i).onBtRemoteNameChange(newName, device);
            }
        }

        @Override
        public void onLocalBtNameChange() {
//            notifyEventLocalNameChange();
            for (int i = 0; i < mCallBackList.size(); i++) {
                mCallBackList.get(i).onLocalBtNameChange();
            }
        }

        @Override
        public void onMainPairMatchRequest(String ssp, BluetoothDevice pairingDevice) {
//            notifyEventMainPairMatchRequest(ssp, pairingDevice);
            for (int i = 0; i < mCallBackList.size(); i++) {
                mCallBackList.get(i).onMainPairMatchRequest(ssp, pairingDevice);
            }
            LogUtils.e(TAG, "onMainPairMatchRequest ssp = " + ssp + ", pairingDevice = " + pairingDevice);
            if (ActivityCompat.checkSelfPermission(App.application, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
//            pairingDevice.setPairingConfirmation(true);
//            ThreadPoolUtil.runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    LogUtils.e(TAG, "onMainPairMatchRequest33  ssp= " + ssp + ", pairingDevice = " + pairingDevice);
//                    connecHFPt(pairingDevice);
//                    disconnectA2ap(pairingDevice);
//                }
//            },8000);
//            LogUtils.e(TAG, "onMainPairMatchRequest22  ssp= " + ssp + ", pairingDevice = " + pairingDevice);
        }

        @Override
        public void onSubPairMatchRequest(String ssp, SubBluetoothDevice pairingDevice) {
//            notifyEventSubPairMatchRequest(ssp, pairingDevice);
            for (int i = 0; i < mCallBackList.size(); i++) {
                mCallBackList.get(i).onSubPairMatchRequest(ssp, pairingDevice);
            }
        }
    };

    private final BtHandlerUtil.HandlerCallBack mHandlerCallBack = new BtHandlerUtil.HandlerCallBack() {

        @Override
        public void onTimeOut(int what) {
            if (what == BtHandlerUtil.WHAT_START_DISCOVERY) {
                realStartDiscovery();
            } else if (what == BtHandlerUtil.WHAT_STOP_DISCOVERY) {
                cancelDiscovery();
            } else if (what == BtHandlerUtil.WHAT_RE_START_DISCOVERY_TIME_OUT) {
                if (mIsNeedReStartDiscovery) {
                    mIsNeedReStartDiscovery = false;
//                    notifyEventBtDiscoveryStateChange(isDiscovering());
                    for (int i = 0; i < mCallBackList.size(); i++) {
                        mCallBackList.get(i).onBtDiscoveryStateChange(isDiscovering());
                    }
                }
            }
        }
    };

    private void handlerCheckPriority(UnifyBluetoothDevice device) {
        if (device == null) {
            xLog.e(TAG, "handlerCheckPriority device is null");
            return;
        }

        if (mHandler != null) {
            handlerPriorityClear();
        }
    }

    private void handlerPriorityClear() {
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
    }
}
