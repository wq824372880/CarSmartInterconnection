package com.zeekrlife.connect.core;

import android.content.Context;
import android.util.Log;

import com.alibaba.fastjson.JSONObject;
import com.ecarx.xui.adaptapi.FunctionStatus;
import com.ecarx.xui.adaptapi.car.base.ICarFunction;
import com.ecarx.xui.adaptapi.car.vehicle.IVehicle;
import com.zeekr.car.adaptapi.CarApiProxy;
import com.zeekrlife.connect.core.data.repository.HiCarRequestCode;
import com.zeekrlife.connect.core.data.repository.UserRepository;

import java.nio.charset.StandardCharsets;

public class DayNightManager {

    private final String TAG = "DayNightManager";

    //白天黑夜接口值
    private static final int DAY_NIGHT_MODE = 538247424;
    private  ICarFunction iCarFunction;

    private static DayNightManager dayNightManager;

    public static void init(Context context) {
        if (dayNightManager == null) {
            dayNightManager = new DayNightManager(context);
        }else{
            dayNightManager.uploadOriginDayNightMode();
        }
    }


    public DayNightManager(Context mContext){
        if (iCarFunction == null) {
            iCarFunction = CarApiProxy.getInstance(mContext).getICar().getICarFunction();
        }
        if (iCarFunction != null) {
            iCarFunction.registerFunctionValueWatcher(DAY_NIGHT_MODE, new ICarFunction.IFunctionValueWatcher() {
                @Override
                public void onFunctionChanged(int i) {

                }

                @Override
                public void onFunctionValueChanged(int i, int i1, int i2) {
                    if (i == DAY_NIGHT_MODE) {
                        uploadDayNightMode(i2);
                    }
                }

                @Override
                public void onCustomizeFunctionValueChanged(int i, int i1, float v) {

                }

                @Override
                public void onSupportedFunctionStatusChanged(int i, int i1, FunctionStatus functionStatus) {

                }

                @Override
                public void onSupportedFunctionValueChanged(int i, int[] ints) {

                }
            });
            uploadOriginDayNightMode();
        }
    }

    public void uploadOriginDayNightMode(){
        if (iCarFunction != null) {
            int functionValue = iCarFunction.getFunctionValue(DAY_NIGHT_MODE);
            uploadDayNightMode(functionValue);
        }
    }

    public void uploadDayNightMode(int value){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("dayNightMode", value == IVehicle.DAYMODE_SETTING_DAY ? "day":"night");
        String data = jsonObject.toJSONString();
        Log.e("uploadDayNightMode", "---data--" + data);
        ConnectServiceImpl.getInstance().sendHiCarData(data.getBytes(StandardCharsets.UTF_8), UserRepository.DATA_TYPE_DAY_NIGHT_MODE);
    }
}
