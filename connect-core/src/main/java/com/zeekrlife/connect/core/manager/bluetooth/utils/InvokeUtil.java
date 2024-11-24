package com.zeekrlife.connect.core.manager.bluetooth.utils;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.content.AttributionSource;
import android.os.Build;

import androidx.annotation.RequiresApi;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Copyright (c) 2021-2022 浙江极氪智能科技有限公司.All rights reserved.
 *
 * @description: 蓝牙反射调用
 * @author: Hongyun.Wu
 * @date: 2022/12/15 17:40:17
 * @version: V1.0
 */


public class InvokeUtil {

    public static AttributionSource getAttributionSource() {
        AttributionSource attributionSource = null;
        try {
            @SuppressLint("BlockedPrivateApi") Method method = BluetoothAdapter.class.getDeclaredMethod("getAttributionSource");
            method.setAccessible(true);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                attributionSource = (AttributionSource) method.invoke(BluetoothAdapter.getDefaultAdapter(), new Object[]{});
            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return attributionSource;
    }


    public static int getSideLabel(Object instance) {
        try {
            Class<?> aClass = instance.getClass();
            return (int) aClass.getDeclaredMethod("getSideLabel")
                    .invoke(instance, new Object[]{});
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static int getSideIcon(Object instance) {
        try {
            Class<?> aClass = instance.getClass();
            return (int) aClass.getDeclaredMethod("getSideIcon")
                    .invoke(instance, new Object[]{});
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
