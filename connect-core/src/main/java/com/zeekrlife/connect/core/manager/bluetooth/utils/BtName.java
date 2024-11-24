package com.zeekrlife.connect.core.manager.bluetooth.utils;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.hardware.camera2.utils.ListUtils;
import android.text.TextUtils;
import android.util.Log;

import com.zeekrlife.common.util.SPUtils;
import com.zeekrlife.connect.core.constants.SPConstants;
import com.zeekrlife.connect.core.manager.bluetooth.MyBluetoothManager;

import java.util.List;

/**
 * @author qiangwang
 * @e-mail qiangwang@ecarx.com.cn
 * @time 2020/09/09
 * @description Bluetooth 名称字符串
 */
public class BtName {

    /**
     * 获取 BluetoothProfile 名称
     *
     * @param profile BluetoothProfile
     * @return 名称
     */
    public static String getProfileName(int profile) {
        switch (profile) {
            case 1: {
                return (" [" + profile + " : " + "HEADSET] ");
            }
            case 2: {
                return (" [" + profile + " : " + "A2DP] ");
            }
            case 3: {
                return (" [" + profile + " : " + "HEALTH] ");
            }
            case 4: {
                return (" [" + profile + " : " + "HID_HOST] ");
            }
            case 5: {
                return (" [" + profile + " : " + "PAN] ");
            }
            case 6: {
                return (" [" + profile + " : " + "PBAP] ");
            }
            case 7: {
                return (" [" + profile + " : " + "GATT] ");
            }
            case 8: {
                return (" [" + profile + " : " + "GATT_SERVER] ");
            }
            case 9: {
                return (" [" + profile + " : " + "MAP] ");
            }
            case 10: {
                return (" [" + profile + " : " + "SAP] ");
            }
            case 11: {
                return (" [" + profile + " : " + "A2DP_SINK] ");
            }
            case 12: {
                return (" [" + profile + " : " + "AVRCP_CONTROLLER] ");
            }
            case 13: {
                return (" [" + profile + " : " + "AVRCP] ");
            }
            case 14: {
                return (" [" + profile + " : " + "UNKNOWN] ");
            }
            case 15: {
                return (" [" + profile + " : " + "UNKNOWN] ");
            }
            case 16: {
                return (" [" + profile + " : " + "HEADSET_CLIENT] ");
            }
            case 17: {
                return (" [" + profile + " : " + "PBAP_CLIENT] ");
            }
            case 18: {
                return (" [" + profile + " : " + "MAP_CLIENT] ");
            }
            case 19: {
                return (" [" + profile + " : " + "HID_DEVICE] ");
            }
            case 20: {
                return (" [" + profile + " : " + "OPP] ");
            }
            case 21: {
                return (" [" + profile + " : " + "SPP] ");
            }
            default:
                break;
        }
        return (" [" + profile + " : " + "UNKNOWN] ");
    }

    /**
     * 获取连接状态名称
     *
     * @param state 连接状态
     * @return 名称
     */
    public static String getConnectionStateName(int state) {
        switch (state) {
            case BluetoothProfile.STATE_DISCONNECTED:
                return "STATE_DISCONNECTED";
            case BluetoothProfile.STATE_CONNECTING:
                return "STATE_CONNECTING";
            case BluetoothProfile.STATE_CONNECTED:
                return "STATE_CONNECTED";
            case BluetoothProfile.STATE_DISCONNECTING:
                return "STATE_DISCONNECTING";
            default:
                break;
        }
        return "" + state;
    }

    /**
     * 获取绑定状态名称
     *
     * @param state 绑定状态
     * @return 名称
     */
    public static String getBondStateName(int state) {
        switch (state) {
            case BluetoothDevice.BOND_NONE:
                return "BOND_NONE";
            case BluetoothDevice.BOND_BONDING:
                return "BOND_BONDING";
            case BluetoothDevice.BOND_BONDED:
                return "BOND_BONDED";
            default:
                break;
        }
        return "" + state;
    }

    /**
     * 获取扫描模式名称
     *
     * @param state 扫描模式
     * @return 名称
     */
    public static String getScanModeName(int state) {
        switch (state) {
            case BluetoothAdapter.SCAN_MODE_NONE:
                return "SCAN_MODE_NONE";
            case BluetoothAdapter.SCAN_MODE_CONNECTABLE:
                return "SCAN_MODE_CONNECTABLE";
            case BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE:
                return "SCAN_MODE_CONNECTABLE_DISCOVERABLE";
            default:
                break;
        }
        return "" + state;
    }

    /**
     * 获取优先级名称
     *
     * @param priority 优先级
     * @return 名称
     */
    public static String getPriorityName(int priority) {
        switch (priority) {
            case 1000:
                return (" [" + priority + " : " + "PRIORITY_AUTO_CONNECT] ");
            case 100:
                return (" [" + priority + " : " + "PRIORITY_ON] ");
            case 0:
                return (" [" + priority + " : " + "PRIORITY_OFF] ");
            case -1:
                return (" [" + priority + " : " + "PRIORITY_UNDEFINED] ");
            default:
                break;
        }
        return (" [" + priority + " : " + "UNKNOWN] ");
    }


    public static String getHiCarConnectingDevice(){
        String device = SPUtils.getInstance().getString(SPConstants.DEVICE_NAME);

        if (!TextUtils.isEmpty(device)) {
            return device;
        }
        MyBluetoothManager manager = MyBluetoothManager.getManager();
        List<BluetoothDevice> deviceList = manager.getConnectedPhoneDevices();
        if (deviceList != null && !deviceList.isEmpty()) {
            if (deviceList.size() > 1) {
                return "..."; // 双蓝牙连接时 当前无法判断具体哪个设备是连的hicar
            }
            return deviceList.get(0).getName();
        }
        return "...";
    }
}
