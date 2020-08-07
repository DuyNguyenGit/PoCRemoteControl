package com.samsung.android.poc.remotecontroller.data.extensions

import android.util.Log
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
