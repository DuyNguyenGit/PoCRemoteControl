package com.samsung.android.poc.remotecontroller.domain.usecases

import com.samsung.android.poc.remotecontroller.domain.MsfDataSource
import com.samsung.android.poc.remotecontroller.domain.UseCase
import com.samsung.multiscreen.Client
import com.samsung.multiscreen.Service
import io.reactivex.Flowable
import io.reactivex.Scheduler
import io.reactivex.Single

class DiscoveryMsfUseCase(
    val dataSource: MsfDataSource,
    executorThread: Scheduler,
    uiThread: Scheduler
) : UseCase<List<Service>>(executorThread, uiThread) {
    override fun create(): Single<Client> {
        return dataSource.discoveryMsf()
    }
}