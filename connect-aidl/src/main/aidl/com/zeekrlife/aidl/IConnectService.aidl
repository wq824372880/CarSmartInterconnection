// IConnectService.aidl
package com.zeekrlife.aidl;
import com.zeekrlife.aidl.IHiCarListener;

interface IConnectService {

    void loadHiCarSdk();
    void unLoadHiCarSdk();

    void handleStartAdv();
    void handleStoptAdv();

    void startBluetoothRecommend();

    void startProjection();
    void pauseProjection();
    void stopProjection();

    void onTouchEvent(in MotionEvent event);

    void requestStartApp(String packageName);

    boolean updateCarConfig(in Surface surface,int width ,int height);

    void disconnectDevice(String deviceId);

    boolean registerHiCarListener(in IHiCarListener callback);
    boolean unregisterHiCarListener(in IHiCarListener callback);

    void requestAppList();

    void sendHiCarData(in byte[] byteData, int requestCode);

    void sendKeyEvent(int keyEvent,int action);

    void updateDimInfo();

    void openBluetooh();

    boolean getBluetoothState();

    boolean getWifiState();

    void openWifi();

    void closeWifi();

    void modifyAPPassWord(String passWord);

    void setCurrentEventType(int type);

    int getCurrentEventType();

    String getPhoneName();

    void requestBackground(boolean isBackground);

    void startReconnect(String mac);

    void finishActivity(boolean isBack);

    void moveTaskToBack(boolean isBack);

    void deleteApplist();

    void manualReconnect();

    int currentCastType();

    void setAdvPower(int power);

    void disconnectCarlink();

}
