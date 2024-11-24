package com.zeekrlife.connect.core.utils;

import android.text.TextUtils;
import android.util.Log;

import com.zeekr.sdk.device.impl.DeviceAPI;

public class VehicleUtil {

    /**
     * 车型
     */
    private static final String VEHICLE_DC1E_A2 = "DC1E-A2";
    private static final String VEHICLE_DC1E_BASE = "DC1E-A2-BASE";
    private static final String VEHICLE_CS1E = "CS1E";


    private static final String MODE_ID_DC1E = "00049200";
    private static final String MODE_ID_CS1E = "00049400";
    private static final String MODE_ID_DEFAULT = "00040900";

    public static String vehicleType;

    public static final String TAG = "VehicleUtil";
    public static String getVehicleType() {
        if (TextUtils.isEmpty(vehicleType)) {
            try{
                return DeviceAPI.get().getVehicleType();
            }catch (Exception e){
                e.printStackTrace();
                return "";
            }
        } else {
            return vehicleType;
        }
    }


    public static String getModeId(){
        String vehicle = getVehicleType();
        Log.e(TAG, "vehicleType：  " + vehicle);
        if (VEHICLE_DC1E_A2.equalsIgnoreCase(vehicle) || VEHICLE_DC1E_BASE.equalsIgnoreCase(vehicle)) {
            return MODE_ID_DC1E;
        } else if (VEHICLE_CS1E.equalsIgnoreCase(vehicle)) {
            return MODE_ID_CS1E;
        }
        return MODE_ID_DEFAULT;
    }


}
