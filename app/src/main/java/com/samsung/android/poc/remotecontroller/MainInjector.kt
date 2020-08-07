package com.samsung.android.poc.remotecontroller

import android.content.Context
import com.samsung.android.poc.remotecontroller.data.repositories.MsfDataRepository
import com.samsung.android.poc.remotecontroller.datastore.DataStore
import com.samsung.android.poc.remotecontroller.domain.usecases.DiscoveryMsfUseCase
import com.samsung.multiscreen.Service
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

object MainInjector {

    fun getMsfDiscoveryUseCase(context: Context, deviceName: String): DiscoveryMsfUseCase {
        return DiscoveryMsfUseCase(
            MsfDataRepository(
                Service.search(context),
                deviceName,
                DataStore.getTVToken(context)
            ),
            Schedulers.io(),
            AndroidSchedulers.mainThread()
        )
    }
}