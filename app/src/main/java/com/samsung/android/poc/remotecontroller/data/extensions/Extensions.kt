package com.samsung.android.poc.remotecontroller.data.extensions

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.provider.Settings
import android.util.Log
import android.view.Gravity
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.samsung.android.poc.remotecontroller.eventhandler.EventManager.Companion.mDeviceInfo
import com.samsung.multiscreen.*
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.Single


fun Search.getFoundServices(): Flowable<Service> {
    return Flowable.create({ emitter ->
        setOnServiceFoundListener { emitter.onNext(it) }
        start()
    }, BackpressureStrategy.LATEST)
}

fun RemoteControl.connectToTV(): Single<Client> {
    return Single.create { emitter ->
        connect(HashMap<String, String>(), object : Result<Client> {
            override fun onSuccess(client: Client?) {
                Log.d("Extensions", "onSuccess: >>>RemoteControl connect to TV success")
                emitter.onSuccess(client!!)
            }

            override fun onError(error: Error?) {
                Log.e("Extensions", "onError: >>>RemoteControl connect to TV failed")
                emitter.onError(Throwable(error!!.message))
            }
        })
    }
}

fun Service.getDeviceResponse(deviceName: String): Single<Service> {
    val service = this
    return Single.create { emitter ->
        getDeviceInfo(object : Result<Device> {
            override fun onSuccess(device: Device) {
                Log.d("Extensions", "Device Found: device:$device")
                if (device.name.contains(deviceName, true).not()) {
                    emitter.tryOnError(Exception("Device name is not matched"))
                    return
                }
                mDeviceInfo = device
                emitter.onSuccess(service)
            }

            override fun onError(error: Error) {
                Log.d("Extensions", "isDeviceFound: onError, err:$error")
                emitter.tryOnError(Exception("failed to get device info, err:$error"))
            }
        })
    }
}

val Context.canDrawOverlay
    get() = Build.VERSION.SDK_INT < Build.VERSION_CODES.M || Settings.canDrawOverlays(this)

fun Context.showToast(message: String) {
    Handler(mainLooper).postDelayed({
        Toast.makeText(this, message, Toast.LENGTH_LONG).apply { show() }
    }, 50)

}

fun Activity.goToPermissionSetting(action: String, requestCode: Int) {
    startActivityForResult(
        Intent(action, Uri.parse("package:$packageName")), requestCode
    )
}

fun WindowManager.LayoutParams.floatingLayoutParams(): WindowManager.LayoutParams {
    return this.apply {
        width = WindowManager.LayoutParams.MATCH_PARENT
        height = WindowManager.LayoutParams.MATCH_PARENT
        flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
        format = PixelFormat.TRANSLUCENT
        gravity = Gravity.TOP
        x = 0
        y = 100
        type = when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ->
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            else -> WindowManager.LayoutParams.TYPE_TOAST
        }
    }
}

fun AppCompatActivity.showAlert(title: Int, message: Int, func: () -> Unit) {
    val builder = AlertDialog.Builder(this)
        .setTitle(getString(title))
        .setMessage(getString(message))
        .setPositiveButton("Ok") { _, _ ->
            func()
        }
    val dialog: AlertDialog = builder.create()
    dialog.show()
}