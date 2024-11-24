package com.zeekrlife.connect.core.constants
import com.alibaba.fastjson.JSONObject
import java.nio.charset.StandardCharsets
/**
 *@author Yueming.Zhao
 *2023/5/16
 */
 object Extensions {
    /**
     * 创建hiCar请求body
     */
    fun Map<String,String>.createByte():ByteArray{
        val json = JSONObject()
        forEach{
            json[it.key] = it.value
        }
        return json.getBytes(StandardCharsets.UTF_8.toString())
    }
}
