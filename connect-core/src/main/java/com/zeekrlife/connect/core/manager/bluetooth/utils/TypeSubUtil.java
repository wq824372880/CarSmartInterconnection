package com.zeekrlife.connect.core.manager.bluetooth.utils;

import android.bluetooth.ext.BluetoothClass;
import android.bluetooth.ext.SubBluetoothDevice;

import com.huawei.managementsdk.common.LogUtils;


public class TypeSubUtil {

    private static final String TAG = "TypeSubUtil";

    private static int getSubDeviceType(SubBluetoothDevice device) {
        int type = -1;
        BluetoothClass bluetoothClass = device.getBluetoothClass();
        if (bluetoothClass == null) {
            LogUtils.e(TAG, "getSubDeviceType device.name=" + device.getName() + ",bluetoothClass is null!!!");
            return type;
        }
        type = bluetoothClass.getMajorDeviceClass();
        LogUtils.w(TAG, "getSubDeviceType device.name=" + device.getName() + ",type=" + type);
        return type;

        /*switch (type) {
            case BluetoothClass.Device.AUDIO_VIDEO_HEADPHONES://耳机
            case BluetoothClass.Device.AUDIO_VIDEO_WEARABLE_HEADSET://穿戴式耳机
            case BluetoothClass.Device.AUDIO_VIDEO_HANDSFREE://蓝牙耳机
            case BluetoothClass.Device.Major.AUDIO_VIDEO://音频设备
                imageView.setImageResource(R.mipmap.icon_headset);
                break;
            case BluetoothClass.Device.Major.COMPUTER://电脑
                imageView.setImageResource(R.mipmap.icon_computer);
                break;
            case BluetoothClass.Device.Major.PHONE://手机
                imageView.setImageResource(R.mipmap.icon_phone);
                break;
            case BluetoothClass.Device.Major.HEALTH://健康类设备
                imageView.setImageResource(R.mipmap.icon_health);
                break;
            case BluetoothClass.Device.Major.PERIPHERAL://外设
                imageView.setImageResource(getHidClassDrawable(btClass));
                break;

            case BluetoothClass.Device.AUDIO_VIDEO_CAMCORDER://照相机录像机
            case BluetoothClass.Device.AUDIO_VIDEO_VCR://录像机
                imageView.setImageResource(R.mipmap.icon_vcr);
                break;
            case BluetoothClass.Device.AUDIO_VIDEO_CAR_AUDIO://车载设备
                imageView.setImageResource(R.mipmap.icon_car);
                break;
            case BluetoothClass.Device.AUDIO_VIDEO_LOUDSPEAKER://扬声器
                imageView.setImageResource(R.mipmap.icon_loudspeaker);
                break;
            case BluetoothClass.Device.AUDIO_VIDEO_MICROPHONE://麦克风
                imageView.setImageResource(R.mipmap.icon_microphone);
                break;
            case BluetoothClass.Device.AUDIO_VIDEO_PORTABLE_AUDIO://打印机
                imageView.setImageResource(R.mipmap.icon_printer);
                break;
            case BluetoothClass.Device.AUDIO_VIDEO_SET_TOP_BOX://音频视频机顶盒
                imageView.setImageResource(R.mipmap.icon_top_box);
                break;
            case BluetoothClass.Device.AUDIO_VIDEO_VIDEO_CONFERENCING://音频视频视频会议
                imageView.setImageResource(R.mipmap.icon_meeting);
                break;
            case BluetoothClass.Device.AUDIO_VIDEO_VIDEO_DISPLAY_AND_LOUDSPEAKER://显示器和扬声器
                imageView.setImageResource(R.mipmap.icon_tv);
                break;
            case BluetoothClass.Device.AUDIO_VIDEO_VIDEO_GAMING_TOY://游戏
                imageView.setImageResource(R.mipmap.icon_game);
                break;
            case BluetoothClass.Device.AUDIO_VIDEO_VIDEO_MONITOR://可穿戴设备
                imageView.setImageResource(R.mipmap.icon_wearable_devices);
                break;
            default://其它
                imageView.setImageResource(R.mipmap.icon_bluetooth);
                break;
        }*/
    }

    /**
     * 是否耳机
     *
     * @param device
     * @return
     */
    public static boolean isHeadSet(SubBluetoothDevice device) {
        int subDeviceType = getSubDeviceType(device);
        switch (subDeviceType) {
            case BluetoothClass.Device.AUDIO_VIDEO_HEADPHONES://耳机
            case BluetoothClass.Device.AUDIO_VIDEO_WEARABLE_HEADSET://穿戴式耳机
            case BluetoothClass.Device.AUDIO_VIDEO_HANDSFREE://蓝牙耳机
            case BluetoothClass.Device.Major.AUDIO_VIDEO://音频设备
                return true;
        }
        return false;
    }

    /**
     * 是否手柄
     *
     * @param device
     * @return
     */
    public static boolean isHid(SubBluetoothDevice device) {
        int subDeviceType = getSubDeviceType(device);
        switch (subDeviceType) {
            case BluetoothClass.Device.AUDIO_VIDEO_VIDEO_GAMING_TOY://游戏
            case BluetoothClass.Device.Major.PERIPHERAL://外设
                return true;
        }
        return false;
    }

}

