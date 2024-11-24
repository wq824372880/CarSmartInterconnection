package com.zeekrlife.connect.core.utils

import androidx.annotation.RequiresPermission
import com.huawei.hicarsdk.HiCarAdapter
import com.zeekrlife.connect.core.ConnectServiceImpl
import com.zeekrlife.connect.core.manager.bluetooth.MyBluetoothManager
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 *Copyright (c) 2021-2023 极氪汽车（宁波杭州湾新区）有限公司.All rights reserved.
 *@description: 蓝牙工具类
 *@author: e-Yang.Dong1
 *@date: 2023/7/11 10:28:43
 *@version: V1.0
 */
class BluetoothUtil {

    companion object {
        @RequiresPermission("android.permission.BLUETOOTH_CONNECT")
        fun openBluetooth() {
//            val bluetoothManager =
//                Common.app.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
//            val bluetoothAdapter = bluetoothManager.adapter
//            bluetoothAdapter?.let {
//                if (!it.isEnabled) {
//                    Log.i(tag, "yang openBluetooth")
//                    it.enable()
//                }
//            }
//            val mAdapter = BluetoothAdapter.getDefaultAdapter()
//            if (!mAdapter.isEnabled) {
//                mAdapter.enable()
//            }
            val bluetoothManager = MyBluetoothManager.getManager()
            if(!bluetoothManager.isBtOpened){
                bluetoothManager.setBtOpen(true)
            }
//            ConnectServiceImpl.instance.getCarConfig()?.updateAdvPower(-120)
            ConnectServiceImpl.instance.getCarConfig()?.updateSupportReconnect(true)
        }

        @RequiresPermission("android.permission.BLUETOOTH_CONNECT")
        fun startBluetoothRecommend(mHiCarAdapter: HiCarAdapter?) {
//                mHiCarAdapter?.startBluetoothRecommend()
                mHiCarAdapter?.startBlueToothRecommend()
        }
    }
}