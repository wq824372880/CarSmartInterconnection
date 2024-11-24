package com.zeekrlife.connect.core.manager.wifi;

import static com.zeekrlife.commonlib.datastruct.connectivity.decode.WlanMode.accessPointOn;
import static com.zeekrlife.commonlib.datastruct.connectivity.decode.WlanMode.noWLAN;
import static com.zeekrlife.commonlib.datastruct.connectivity.decode.WlanMode.staOn;

import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;

import com.huawei.managementsdk.common.LogUtils;
import com.zeekrlife.common.util.threadtransform.ThreadPoolUtil;
import com.zeekrlife.commonlib.datastruct.connectivity.decode.FiveGMode;
import com.zeekrlife.commonlib.datastruct.connectivity.decode.FiveGModeReq_Response;
import com.zeekrlife.commonlib.datastruct.connectivity.decode.SecurityType;
import com.zeekrlife.commonlib.datastruct.connectivity.decode.TcamInformation_Response;
import com.zeekrlife.commonlib.datastruct.connectivity.decode.WlanApConnDevList_Notification;
import com.zeekrlife.commonlib.datastruct.connectivity.decode.WlanApConnDevList_Response;
import com.zeekrlife.commonlib.datastruct.connectivity.decode.WlanApSetting_Response;
import com.zeekrlife.commonlib.datastruct.connectivity.decode.WlanMode;
import com.zeekrlife.commonlib.datastruct.connectivity.decode.WlanModeReq_Response;
import com.zeekrlife.commonlib.datastruct.connectivity.decode.WlanStaConnect_Notification;
import com.zeekrlife.commonlib.datastruct.connectivity.decode.WlanStaForgetRememberedNet_Notification;
import com.zeekrlife.commonlib.datastruct.connectivity.decode.WlanStaRememberedNetwork_Common;
import com.zeekrlife.commonlib.datastruct.connectivity.decode.WlanStaScan_Response;
import com.zeekrlife.commonlib.datastruct.connectivity.decode.WlanStaStatus_Common;
import com.zeekrlife.commonlib.datastruct.connectivity.encode.SetFiveGMode_Request;
import com.zeekrlife.commonlib.datastruct.connectivity.encode.WlanApSetting_Request;
import com.zeekrlife.commonlib.datastruct.connectivity.encode.WlanMode_Request;
import com.zeekrlife.commonlib.datastruct.connectivity.encode.WlanStaConnect_Request;
import com.zeekrlife.commonlib.datastruct.connectivity.encode.WlanStaDisConn_Request;
import com.zeekrlife.commonlib.datastruct.connectivity.encode.WlanStaForgetRememberedNet_Request;
import com.zeekrlife.connect.core.app.App;
import com.zeekrlife.lib.ipcpapi.ClientDataCallback;
import com.zeekrlife.lib.ipcpapi.IpcpClient;
import com.zeekrlife.lib.ipcpapi.IpcpContact;
import com.zeekrlife.lib.ipcpapi.Session;
import com.zeekrlife.lib.ipcpapi.SessionManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright (c) 2021-2022 浙江极氪智能科技有限公司.All rights reserved.
 *
 * @description: 联网服务(wifi 、 热点 、 5G按钮)的管理，包括反馈以及主动调用
 * @author: Hongyun.Wu
 * @date: 2022/12/16 17:32:50
 * @version: V1.0
 */
public class WifiServiceManager {
    private static final String TAG = "IpcpServiceManager-IPCP-NET";
    private static volatile WifiServiceManager mWifiServiceManager;
    /**
     * 回调列表
     */
    private final List<Callback> mCallBackList = new ArrayList<>();

