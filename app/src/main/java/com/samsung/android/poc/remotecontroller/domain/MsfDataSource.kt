package com.samsung.android.poc.remotecontroller.domain

import com.samsung.multiscreen.Client
import io.reactivex.Single

interface MsfDataSource {
    fun discoveryMsf(): Single<Client>
    fun setDeviceName(deviceName: String)
}
