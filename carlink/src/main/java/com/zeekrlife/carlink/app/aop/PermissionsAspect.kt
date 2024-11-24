package com.zeekrlife.carlink.app.aop

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import com.hjq.permissions.XXPermissions
import com.zeekr.basic.currentActivity
import com.zeekrlife.carlink.app.util.PermissionCallback
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Pointcut
import timber.log.Timber

/**
 *    desc   : 权限申请切面
 */
@Suppress("unused")
@Aspect
class PermissionsAspect {

    /**
     * 方法切入点
     */
    @Pointcut("execution(@com.zeekrlife.hicar.app.aop.Permissions * *(..))")
    fun method() {}

    /**
     * 在连接点进行方法替换
     */
    @Around("method() && @annotation(permissions)")
    fun aroundJoinPoint(joinPoint: ProceedingJoinPoint, permissions: Permissions) {
        var activity: Activity? = null

        // 方法参数值集合
        val parameterValues: Array<Any?> = joinPoint.args
        for (arg: Any? in parameterValues) {
            if (arg !is AppCompatActivity) {
                continue
            }
            activity = arg
            break
        }
        if ((activity == null) || activity.isFinishing || activity.isDestroyed) {
            activity = currentActivity
        }
        if (activity == null ||activity.isFinishing || activity.isDestroyed) {
            Timber.e("The activity has been destroyed and permission requests cannot be made")
            return
        }
        requestPermissions(joinPoint, activity, permissions.value)
    }

    private fun requestPermissions(joinPoint: ProceedingJoinPoint, activity: Activity, permissions: Array<out String>) {
        XXPermissions.with(activity)
            .permission(*permissions)
            .request(object : PermissionCallback(activity) {
                override fun onGranted(permissions: MutableList<String?>?, all: Boolean) {
                    if (all) {
                        try {
                            // 获得权限，执行原方法
                            joinPoint.proceed()
                        } catch (e: Throwable) {
//                            CrashReport.postCatchedException(e)
                        }
                    }
                }
            })
    }
}