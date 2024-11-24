package com.zeekrlife.connect.core.app.ext

import com.tencent.mmkv.MMKV
import com.zeekrlife.connect.core.data.cache.ValueKey

/**
 * 描述　:
 */

/**
 * 获取MMKV
 */
val mmkv: MMKV by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
    MMKV.mmkvWithID(ValueKey.MMKV_APP_KEY,MMKV.MULTI_PROCESS_MODE)
}

/**
 * 获取始终保存的MMKV实例
 */
val mmkvSave: MMKV by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
    MMKV.mmkvWithID(ValueKey.MMKV_APP_KEY_SAVE,MMKV.MULTI_PROCESS_MODE)
}