    int[] mFunckeys = {
            //5G板块
            //5G主动查询状态的反馈
            IpcpContact.IPCP_5GMODE_REQ,
            //5G开关状态反馈
            IpcpContact.IPCP_SET_5GMODE,

            //WIFI板块
            //验证 WLANModeReq 查询当前TCAM的状态
            IpcpContact.IPCP_WLANMODE_REQ,
            //WIFI/AP开关状态反馈
            IpcpContact.IPCP_WLAN_MODE,
            //WIFI连接状态反馈
            IpcpContact.IPCP_WLANSTA_CONNECT,
            IpcpContact.IPCP_WLANSTA_DISCON,
            //忘记网络反馈
            IpcpContact.IPCP_WLANSTA_FORGET_REMEMBERED_NETWORK,
            //连接历史
            IpcpContact.IPCP_WLANSTA_REMEMBERED_NETWORKS,
            //扫描到周围WiFi
            IpcpContact.IPCP_WLANSTA_SCAN,
            //查询当前连接WiFi状态
            IpcpContact.IPCP_WLANSTA_STATUS,

            //AP板块
            //设置AP热点反馈
            IpcpContact.IPCP_WLANAP_SETTING,
            //验证 WLANAPConnDevList，AP连接设备列表反馈
            IpcpContact.IPCP_WLANAP_CONNDEV_LIST,

            //TCAM信息反馈
            IpcpContact.IPCP_SYNC_TCAMINFO

    };

    private List<WlanStaRememberedNetwork_Common.RememberedApInfo> mRememberedNetwork;

    /**
     * NET_INVALIDATE 未检测到网络状态，此时UI 不允许点击
     * NET_PENDING  保留参数
     * NET_OFF (1),    --WLAN is in off mode    wifi/ap 关闭
     * AP_ON   (2),    -- AP On                 ap打开
     * WIFI_ON (3),    -- STA On                wifi打开
     * NET_REQ_PENDING  4 网络状态获取中
     * AP_ON_PENDING AP 5 打开中
     * WIFI_ON_PENDING  6 WIFI 打开中
     * NET_OFF_PENDING  7 网络关闭中（从开到关）
     */
    public static final int NET_INVALIDATE = -1;
    public static final int NET_PENDING = 0;
    public static final int NET_OFF = 1;
    public static final int AP_ON = 2;
    public static final int WIFI_ON = 3;
    public static final int NET_REQ_PENDING = 4;
    public static final int AP_ON_PENDING = 5;
    public static final int WIFI_ON_PENDING = 6;
    public static final int NET_OFF_PENDING = 7;
    //这个值，跟接口对应
    public static final int G5_PENDING_OFF = 20;
    public static final int G5_PENDING_ON = 10;
    public static final int G5_ON = 1;
    public static final int G5_OFF = 2;

    //8155
    public static final int FROM_FLAG = 5;

    //WLAN msg id
    private final int WLAN_MEG_WHAT = 100;
    //5g msg id
    private final int G5_MEG_WHAT = 101;

    //wifi 连接请求超时
    private final int WLAN_CON_MEG_WHAT = 102;

    //默认是关闭状态,1 全关，2 ap 3,wifi,对应TCOM 转态
    private int mCacheNetStatus = NET_INVALIDATE;
    //当前维护的同步给UI 的状态
    private int mCurrentNetStatus = NET_INVALIDATE;

    private int mG5NetStatus = G5_ON;

    private IpcpClient ipcpClient;

