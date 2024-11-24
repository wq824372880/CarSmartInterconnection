package com.zeekrlife.connect.base.manager;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Surface;


import com.zeekrlife.aidl.IConnectService;
import com.zeekrlife.aidl.IHiCarAppInfo;
import com.zeekrlife.aidl.IHiCarListener;

import java.util.List;

public class ConnectServiceManager {
    private static final String TAG = "zzzConnectManager";

    private Context context;
    private ServiceConnection serviceConnection;
    private IConnectService connectService;
    private InitConnectServiceCallback initConnectServiceCallback;
    private UnRegisterHiCarListenerCallback unRegisterHiCarListenerCallback;
    public boolean isUnRegisterListener = false;



    public static ConnectServiceManager getInstance() {
        return InstanceHolder.instance;
    }

    private static class InstanceHolder {
        public static final ConnectServiceManager instance = new ConnectServiceManager();
    }

    private final IHiCarListener.Stub connectCallback = new IHiCarListener.Stub() {


        @Override
        public void onDeviceChange(String key, int event, int errorcode) throws RemoteException {

        }

        @Override
        public void onDeviceServiceChange(String serviceId, int event) throws RemoteException {

        }

        @Override
        public void onHiCarApplistChange(List<IHiCarAppInfo> list) throws RemoteException {

        }

        @Override
        public void onDataReceive(String key, int dataType, byte[] data) throws RemoteException {

        }

        @Override
        public void onPinCode(String code) throws RemoteException {

        }

        @Override
        public void onWifiAPState(boolean isOpen) throws RemoteException {

        }

        @Override
        public void onBinderDied() throws RemoteException {

        }

        @Override
        public void onShowStartPage() throws RemoteException {

        }
    };

    public class Connection implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.e(TAG, "connectService connected!");
            connectService = IConnectService.Stub.asInterface(service);

            if(initConnectServiceCallback != null){
                initConnectServiceCallback.onServiceConnected(true);
            }

