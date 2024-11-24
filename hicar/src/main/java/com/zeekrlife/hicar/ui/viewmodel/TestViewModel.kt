package com.zeekrlife.hicar.ui.viewmodel

import androidx.lifecycle.viewModelScope
import com.hjq.permissions.Permission
import com.zeekr.basic.appContext
import com.zeekrlife.net.api.NetUrl
import kotlinx.coroutines.launch
import com.zeekrlife.common.base.BaseViewModel
import com.zeekrlife.hicar.app.aop.Permissions
import kotlinx.coroutines.flow.catch
import rxhttp.toFlow
import rxhttp.wrapper.entity.Progress
import rxhttp.wrapper.param.RxHttp
import java.io.File
import java.lang.Exception

/**
 * 描述　:
 */
class TestViewModel : BaseViewModel() {

    /**
     * 下载
     * @param downLoadData Function1<ProgressT<String>, Unit>
     * @param downLoadSuccess Function1<String, Unit>
     * @param downLoadError Function1<Throwable, Unit>
     */
    @Permissions(Permission.READ_EXTERNAL_STORAGE, Permission.WRITE_EXTERNAL_STORAGE)
    fun downLoad(downLoadData: (Progress) -> Unit = {}, downLoadSuccess: (String) -> Unit, downLoadError: (Throwable) -> Unit = {}) {
        viewModelScope.launch {
//            if (checkedAndroid_Q()) {
//                //android 10 以上
//                val factory = Android10DownloadFactory(appContext, "${System.currentTimeMillis()}.apk")
//                RxHttp.get(NetUrl.DOWNLOAD_URL)
//                    .toFlow(factory) {
//                        downLoadData.invoke(it)
//                    }.catch {
//                        //异常回调
//                        downLoadError(it)
//                    }.collect {
//                        //成功回调
//                        downLoadSuccess.invoke(UriUtils.getFileAbsolutePath(appContext,it)?:"")
//                    }
//            } else {
                //android 10以下
                val localPath = appContext.externalCacheDir!!.absolutePath + "/${System.currentTimeMillis()}.apk"
                RxHttp.get(NetUrl.DOWNLOAD_URL)
                    .toFlow(localPath) {
                        downLoadData.invoke(it)
                    }.catch {
                        //异常回调
                        downLoadError(it)
                    }.collect {
                        //成功回调
                        downLoadSuccess.invoke(it)
                    }
//            }
        }
    }

    /**
     * 上传
     * @param uploadData Function1<Progress, Unit>
     * @param uploadSuccess Function1<String, Unit>
     * @param uploadError Function1<Throwable, Unit>
     */
    fun upload(filePath: String, uploadData: (Progress) -> Unit = {}, uploadSuccess: (String) -> Unit, uploadError: (Throwable) -> Unit = {}) {
        viewModelScope.launch {
//            if (checkedAndroid_Q() && filePath.startsWith("content:")) {
//                //android 10 以上
//                RxHttp.postForm(NetUrl.UPLOAD_URL)
//                    .addPart(appContext, "apkFile", Uri.parse(filePath))
//                    .toFlow<String> {
//                        //上传进度回调,0-100，仅在进度有更新时才会回调
//                        uploadData.invoke(it)
//                    }.catch {
//                        //异常回调
//                        uploadError.invoke(it)
//                    }.collect {
//                        //成功回调
//                        uploadSuccess.invoke(it)
//                    }
//            } else {
                // android 10以下
                val file = File(filePath)
                if(!file.exists()){
                    uploadError.invoke(Exception("文件不存在"))
                    return@launch
                }
                RxHttp.postForm(NetUrl.UPLOAD_URL)
                    .addFile("apkFile", file)
                    .toFlow<String> {
                        //上传进度回调,0-100，仅在进度有更新时才会回调
                        uploadData.invoke(it)
                    }.catch {
                        //异常回调
                        uploadError.invoke(it)
                    }.collect {
                        //成功回调
                        uploadSuccess.invoke(it)
                    }
//            }

        }
    }
}

private fun checkedAndroid_Q(): Boolean {
//    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
    return false
}