package com.zeekrlife.net.api;

import java.lang.System;

@kotlin.Metadata(mv = {1, 8, 0}, k = 1, d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0017\b\u00c6\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000b\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\f\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\r\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000e\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u0012\u0010\u000f\u001a\u00020\u00048\u0006@\u0006X\u0087\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0010\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0011\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0012\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0013\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0014\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0015\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0016\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0017\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0018\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0019\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u001a\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u001b"}, d2 = {"Lcom/zeekrlife/net/api/NetUrl;", "", "()V", "API_VERSION_V1", "", "APPLET_DEVICE_SIGNATURE", "APPLET_USABLE_APPLET_IDS", "APP_ATTRIBUTES", "APP_DETAIL", "APP_GET_ALL_PACKAGES", "APP_INSTALL_DIGITAL_SIGNATURE", "APP_LIST", "APP_QUERY_ADVERTISEMNETS", "APP_QUERY_DUAL_AUDIO", "APP_UPDATE_APP_LIST", "BASE_URL", "BASE_URL_DEV", "BASE_URL_PRODUCTION", "BASE_URL_TESTING", "DOWNLOAD_URL", "HOME_APP_DETAIL", "HOME_CATEGORT_LIST", "HOME_LIST", "LOGIN", "PROTOCOL_INFO", "PROTOCOL_SIGN", "UPLOAD_URL", "net_release"})
public final class NetUrl {
    @org.jetbrains.annotations.NotNull()
    public static final com.zeekrlife.net.api.NetUrl INSTANCE = null;
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String BASE_URL_DEV = "https://snc-api-gw-dev.zeekrlife.com/app-market/openserver/";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String BASE_URL_TESTING = "https://snc-api-gw-sit.zeekrlife.com/app-market/openserver/";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String BASE_URL_PRODUCTION = "https://snc-api-gw.zeekrlife.com/app-market/openserver/";
    @org.jetbrains.annotations.NotNull()
    @rxhttp.wrapper.annotation.DefaultDomain()
    @kotlin.jvm.JvmField()
    public static java.lang.String BASE_URL = "https://snc-api-gw-sit.zeekrlife.com/app-market/openserver/";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String API_VERSION_V1 = "v1";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String PROTOCOL_INFO = "clientApi/v1/protocol/info";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String PROTOCOL_SIGN = "clientApi/v1/protocol/sign";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String LOGIN = "user/login";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String HOME_LIST = "article/list/%1$d/json";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String HOME_CATEGORT_LIST = "clientApi/v1/category/allList";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String HOME_APP_DETAIL = "/clientApi/app/detail/%1$d/%2$d";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String UPLOAD_URL = "http://t.xinhuo.com/index.php/Api/Pic/uploadPic";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String DOWNLOAD_URL = "http://update.9158.com/miaolive/Miaolive.apk";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String APP_DETAIL = "clientApi/v1/app/detail/app/%1$d";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String APP_LIST = "clientApi/v1/app/appList";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String APP_QUERY_DUAL_AUDIO = "clientApi/v1/app/queryDualAudio";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String APP_QUERY_ADVERTISEMNETS = "clientApi/v1/app/queryAdvertisements";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String APP_INSTALL_DIGITAL_SIGNATURE = "clientApi/v1/app/signature";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String APP_ATTRIBUTES = "clientApi/v1/app/attributes";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String APP_UPDATE_APP_LIST = "clientApi/v1/ota/update/appList";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String APP_GET_ALL_PACKAGES = "clientApi/v1/ota/getAllPackagePath";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String APPLET_DEVICE_SIGNATURE = "clientApi/v1/applet/acquireDeviceSignature";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String APPLET_USABLE_APPLET_IDS = "clientApi/v1/applet/usableAppletIds";
    
    private NetUrl() {
        super();
    }
}