    private ServiceConnection mIpcpServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Session session = SessionManager.createSession(service);
            ipcpClient = session.createClient();
            LogUtils.w(TAG, "Ipcp ServiceConnection success, ipcpClient = " + ipcpClient);
            if (ipcpClient == null) {
                return;
            }
            //TODO 先Start在订阅
            ipcpClient.registerCallback(new ClientDataCallback() {
                @Override
                protected void onClientCallback(int funckey, byte[] data) {
                    switch (funckey) {
                        case IpcpContact.IPCP_5GMODE_REQ://5G主动查询状态的反馈
                        case IpcpContact.IPCP_SET_5GMODE://5G开关状态反馈
                            FiveGModeReq_Response fiveGModeReq_response = new FiveGModeReq_Response(data);
                            LogUtils.w(TAG, "OpSet5GMode_Response fiveGModeReq_response.get5GMode() = " + fiveGModeReq_response.get5GMode());
                            int result = G5_ON;
                            if (fiveGModeReq_response.get5GMode() == FiveGMode.fiveGModeOff) {
                                result = G5_OFF;
                            }
//                            CacheUtils.putInt(AppUtils.G5_NET_STATUS, result);
                            mG5NetStatus = result;
                            for (Callback callback : mCallBackList) {
                                callback.onG5Status(mG5NetStatus);
                            }
                            handler.removeMessages(G5_MEG_WHAT);
                            break;
                        //WIFI板块
                        case IpcpContact.IPCP_WLANMODE_REQ://验证 WLANModeReq 查询当前TCAM的状态
                        case IpcpContact.IPCP_WLAN_MODE://WIFI/AP开关状态反馈
                            WlanModeReq_Response wlanModeReq_response = new WlanModeReq_Response(data);
                            WlanMode wlanMode = wlanModeReq_response.getWlanMode();
                            LogUtils.w(TAG, "onEventReceiveFromClient :: functionalControl = " + wlanMode);
                            switch (wlanMode) {
                                case accessPointOn:
                                    mCurrentNetStatus = mCacheNetStatus = AP_ON;
                                    break;
                                case staOn:
                                    mCurrentNetStatus = mCacheNetStatus = WIFI_ON;
                                    break;
                                case noWLAN:
                                    mCurrentNetStatus = mCacheNetStatus = NET_OFF;
                                    break;
                                case reserved:
                                default:
                                    mCurrentNetStatus = mCacheNetStatus = NET_INVALIDATE;
                                    break;
                            }
                            for (Callback callback : mCallBackList) {
                                callback.onNetStatus(mCurrentNetStatus);
                            }
                            handler.removeMessages(WLAN_MEG_WHAT);
                            break;
                        case IpcpContact.IPCP_WLANSTA_CONNECT://WIFI连接状态反馈
                            WlanStaConnect_Notification wlanStaConnect_notification = new WlanStaConnect_Notification(data);
                            boolean connectedResult = wlanStaConnect_notification.getConnectedResut();
                            LogUtils.w(TAG, "onEventReceiveFromClient :: WLANSTAConnect = " + wlanStaConnect_notification);
                            for (Callback callback : mCallBackList) {
                                if (connectedResult) {
                                    //连接成功
                                    callback.onWifiConnect(100, wlanStaConnect_notification.getApSsid());
                                } else {
                                    //连接失败
                                    int failureReasonCode = 1/*wlanStaConnect_notification.getFailure_ReasonCode()*/;
                                    callback.onWifiConnect(failureReasonCode, wlanStaConnect_notification.getApSsid());
                                }
                            }
                            handler.removeMessages(WLAN_CON_MEG_WHAT);
                            break;
                        case IpcpContact.IPCP_WLANSTA_DISCON:

                            break;
                        case IpcpContact.IPCP_WLANSTA_FORGET_REMEMBERED_NETWORK://忘记网络反馈
                            WlanStaForgetRememberedNet_Notification wlanStaForgetRememberedNet_notification = new WlanStaForgetRememberedNet_Notification(data);
                            LogUtils.w(TAG, "onEventReceiveFromClient :: decode WLANSTAForgetRememberedNetwk = " + wlanStaForgetRememberedNet_notification);
                            boolean executionResult = wlanStaForgetRememberedNet_notification.getExecutionResult();
                            if (executionResult) {
                                for (Callback callback : mCallBackList) {
                                    callback.onWifiForget(0);
                                }
                            }
                            break;

                        case IpcpContact.IPCP_WLANSTA_REMEMBERED_NETWORKS://连接历史
                            WlanStaRememberedNetwork_Common wlanStaRememberedNetwork_common = new WlanStaRememberedNetwork_Common(data);
                            mRememberedNetwork = wlanStaRememberedNetwork_common.getRememberedApInfo();
                            LogUtils.w(TAG, "onEventReceiveFromClient :: OpWLANSTARememberedNetwks_Response = " + mRememberedNetwork);
                            for (Callback callback : mCallBackList) {
                                callback.onWifiConnectHistory(0, mRememberedNetwork);
                            }
                            break;
                        case IpcpContact.IPCP_WLANSTA_SCAN://扫描到周围WiFi
                            WlanStaScan_Response wlanStaScan_response = new WlanStaScan_Response(data);
                            try {
                                List<WlanStaScan_Response.CurrentApInfo> scanResults = wlanStaScan_response.getCurrentApInfo();
                                LogUtils.w(TAG, "onEventReceiveFromClient :: WLANSTAScan result = " + scanResults);
                                for (Callback callback : mCallBackList) {
                                    callback.onWifiScan(scanResults != null ? 0 : -1, scanResults);
                                }
                            } catch (NullPointerException e) {
                                LogUtils.e(TAG, "getScanResults NullPointerException:" + e);
                            }
                            break;
                        case IpcpContact.IPCP_WLANSTA_STATUS://查询当前连接WiFi状态
                            WlanStaStatus_Common wlanStaStatus_common = new WlanStaStatus_Common(data);
                            WlanStaStatus_Common.AvailableApInfo avaliableNetwork = wlanStaStatus_common.getAvailableInfos();
                            LogUtils.w(TAG, "onEventReceiveFromClient :: WLANSTAStatus = " + wlanStaStatus_common);
                            for (Callback callback : mCallBackList) {
                                if (avaliableNetwork != null && avaliableNetwork.connStatus) {
                                    callback.onWifiCurrentConnectStatus(0, avaliableNetwork);
                                } else {
                                    callback.onWifiCurrentConnectStatus(-1, null);
                                }
                            }
                            break;

                        //AP板块
                        case IpcpContact.IPCP_WLANAP_SETTING://设置AP热点反馈
                            WlanApSetting_Response wlanApSetting_response = new WlanApSetting_Response(data);
                            for (Callback callback : mCallBackList) {
                                callback.onWlanApSetting(0, wlanApSetting_response);
                            }
                            WLANAPConnDevList();
                            LogUtils.w(TAG, "onEventReceiveFromClient :: decode WLANAPSETTING  " + wlanApSetting_response);
                            break;
                        case IpcpContact.IPCP_WLANAP_CONNDEV_LIST://验证 WLANAPConnDevList，AP连接设备列表反馈
                            WlanApConnDevList_Response wlanApConnDevList_response = new WlanApConnDevList_Response(data);
                            List<WlanApConnDevList_Response.ConnDevInfo> devResponseList = wlanApConnDevList_response.getDevInfoList();
                            WlanApConnDevList_Notification wlanApConnDevList_notification = new WlanApConnDevList_Notification(data);
                            List<WlanApConnDevList_Notification.ConnDevInfo> devNotificationList = wlanApConnDevList_notification.getDevInfoList();
                            LogUtils.w(TAG, "onEventReceiveFromClient :: WLANAPConnDevList Response = " + devResponseList);
                            LogUtils.w(TAG, "onEventReceiveFromClient :: WLANAPConnDevList Notification = " + devNotificationList);
                            List<String> devNameList = new ArrayList<>();
                            for (Callback callback : mCallBackList) {
                                if (devResponseList.size() > 0) {
                                    for (WlanApConnDevList_Response.ConnDevInfo connDevInfo : devResponseList) {
                                        devNameList.add(connDevInfo.devName);
                                    }
                                } else if (devNotificationList.size() > 0) {
                                    for (WlanApConnDevList_Notification.ConnDevInfo connDevInfo : devNotificationList) {
                                        devNameList.add(connDevInfo.devName);
                                    }
                                }
                                callback.onApConnDevs(0, devNameList);
                            }
                            break;
                        case IpcpContact.IPCP_SYNC_TCAMINFO://TCAM信息反馈
                            TcamInformation_Response tcamInformation_response = new TcamInformation_Response(data);
                            LogUtils.w(TAG, "onEventReceiveFromClient :: TcamInformation Response = " + tcamInformation_response);
                            break;
                        default:
                            break;
                    }
                }
            });
            ipcpClient.start(IpcpContact.IPCP_CONNECTIVITY);
            ipcpClient.subscribeSome(mFunckeys);
            ipcpClient.subscribe(IpcpContact.IPCP_WLAN_MODE);

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            LogUtils.e(TAG, "Ipcp ServiceConnection error, ipcpClient = " + ipcpClient);
        }
    };

    public static synchronized WifiServiceManager getInstance() {
        if (mWifiServiceManager == null) {
            mWifiServiceManager = new WifiServiceManager();
        }
        return mWifiServiceManager;
    }

    private WifiServiceManager() {
        init(App.application.getApplicationContext());
    }

    /**
     * 初始化
     *
     * @param context
     */
    public void init(Context context) {
        SessionManager.connect(context, mIpcpServiceConnection);

    }

    /**
     * 取消初始化
     */
    public void deinit() {

    }

    /**
     * 注册回调
     *
     * @param callBack   回调
     * @param isRegister true: 注册, false：取消注册
     */
    public void setCallBack(Callback callBack, boolean isRegister) {
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

    public int getNetStatus() {
        LogUtils.w(TAG, "getNetStatus " + mCurrentNetStatus);
        return mCurrentNetStatus;
    }

    /**
     * 获取5G 状态
     *
     * @return 直接返回当前的 cache
     */
    public int getG5Status() {
        LogUtils.w(TAG, "getG5Status " + mG5NetStatus);
        /*if (mG5NetStatus == G5_PENDING_ON) {
            mG5NetStatus = CacheUtils.getInt(AppUtils.G5_NET_STATUS, G5_ON);
            LogUtils.w(TAG, "did not getG5Status,return record status. " + mG5NetStatus);
        }*/
        return mG5NetStatus;
    }

    public interface Callback {
        /**
         * wifi连接完成
         */
        void onWifiConnect(int status, String ssid);

        /**
         * wifi断开
         */
        void onWifiDisconnect(int status);

        /**
         * wifi扫描热点
         */
        void onWifiScan(int status, List<WlanStaScan_Response.CurrentApInfo> wifiInfos);

        /**
         * 当前连接WiFi
         */
        void onWifiCurrentConnectStatus(int status, WlanStaStatus_Common.AvailableApInfo avaliableNetwork);

        /**
         * WiFi连接历史记录
         */
        void onWifiConnectHistory(int status, List<WlanStaRememberedNetwork_Common.RememberedApInfo> rememberedApInfos);

        /**
         * 忘记网络
         */
        void onWifiForget(int status);

        //     * noWLAN          (1),    --WLAN is in off mode    wifi/ap 关闭
        //     * accessPointOn   (2),    -- AP On                 ap打开
        //     * staOn           (3),    -- STA On                wifi打开
        //     * NET_REQ_PENDING  4 网络状态获取中
        //     * AP_ON_PENDING AP 5 打开中
        //     * WIFI_ON_PENDING  6 WIFI 打开中
        //     * NET_OFF_PENDING  7 网络关闭中（从开到关）


        void onNetStatus(int status);

        /**
         * ap连接设备
         */
        void onApConnDevs(int status, List<String> conDevInfos);

        /**
         * 查询ap默认名称密码
         */
        void onWlanApSetting(int status, WlanApSetting_Response response);

        /**
         * 5G 开关状态
         */
        void onG5Status(int g5Status);
    }


    private final Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg != null) {
                switch (msg.what) {
                    case WLAN_MEG_WHAT:
                        //长时间未收到，网络状态回调，重置开关状态为上次状态
                        LogUtils.w(TAG, "oops ,delay 15000ms re-ReportOldStatus " + mCacheNetStatus);
                        mCurrentNetStatus = mCacheNetStatus;
                        handler.removeMessages(WLAN_MEG_WHAT);
                        for (WifiServiceManager.Callback callback : mCallBackList) {
                            callback.onNetStatus(mCacheNetStatus);
                        }
                        if (mCurrentNetStatus == NET_INVALIDATE) {
                            //如果当前是 pending 状态,再次轮询，目的是为了解决 TCAM 异常或者重启过程中
                            //可能拿不到响应的问题
                            LogUtils.w(TAG, "oops ,WLANModeReq again!!");
                            if (ipcpClient != null) {
                                ipcpClient.requestNoArgs(IpcpContact.IPCP_WLANMODE_REQ);
                            }
                            handler.sendEmptyMessageDelayed(WLAN_MEG_WHAT, 15000);
                        }
                        break;
                    case G5_MEG_WHAT:
                        LogUtils.w(TAG, "oops ,delay 3000ms re-Request 5G status = " + mG5NetStatus);
                        for (WifiServiceManager.Callback callback : mCallBackList) {
//                            mCallback.onG5Status(CacheUtils.getInt(AppUtils.G5_NET_STATUS, G5_ON));
                            callback.onG5Status(mG5NetStatus);
                        }
                        break;
                    case WLAN_CON_MEG_WHAT:
                        LogUtils.w(TAG, "oops ,delay 10000ms re-con wlan");
                        for (WifiServiceManager.Callback callback : mCallBackList) {
                            callback.onWifiConnect(4, (String) msg.obj);
                        }
                        break;
                    default:
                        break;
                }
            }
        }
    };


    /**
     * 设置WLAN状态
     * noWLAN          (1),    --WLAN is in off mode
     * accessPointOn   (2),    -- AP On     ap打开
     * staOn           (3),    -- STA On    wifi打开
     * reserved        (4)     --Reserved
     *
     * @param type
     */
    //TODO 设置WLAN
    public void WLANMode(int type) {
        LogUtils.w(TAG, "WLANMode :" + type);
        if (ipcpClient == null) {
            return;
        }

        ThreadPoolUtil.runOnSubThread(() ->{
                LogUtils.w(TAG, "WLANMode params :" + type);
                for (Callback callback : mCallBackList) {
                    if (type == NET_OFF) {
                        mCurrentNetStatus = NET_OFF_PENDING;
                    } else if (type == AP_ON) {
                        mCurrentNetStatus = AP_ON_PENDING;
                    } else if (type == WIFI_ON) {
                        mCurrentNetStatus = WIFI_ON_PENDING;
                    }
                    callback.onNetStatus(mCurrentNetStatus);
                }
                LogUtils.w(TAG, "WLANMode DO OPEN :" + type);
                WlanMode_Request wlanMode_request = new WlanMode_Request();
                switch (type) {
                    case NET_OFF:
                        wlanMode_request.setWlanMode(noWLAN);
                        break;
                    case AP_ON:
                        wlanMode_request.setWlanMode(accessPointOn);
                        break;
                    case WIFI_ON:
                        wlanMode_request.setWlanMode(staOn);
                        break;
                }
                ipcpClient.requesstNoReturn(IpcpContact.IPCP_WLAN_MODE, wlanMode_request.encode());
                //重新获取 wifi/ap 连接状态
                handler.removeMessages(WLAN_MEG_WHAT);
                handler.sendEmptyMessageDelayed(WLAN_MEG_WHAT, 15000);
        });
    }

    /**
     * 设置 5G 状态
     *
     * @param g5Type G5_ON/G5_OFF
     */
    public void setG5Mode(final int g5Type) {
        LogUtils.w(TAG, "SetG5Mode :" + g5Type);
        if (ipcpClient == null) {
            return;
        }
        ThreadPoolUtil.runOnSubThread(() ->{
                for (Callback callback : mCallBackList) {
                    if (G5_ON == g5Type) {
                        callback.onG5Status(G5_PENDING_ON);
                    } else {
                        callback.onG5Status(G5_PENDING_OFF);
                    }
                }
                try {
                    SetFiveGMode_Request setFiveGMode_request = new SetFiveGMode_Request();
                    setFiveGMode_request.setFiveMode(g5Type == G5_ON ? FiveGMode.fiveGModeOn : FiveGMode.fiveGModeOff);
                    ipcpClient.request(IpcpContact.IPCP_SET_5GMODE, setFiveGMode_request.encode());
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                LogUtils.w(TAG, "SetFiveGMode :" + g5Type);
                handler.removeMessages(G5_MEG_WHAT);
                handler.sendEmptyMessageDelayed(G5_MEG_WHAT, 3000);
        });
    }

    /**
     * 设置AP热点名称，密码
     *
     * @param ssid
     * @param password
     * @param frequencyBand INTEGER(0..14) OPTIONAL --Static Channel setting, only take effect in case of 2.4GHz
     * @param channel       frequencyChannel    INTEGER(0..14) OPTIONAL --Static Channel setting, only take effect in case of 2.4GHz
     */
    public void WLANAPSsid(String ssid) {
        if (ipcpClient == null) {
            return;
        }
        ThreadPoolUtil.runOnSubThread(() ->{
                LogUtils.w(TAG, "WLANAPSETTING  ssid:" + ssid);
                WlanApSetting_Request wlanApSetting_request = new WlanApSetting_Request();
                wlanApSetting_request.setApSSid(ssid);
                ipcpClient.request(IpcpContact.IPCP_WLANAP_SETTING, wlanApSetting_request.encode());
        });
    }

    public void WLANAPPassword(String password) {
        if (ipcpClient == null) {
            return;
        }
        ThreadPoolUtil.runOnSubThread(() ->{
                LogUtils.w(TAG, "WLANAPSETTING  password:" + password);
                WlanApSetting_Request wlanApSetting_request = new WlanApSetting_Request();
                wlanApSetting_request.setApPssword(password);
                ipcpClient.request(IpcpContact.IPCP_WLANAP_SETTING, wlanApSetting_request.encode());
        });
    }

    public void WLANAPFrequencyBand(int frequencyBand) {
        if (ipcpClient == null) {
            return;
        }
        ThreadPoolUtil.runOnSubThread(() ->{
                LogUtils.w(TAG, "WLANAPSETTING  frequencyBand:" + frequencyBand);
                WlanApSetting_Request wlanApSetting_request = new WlanApSetting_Request();
                wlanApSetting_request.setFreqBand(frequencyBand);
                ipcpClient.request(IpcpContact.IPCP_WLANAP_SETTING, wlanApSetting_request.encode());
        });
    }

    /**
     * 获取ap默认名称和密码    主动查询Ap的配置，比如名称密码等等
     */
    public void queryApSettings() {
        if (ipcpClient == null) {
            return;
        }
        ThreadPoolUtil.runOnSubThread(() -> {
            LogUtils.w(TAG, "queryApSettings");
            ipcpClient.requestNoArgs(IpcpContact.IPCP_WLANAP_SETTING);
        });
    }

    /**
     * 请求查询AP 模式下连接的设备列表
     */
    public void WLANAPConnDevList() {
        if (ipcpClient == null) {
            return;
        }
        ThreadPoolUtil.runOnSubThread(() -> {
            LogUtils.w(TAG, "WLANAPConnDevList()");
            ipcpClient.requestNoArgs(IpcpContact.IPCP_WLANAP_CONNDEV_LIST);
        });
    }

    /**
     * STA 模式下，连接扫描到的指定的wifi
     *
     * @param password 密码
     * @param ssid
     * @param secType  表示加密类型
     */
    public void WLANSTAConnect(String ssid, String password, SecurityType secType) {
        if (ipcpClient == null) {
            return;
        }
        ThreadPoolUtil.runOnSubThread(() ->{
                LogUtils.w(TAG, "WLANSTAConnect ssid=>" + ssid + " password:" + password + " sec_type:" + secType);
                WlanStaConnect_Request wlanStaConnect_request = new WlanStaConnect_Request();
                wlanStaConnect_request.setApSSid(ssid);
                if (!TextUtils.isEmpty(password)) {
                    wlanStaConnect_request.setApPassword(password);
                }
                wlanStaConnect_request.setSecurityType(secType);
                ipcpClient.requesstNoReturn(IpcpContact.IPCP_WLANSTA_CONNECT, wlanStaConnect_request.encode());

                handler.removeMessages(WLAN_CON_MEG_WHAT);
                Message msg = Message.obtain();
                msg.what = WLAN_CON_MEG_WHAT;
                msg.obj = ssid;
                handler.sendMessageDelayed(msg, 10000);
        });
    }

    /**
     * 主动查询当前连接的wifi的状态信息
     */
    public void WLANSTAStatus() {
        if (ipcpClient == null) {
            return;
        }
        ThreadPoolUtil.runOnSubThread(() ->{
                LogUtils.w(TAG, "WLANSTAStatus()");
                ipcpClient.requestNoArgs(IpcpContact.IPCP_WLANSTA_STATUS);
        });
    }

    /**
     * 查询WiFi的连接历史
     */
    public void WLANSTARememberedNetwks() {
        if (ipcpClient == null) {
            return;
        }
        ThreadPoolUtil.runOnSubThread(() ->{
                LogUtils.w(TAG, "WLANSTARememberedNetwks()");
                ipcpClient.requestNoArgs(IpcpContact.IPCP_WLANSTA_REMEMBERED_NETWORKS);
        });
    }

    /**
     * STA 模式下删除 保存或者记住的wifi
     */
    public void WLANSTAForgetRememberedNetwk(String ssid, SecurityType secType) {
        if (ipcpClient == null) {
            return;
        }
        ThreadPoolUtil.runOnSubThread(() ->{
                LogUtils.w(TAG, "forget network ssid: " + ssid + " ,secType: " + secType);
                WlanStaForgetRememberedNet_Request wlanStaForgetRememberedNet_request = new WlanStaForgetRememberedNet_Request();
                wlanStaForgetRememberedNet_request.setApSSid(ssid);
                wlanStaForgetRememberedNet_request.setSecurityType(secType);
                ipcpClient.request(IpcpContact.IPCP_WLANSTA_FORGET_REMEMBERED_NETWORK, wlanStaForgetRememberedNet_request.encode());
        });
    }

    /**
     * STA 模式下，请求扫描周围的 wifi
     */
    public void WLANSTAScan() {
        if (ipcpClient == null) {
            return;
        }
        ThreadPoolUtil.runOnSubThread(() ->{
                LogUtils.w(TAG, "WLANSTAScan()");

                ipcpClient.requestNoArgs(IpcpContact.IPCP_WLANSTA_SCAN);
        });
    }

    /**
     * 查询当前TCAM的状态
     * FunctionalControl:  WLAN operation Mode is AP or Station, or OFF
     */
    public void WLANModeReq() {
        LogUtils.w(TAG, "WLANModeReq()");
        if (ipcpClient == null) {
            return;
        }
        ipcpClient.requestNoArgs(IpcpContact.IPCP_WLANMODE_REQ);

        // 添加超时处理，没有返回或者网络不可用时，重新查询
        handler.removeMessages(WLAN_MEG_WHAT);
        handler.sendEmptyMessageDelayed(WLAN_MEG_WHAT, 15000);
    }

    /**
     * 查询当前TCAM的状态
     * FunctionalControl:  G5 or Station, or OFF
     */
    public void queryG5Status() {
        LogUtils.w(TAG, "queryG5Status()");
        if (ipcpClient == null) {
            return;
        }
        ipcpClient.requestNoArgs(IpcpContact.IPCP_5GMODE_REQ);
    }


    /**
     * STA 模式下断开 连接的wifi 参数是wifi
     *
     * @param ssid
     */
    public void WLANSTADisCon(final String ssid) {
        if (ipcpClient == null) {
            return;
        }
        WlanStaDisConn_Request wlanStaDisConn_request = new WlanStaDisConn_Request();
        wlanStaDisConn_request.setApSSid(ssid);
        ipcpClient.request(IpcpContact.IPCP_WLANSTA_DISCON, wlanStaDisConn_request.encode());
    }

    /**
     * 判断WiFi是否连接过
     */
    public WlanStaRememberedNetwork_Common.RememberedApInfo isExsits(String ssid, SecurityType type) {
        LogUtils.w(TAG, "isExsits " + ssid);
        if (mRememberedNetwork != null) {
            for (WlanStaRememberedNetwork_Common.RememberedApInfo apInfo : mRememberedNetwork) {
                if (TextUtils.equals(apInfo.availableApSSid, ssid) && apInfo.securityType == type) {
                    LogUtils.w(TAG, "apInfo " + apInfo);
                    return apInfo;
                }
            }
        }
        return null;
    }


    /**
     * 恢复出厂设备
     *
     * WiFi功能关闭
     * 车载热点关闭
     * 清除自定义用户名称密码
     */
    public void factoryReset() {
        LogUtils.w(TAG, "factoryReset()");
        if (ipcpClient == null) {
            return;
        }
        ThreadPoolUtil.runOnSubThread(() ->{
                LogUtils.w(TAG, "factoryReset WLANAPSETTING  ZEEKR-0001 = 12345678");
                WlanApSetting_Request wlanApSetting_request = new WlanApSetting_Request();
                wlanApSetting_request.setApSSid("ZEEKR-0001");
                wlanApSetting_request.setApPssword("12345678");
                ipcpClient.request(IpcpContact.IPCP_WLANAP_SETTING, wlanApSetting_request.encode());
        });

        WLANMode(WifiServiceManager.NET_OFF);
    }
}
