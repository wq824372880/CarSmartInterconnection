package com.zeekrlife.connect.core.manager.bluetooth.constant;

public class BluetoothConst {

    /**
     * 默认蓝牙名称
     */
    public final static String DEF_BT_NAME = "ZEEKER";

    /**
     * 优先设备的key
     */
    public static final String KEY_FAV_DEVICE_ADDR = "persist.zeekr.fav_device_address";

    /**
     * 手动扫描
     */
    public static final String ACTION_MANUAL_START_DISCOVERY = "com.zeekrlife.action.MANUAL_START_DISCOVERY";

    /**
     * 手动断开
     */
    public static final String ACTION_MANUAL_DISCONNECT_PROFILE = "com.zeekrlife.action.MANUAL_DISCONNECT_PROFILE";

    /**
     * 手动连接
     */
    public static final String ACTION_MANUAL_CONNECT_DEVICE = "com.zeekrlife.action.MANUAL_CONNECT_DEVICE";

    public static final String EXTRA_PROFILE = "com.zeekrlife.extra.PROFILE";

    /**
     * 本机名称key
     */
    public static final String NATIVE_NAME_KEY = "persist.zeekr.native_name";

    /**
     * 主副蓝牙区分
     */
    public static final String EXTRA_TYPE = "com.zeekrlife.extra.TYPE";


    public static final int mainDeviceType = 1;
    public static final int subDeviceType = 2;

    /**
     * 手柄常量
     */
    public static final int PROFILE_HID = 3;

    /**
     * 耳机常量
     */
    public static final int PROFILE_HEADSET = 4;
}
