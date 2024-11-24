package com.zeekrlife.common.ext

import android.view.View

private var triggerLastTime: Long = 0
private var lastExecuteTime: Long = 0

/**
 * get set
 * 给view添加一个延迟的属性（用来屏蔽连击操作）
 */
private var triggerDelay: Long = 0

/**
 *
 */
private var onAppInfoAll: Long = 0
private var onAppInfoAdd: Long = 0
private var backgroundObserver: Long = 0

/**
 * 判断时间是否满足再次点击的要求（控制点击）
 */
private fun <T : View> T.clickEnable(): Boolean {
    var clickable = false
    val currentClickTime = System.currentTimeMillis()
    if (currentClickTime - triggerLastTime >= triggerDelay) {
        triggerLastTime = currentClickTime
        clickable = true
    }
    return clickable
}

/***
 * 带延迟过滤点击事件的 View 扩展
 * @param delay Long 延迟时间，默认600毫秒
 * @param block: (T) -> Unit 函数
 * @return Unit
 */
fun <T : View> T.clickWithTrigger(delay: Long = 1000, block: (T) -> Unit) {
    triggerDelay = delay
    setOnClickListener {
        if (clickEnable()) {
            block(this)
        }
    }
}

/**
 * 判断时间是否满足再次执行的要求
 */
private fun isExecutable(): Boolean {
    val currentTime = System.currentTimeMillis()
    if (currentTime - lastExecuteTime >= triggerDelay) {
        lastExecuteTime = currentTime
        return true
    }
    return false
}

/**
 * 带延迟过滤执行的方法扩展
 * @param delay Long 延迟时间，默认1000毫秒
 * @param block: () -> Unit 方法逻辑
 */
fun executeWithNoRepeat(delay: Long = 1000, block: () -> Unit) {
    triggerDelay = delay
    if (isExecutable()) {
        block()
    }
}