            try {
                registerHiCarListener(connectCallback);
            } catch (Throwable throwable) {
                Log.e(TAG, "register connectService:" + Log.getStackTraceString(throwable));
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.e(TAG, "connectService disconnected!");

            if(initConnectServiceCallback != null){
                initConnectServiceCallback.onServiceConnected(false);
            }

            if(unRegisterHiCarListenerCallback != null){
                unRegisterHiCarListenerCallback.onUnRegisterHiCarListenerCallback(true);
            }

            try {
                unregisterHiCarListener(connectCallback);
                isUnRegisterListener = true;


            } catch (Throwable throwable) {
                Log.e(TAG, "unregister connectService:" + Log.getStackTraceString(throwable));
            }

            connectService = null;

            try {
                if(context != null){
                    Log.e(TAG, "connectService retry initConnectService!");
                    initConnectService(context);
                }
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public IHiCarListener getConnectCallback() throws RemoteException {
        return connectCallback;
    }



    public void initConnectService(Context context) throws RemoteException {
        if(ensureServiceAvailable()){
            return;
        }
        this.context = context.getApplicationContext();
        this.serviceConnection = new Connection();
        Intent intent = new Intent();
        intent.setPackage("com.zeekrlife.connect.core");
        intent.setAction("zeekrlife.intent.action.INTERCONNECT_SERVICE_START");
        boolean result = false;
        try {
            result = this.context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
        } catch (Throwable throwable) {
            Log.e(TAG, "bind connectService:" + Log.getStackTraceString(throwable));
        }
        if (result) {
            Log.e(TAG, "bind connectService success!");

        } else {
            Log.e(TAG, "bind connectService failure!");
        }
    }



    public void loadHiCarSDK() throws RemoteException {
        try {
            if(connectService!= null){
                connectService.loadHiCarSdk();
            }

        } catch (Throwable throwable) {
            Log.e("zzzloadHiCarSDK", "loadHiCarSDK" + Log.getStackTraceString(throwable));
        }
    }

    public void unLoadHiCarSdk() throws RemoteException {
        try {
            if(connectService!= null){
                connectService.unLoadHiCarSdk();
            }

        } catch (Throwable throwable) {
            Log.e(TAG, "unLoadHiCarSDK" + Log.getStackTraceString(throwable));
        }
    }

    public void openBlueTooth() throws RemoteException {
        try {
            if(connectService!= null){
                connectService.openBluetooh();
            }

        } catch (Throwable throwable) {
            Log.e(TAG, "openWifi" + Log.getStackTraceString(throwable));
        }
    }

    public boolean getBlueToothState() throws RemoteException {
        try {
            if(connectService!= null){
                return connectService.getBluetoothState();
            }

        } catch (Throwable throwable) {
            Log.e(TAG, "getBlueToothState" + Log.getStackTraceString(throwable));
        }
        return false;
    }

    public boolean getWifiState() throws RemoteException {
        try {
            if(connectService!= null){
                return connectService.getWifiState();
            }

        } catch (Throwable throwable) {
            Log.e(TAG, "getWifiState" + Log.getStackTraceString(throwable));
        }
        return false;
    }

    public void openWifi() throws RemoteException {
        try {
            if(connectService!= null){
                connectService.openWifi();
            }

        } catch (Throwable throwable) {
            Log.e(TAG, "openWifi" + Log.getStackTraceString(throwable));
        }
    }

    public void closeWifi() throws RemoteException {
        try {
            if(connectService!= null){
                connectService.closeWifi();
            }

        } catch (Throwable throwable) {
            Log.e(TAG, "closeWifi" + Log.getStackTraceString(throwable));
        }
    }

    public void modifyAPPassWord(String password) throws RemoteException {
        try {
            if(connectService!= null){
                connectService.modifyAPPassWord(password);
            }

        } catch (Throwable throwable) {
            Log.e(TAG, "modifyAPPassWord" + Log.getStackTraceString(throwable));
        }
    }

    public void handleStartAdv() throws RemoteException {
        try {
            if(connectService!= null){
                connectService.handleStartAdv();
            }
        } catch (Throwable throwable) {
            Log.e(TAG, "handleStartAdv" + Log.getStackTraceString(throwable));
        }
    }

    public void handleStartReconnect(String reConnectMac) throws RemoteException {
        try {
            if(connectService!= null){
                connectService.startReconnect(reConnectMac);
            }
        } catch (Throwable throwable) {
            Log.e(TAG, "handleStartReconnect" + Log.getStackTraceString(throwable));
        }
    }

    public void handleStoptAdv() throws RemoteException {
        try {
            if(connectService!= null){
                connectService.handleStoptAdv();
            }
        } catch (Throwable throwable) {
            Log.e(TAG, "handleStartAdv" + Log.getStackTraceString(throwable));
        }
    }
    public void startBluetoothRecommend() throws RemoteException {
        try {
            if(connectService!= null){
                connectService.startBluetoothRecommend();
            }
        } catch (Throwable throwable) {
            Log.e(TAG, "handleStartAdv" + Log.getStackTraceString(throwable));
        }
    }

    public void startProjection() throws RemoteException {
        try {
            if(connectService!= null){
                connectService.startProjection();
            }
        } catch (Throwable throwable) {
            Log.e(TAG, "handleStartAdv" + Log.getStackTraceString(throwable));
        }
    }

    public void pauseProjection() throws RemoteException {
        try {
            if(connectService!= null){
                connectService.pauseProjection();
            }
        } catch (Throwable throwable) {
            Log.e(TAG, "pauseProjection" + Log.getStackTraceString(throwable));
        }
    }

    public void stopProjection() throws RemoteException {
        try {
            if(connectService!= null){
                connectService.stopProjection();
            }
        } catch (Throwable throwable) {
            Log.e(TAG, "handleStartAdv" + Log.getStackTraceString(throwable));
        }
    }

    public void onTouchEvent(MotionEvent event) throws RemoteException {
        try {
            if(connectService!= null){
                connectService.onTouchEvent(event);
            }
        } catch (Throwable throwable) {
            Log.e(TAG, "onTouchEvent" + Log.getStackTraceString(throwable));
        }
    }

    public void requestAppList(){
        try {
            if (connectService != null) {
                connectService.requestAppList();
            }
        } catch (Throwable throwable) {
            Log.e(TAG, "requestAppList" + Log.getStackTraceString(throwable));
        }
    }


    public void requestStartApp(String packageName) throws RemoteException {
        try {
            if(connectService!= null){
                connectService.requestStartApp(packageName);
            }
        } catch (Throwable throwable) {
            Log.e(TAG, "requestStartApp" + Log.getStackTraceString(throwable));
        }
    }

    public void requestBackground(boolean isBackground) throws RemoteException {
        try {
            if(connectService!= null){
                connectService.requestBackground(isBackground);
            }
        } catch (Throwable throwable) {
            Log.e(TAG, "requestBackground" + Log.getStackTraceString(throwable));
        }
    }



    public boolean updateCarConfig(Surface surface, int width,int height) throws RemoteException {
        try {
            if(connectService!= null){
            boolean result = connectService.updateCarConfig(surface,width,height);
             Log.e(TAG, "updateCarConfig" + result);
            return  result;
            }
        } catch (Throwable throwable) {
            Log.e(TAG, "updateCarConfig" + Log.getStackTraceString(throwable));
        }
        return false;
    }


    public void disconnectDevice(String deviceId) throws RemoteException {
        try {
            if(connectService!= null){
                connectService.disconnectDevice(deviceId);
            }
        } catch (Throwable throwable) {
            Log.e(TAG, "disconnectDevice" + Log.getStackTraceString(throwable));
        }
    }

    public void updateDimInfo(){
        try {
            if (connectService != null) {
                connectService.updateDimInfo();
            }
        } catch (Throwable throwable) {
            Log.e(TAG, "updateDimInfo" + Log.getStackTraceString(throwable));
        }
    }

    public void sendCarData(byte[] bytes,int requestCode){
            try {
                if (connectService != null) {
                    connectService.sendHiCarData(bytes, requestCode);
                }
            } catch (Throwable throwable) {
                Log.e(TAG, "sendCarData" + Log.getStackTraceString(throwable));
            }
    }

    public void registerHiCarListener(IHiCarListener  listener) throws RemoteException {
        try {
            if(connectService!= null){
                connectService.registerHiCarListener(listener);
            }
        } catch (Throwable throwable) {
            Log.e(TAG, "registerHiCarListener" + Log.getStackTraceString(throwable));
        }
    }

    public void unregisterHiCarListener(IHiCarListener listener) throws RemoteException {
        try {
            if(connectService != null){
                connectService.unregisterHiCarListener(listener);
            }
        } catch (Throwable throwable) {
            Log.e(TAG, "unregisterHiCarListener" + Log.getStackTraceString(throwable));
        }
    }


    public void release() {
        if (context != null && serviceConnection != null) {
            context.unbindService(serviceConnection);
        }
        context = null;
        serviceConnection = null;
    }

    public boolean ensureServiceAvailable() {
        if (connectService == null) {
            Log.e(TAG, "service = null");
            return false;
        }
        IBinder binder = connectService.asBinder();
        if (binder == null) {
            Log.e(TAG, "service.getBinder() = null");
            return false;
        }
        if (!binder.isBinderAlive()) {
            Log.e(TAG, "service.getBinder().isBinderAlive() = false");
            return false;
        }
        if (!binder.pingBinder()) {
            Log.e(TAG, "service.getBinder().pingBinder() = false");
            return false;
        }
        return true;
    }

    public interface InitConnectServiceCallback{
        void onServiceConnected(boolean result);
    }

    public void setInitConnectServiceCallback(InitConnectServiceCallback initConnectServiceCallback) {
        this.initConnectServiceCallback = initConnectServiceCallback;
    }

    public interface UnRegisterHiCarListenerCallback {
        void onUnRegisterHiCarListenerCallback(boolean result);
    }

    public void setUnRegisterHiCarListenerCallback(UnRegisterHiCarListenerCallback unRegisterHiCarListenerCallback) {
        this.unRegisterHiCarListenerCallback = unRegisterHiCarListenerCallback;
    }


    public int getCurrentEventType() throws RemoteException {
        try {
            if(connectService!= null){
              return connectService.getCurrentEventType();
            }
        } catch (Throwable throwable) {
            Log.e(TAG, "getCurrentEventType" + Log.getStackTraceString(throwable));
        }
        return -1;
    }

    public void resetCurrentEventType(int type) throws RemoteException {
        try {
            if(connectService!= null){
                connectService.setCurrentEventType(type);
            }
        } catch (Throwable throwable) {
            Log.e(TAG, "setCurrentEventType" + Log.getStackTraceString(throwable));
        }

    }

    public String getPhoneName()throws RemoteException {
        try {
            if (connectService != null) {
                return connectService.getPhoneName();
            }
        } catch (Throwable throwable) {
            Log.e(TAG, "getPhoneName" + Log.getStackTraceString(throwable));
        }
        return "";
    }

    public int getCurrentCastType()throws RemoteException {
        try {
            if (connectService != null) {
                return connectService.currentCastType();
            }
        } catch (Throwable throwable) {
            Log.e(TAG, "getCurrentCastType" + Log.getStackTraceString(throwable));
        }
        return 0;
    }

    public void finishActivity(boolean isBack ) throws RemoteException {
        try {
            if (connectService != null) {
                connectService.finishActivity(isBack);
            }
        } catch (Throwable throwable) {
            Log.e(TAG, "finishActivity" + Log.getStackTraceString(throwable));
        }

    }

    public void moveTaskToBack(boolean isBack ) throws RemoteException {
        try {
            if (connectService != null) {
                connectService.moveTaskToBack(isBack);
            }
        } catch (Throwable throwable) {
            Log.e(TAG, "moveTaskToBack" + Log.getStackTraceString(throwable));
        }

    }

    public void deleteAppList() throws RemoteException {
        try {
            if (connectService != null) {
                connectService.deleteApplist();
            }
        } catch (Throwable throwable) {
            Log.e(TAG, "deleteAppList" + Log.getStackTraceString(throwable));
        }

    }

    public void ManualReconnect() throws RemoteException {
        try {
            if (connectService != null) {
                connectService.manualReconnect();
            }
        } catch (Throwable throwable) {
            Log.e(TAG, "ManualReconnect" + Log.getStackTraceString(throwable));
        }

    }


    public void disconnectCarlink() {

        try {
            if (connectService != null) {
                connectService.disconnectCarlink();
            }
        } catch (Throwable throwable) {
            Log.e(TAG, "disconnectCarlink: " + Log.getStackTraceString(throwable));
        }

    }
}
