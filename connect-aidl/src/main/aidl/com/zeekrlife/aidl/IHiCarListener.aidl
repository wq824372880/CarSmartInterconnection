// IConnectCallback.aidl
package com.zeekrlife.aidl;
import com.zeekrlife.aidl.IHiCarAppInfo;

interface IHiCarListener {

    void onDeviceChange(String key, int event , int errorcode);

    void onDeviceServiceChange(String serviceId, int event);

    void onHiCarApplistChange(inout List<IHiCarAppInfo> list);

    void onDataReceive(String key, int dataType, inout byte[] data);

    void onPinCode(String code);

    void onWifiAPState(boolean isOpen);

    void onBinderDied();

    void onShowStartPage();

}
