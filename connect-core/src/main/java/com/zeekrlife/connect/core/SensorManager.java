package com.zeekrlife.connect.core;

import android.content.Context;
import android.util.Log;

import com.alibaba.fastjson.JSONObject;
import com.ecarx.xui.adaptapi.FunctionStatus;
import com.ecarx.xui.adaptapi.car.sensor.ISensor;
import com.zeekr.car.adaptapi.CarApiProxy;
import com.zeekrlife.connect.core.data.repository.HiCarRequestCode;

import java.nio.charset.StandardCharsets;

/**
 * Copyright (c) 2021-2023 极氪汽车（宁波杭州湾新区）有限公司.All rights reserved.
 *
 * @description:
 * @author: Yueming.Zhao
 * @date: 2023/7/3 19:20:05
 * @version: V1.0
 */
public class SensorManager {

    private final String TAG = "SensorManager";
    //p挡位值
    private static final int SENSOR_VALUE_P = 2097712;

    private ISensor mISensor;
    private static SensorManager sensorManager;

    public static void init(Context context) {
        if (sensorManager == null) {
            sensorManager = new SensorManager(context);
        }else{
            sensorManager.uploadOriginSensor();
        }
    }

    public SensorManager(Context context) {
        mISensor = CarApiProxy.getInstance(context).getICar().getSensorManager();
        Log.e(TAG, "----sensonr:  " + mISensor);
        if (mISensor != null) {
            mISensor.registerListener(new ISensor.ISensorListener() {
                @Override
                public void onSensorEventChanged(int i, int sensorValue) {
                    /**
                     * 	Line 636096: 01-01 00:09:34.359  5810  5904 E SensorManager: --onSensorEventChanged--2097664-----i1---2097712
                     * 	Line 687194: 01-01 00:09:37.875  5810  5904 E SensorManager: --onSensorEventChanged--2097664-----i1---2097696
                     * 	Line 710331: 01-01 00:09:39.857  5810  5904 E SensorManager: --onSensorEventChanged--2097664-----i1---2097712
                     * 	Line 1154768: 01-01 00:10:09.899  5810  5904 E SensorManager: --onSensorEventChanged--2097664-----i1---2097712
                     */
                    uploadSensor(sensorValue);
                    Log.e(TAG, "--onSensorEventChanged--" + i + "-----i1---" + sensorValue);
                }

                @Override
                public void onSensorValueChanged(int i, float v) {

                }

                @Override
                public void onSensorSupportChanged(int i, FunctionStatus functionStatus) {

                }
            }, ISensor.SENSOR_TYPE_GEAR);
            uploadOriginSensor(); // 上报初始挡位信息
        }else{
            Log.e(TAG, "---Sensor init failed--");
        }
    }

    //获取挡位
    public void uploadOriginSensor(){
        if (mISensor != null) {
            int sensorEvent = mISensor.getSensorEvent(ISensor.SENSOR_TYPE_GEAR);
            uploadSensor(sensorEvent);
        }
    }

    //上传挡位信息到hicar

    public void uploadSensor(int sensor) {
        Log.e(TAG, "----sensorValue:" + sensor);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("drivingMode", sensor == SENSOR_VALUE_P ? 1 : 0);

//        jsonObject.put("drivingMode", 1);

        String data = jsonObject.toJSONString();
        Log.e("sensor", "---data--" + data);
        ConnectServiceImpl.getInstance().sendHiCarData(data.getBytes(StandardCharsets.UTF_8), HiCarRequestCode.DATA_TYPE_SENSOR);
    }

}
