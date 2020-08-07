package com.samsung.android.poc.remotecontroller.data.repositories

import android.net.Uri
import android.util.Log
import com.samsung.android.poc.remotecontroller.data.extensions.connectToTV
import com.samsung.android.poc.remotecontroller.data.extensions.getDeviceResponse
import com.samsung.android.poc.remotecontroller.data.extensions.getFoundServices
import com.samsung.android.poc.remotecontroller.domain.MsfDataSource
import com.samsung.android.poc.remotecontroller.eventhandler.EventManager.Companion.mRemoteControl
import com.samsung.multiscreen.*
import io.reactivex.Single
import java.util.concurrent.TimeUnit

class MsfDataRepository(
    private val search: Search,
    private var tvName: String,
    private val token: String
) : MsfDataSource {

    private val TAG = MsfDataRepository::class.simpleName
    override fun discoveryMsf(): Single<Client> {
        Log.d(TAG, "discoveryMsf: >>>tvName = $tvName")
        return search.getFoundServices()
            .doOnNext {
                Log.d(TAG, "searchTVs: >>> service = ${it.uri} - ${it.name}")
            }
            .filter { it.name.contains(tvName) }
            .firstOrError()
            .flatMap { service ->
                Log.d(TAG, "service:$service")
                service.getDeviceResponse(tvName)
            }
            .flatMap { service ->
                Log.d(TAG, "discoveryMsf: >>>connected service: ${service.uri}")
                connectToService(service.uri)
            }
            .timeout(30, TimeUnit.SECONDS)
            .onErrorReturn {
                Log.e(TAG, "discoveryMsf", it)
                null
            }
            .doFinally { if (search.isSearching) search.stop() }
            .flatMap { service ->
                Log.d(TAG, "onEnabled: >>>service is NOT NULL")
                val remoteControl = service.createRemoteControl()
                remoteControl.setSecurityMode(true, token)
                mRemoteControl = remoteControl
                remoteControl.connectToTV()
            }
    }

    override fun setDeviceName(deviceName: String) {
        this.tvName = deviceName
    }

    private fun connectToService(uri: Uri): Single<Service> {
        return Single.create { emitter ->
            Service.getByURI(uri, object : Result<Service> {
                override fun onSuccess(service: Service) {
                    Log.d("Extensions", "onSuccess: >>>connect to Service success")
                    emitter.onSuccess(service)
                }

                override fun onError(error: Error) {
                    Log.e("Extensions", "onError: >>>connect to Service failed")
                    emitter.onError(Throwable(error.message))
                }
            })
        }
    }

}