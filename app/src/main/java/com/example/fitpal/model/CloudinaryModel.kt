package com.example.fitpal.model


//import android.content.Context
//import android.graphics.Bitmap
//import com.cloudinary.android.MediaManager
//import com.cloudinary.android.callback.ErrorInfo
//import com.cloudinary.android.callback.UploadCallback
//import com.cloudinary.android.policy.GlobalUploadPolicy
//import com.example.fitpal.BuildConfig
//import com.example.fitpal.base.MyApplication
//import com.example.fitpal.utils.extensions.toFile
//import java.io.File
//import java.io.FileOutputStream

class CloudinaryModel {
//
//    init {
//        val config = mapOf(
//            "cloud_name" to BuildConfig.CLOUD_NAME,
//            "api_key" to BuildConfig.API_KEY,
//            "api_secret" to BuildConfig.API_SECRET
//        )
//
//        MyApplication.Globals.context?.let {
//            MediaManager.init(it, config)
//            MediaManager.get().globalUploadPolicy = GlobalUploadPolicy.defaultPolicy()
//        }
//    }
//
//    fun uploadImage(
//        bitmap: Bitmap,
//        name: String,
//        onSuccess: (String?) -> Unit,
//        onError: (String?) -> Unit
//    ) {
//        val context = MyApplication.Globals.context ?: return
//        val file: File = bitmap.toFile(context, name)
//
//        MediaManager.get().upload(file.path)
//            .option("folder", "images")
//            .callback(object  : UploadCallback {
//                override fun onStart(requestId: String?) {
//
//                }
//
//                override fun onProgress(requestId: String?, bytes: Long, totalBytes: Long) {
//
//                }
//
//                override fun onSuccess(requestId: String?, resultData: Map<*, *>) {
//                    val url = resultData["secure_url"] as? String ?: ""
//                    onSuccess(url)
//                }
//
//                override fun onError(requestId: String?, error: ErrorInfo?) {
//                    onError(error?.description ?: "Unknown error")
//                }
//
//                override fun onReschedule(requestId: String?, error: ErrorInfo?) {
//
//                }
//
//            })
//            .dispatch()
//    }
}