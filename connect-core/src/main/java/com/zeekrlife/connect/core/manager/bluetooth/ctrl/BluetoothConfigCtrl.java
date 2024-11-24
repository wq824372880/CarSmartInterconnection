package com.zeekrlife.connect.core.manager.bluetooth.ctrl;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class BluetoothConfigCtrl {
    private final Context mContext;

    private final String KEY_BLUETOOTH_NAME_INIT = "BluetoothNameInit";

    public BluetoothConfigCtrl(Context ctx) {
        mContext = ctx;
    }

    public void onExit() {

    }

    /**
     * 设置蓝牙名称初始化
     */
    public void setBluetoothNameInit() {
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(KEY_BLUETOOTH_NAME_INIT, true);
        editor.commit();
    }

    /**
     * 蓝牙名称是否初始化
     *
     * @return true: 已经初始化过，false: 没有初始化过
     */
    public boolean isBluetoothNameInit() {
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(mContext);
        return sp.getBoolean(KEY_BLUETOOTH_NAME_INIT, false);
    }
}
