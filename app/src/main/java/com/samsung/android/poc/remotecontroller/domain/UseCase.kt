package com.samsung.android.poc.remotecontroller.domain

import com.samsung.multiscreen.Client
import io.reactivex.Scheduler
import io.reactivex.Single

abstract class UseCase<T>(
    private val executorThread: Scheduler,
    private val uiThread: Scheduler
) {
    fun execute(): Single<Client> {
        return create()
            .subscribeOn(executorThread)
            .observeOn(uiThread)
    }

    /**
     * Create UseCase
     * It will be implemented a class that inherit this abstract class
     * In the implementation part, it create Single object to execute a specific request.
     *
     * @return created Single
     */
    protected abstract fun create(): Single<Client>
}
