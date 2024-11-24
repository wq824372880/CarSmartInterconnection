package com.zeekrlife.hicar.app.aop

/**
 *    desc   : 权限申请注解
 */
@kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.PROPERTY_SETTER)
annotation class Permissions constructor(
    /**
     * 需要申请权限的集合
     */
    vararg val value: String
)