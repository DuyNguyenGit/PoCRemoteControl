package com.samsung.android.poc.remotecontroller

import android.Manifest
import android.net.Uri
import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.GrantPermissionRule
import com.samsung.multiscreen.*
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.Observable
import org.junit.Assert.assertNotNull
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.TimeUnit


@RunWith(AndroidJUnit4::class)
class FindTVTest {

    private val TAG = FindTVTest::class.simpleName
    private var mToken: String = "72563310"
    private var numberOfService = 0
    private var mDeviceTestNotConnectedService: Service? = null
    private var mDeviceTestConnectedService: Service? = null
    private var mClient: Client? = null
    private var mUri: String? = null

    @get:Rule
    var permissionRule: GrantPermissionRule = GrantPermissionRule.grant(
        Manifest.permission.INTERNET,
        Manifest.permission.ACCESS_NETWORK_STATE,
        Manifest.permission.ACCESS_WIFI_STATE,
        Manifest.permission.CHANGE_WIFI_MULTICAST_STATE
    )

    fun Search.getFoundServices(): Flowable<Service> {
        return Flowable.create({ emitter ->
            setOnServiceFoundListener { emitter.onNext(it) }
            start()
        }, BackpressureStrategy.LATEST)
    }

    fun connectToService(uri: Uri): Observable<Service> {
        return Observable.create { emitter ->
            Service.getByURI(uri, object : Result<Service> {
                override fun onSuccess(service: Service) {
                    Log.d(TAG, "onSuccess: >>>connect to Service success")
                    emitter.onNext(service)
                    emitter.onComplete()
                }

                override fun onError(error: Error) {
                    Log.e(TAG, "onError: >>>connect to Service failed")
                    emitter.onError(Throwable(error.message))
                }
            })
        }
    }

    fun getClient(client: Client): Boolean {
        mClient = client
        Log.d(TAG, "getClient: >>>new token = $mToken")
        return true
    }

    fun RemoteControl.connectToTV(): Observable<Client> {
        return Observable.create { emitter ->
            connect(HashMap<String, String>(), object : Result<Client> {
                override fun onSuccess(client: Client?) {
                    Log.d(TAG, "onSuccess: >>>RemoteControl connect to TV success")
                    emitter.onNext(client!!)
                    emitter.onComplete()
                }

                override fun onError(error: Error?) {
                    Log.e(TAG, "onError: >>>RemoteControl connect to TV failed")
                    emitter.onError(Throwable(error!!.message))
                }
            })
        }
    }

    @Test
    fun testSearchTVs() {
        Log.d(TAG, "searchTVs: >>> ")
        val search = Service.search(InstrumentationRegistry.getInstrumentation().targetContext)
        search.getFoundServices()
            .doOnNext {
                Log.d(TAG, "searchTVs: >>> service = ${it.uri} - ${it.name}")
                numberOfService++
            }
            .test()
            .awaitDone(10, TimeUnit.SECONDS)
            .assertValueCount(numberOfService)
    }

    @Test
    fun testFindTVByDeviceName() {
        val tvName = "[TV] UITEST-2020-QTV-US"
        val search = Service.search(InstrumentationRegistry.getInstrumentation().targetContext)
        search.getFoundServices()
            .doOnNext {
                Log.d(TAG, "searchTVs: >>> service = ${it.uri} - ${it.name}")
            }
            .filter { tvName == it.name }
            .doOnNext {
                Log.d(TAG, "searchTVs: >>> TV's service = ${it.uri} - ${it.name}")
            }
            .test()
            .awaitDone(10, TimeUnit.SECONDS)
            .assertValue { it.name == tvName }
    }

    @Test
    fun testConnectToService() {
        val tvName = "[TV] UITEST-2020-QTV-US"
        val search = Service.search(InstrumentationRegistry.getInstrumentation().targetContext)
        search.getFoundServices()
            .doOnNext {
                Log.d(TAG, "searchTVs: >>> service = ${it.uri} - ${it.name}")
            }
            .filter { tvName == it.name }
            .doOnNext {
                Log.d(TAG, "searchTVs: >>> TV's service = ${it.uri} - ${it.name}")
                this.mDeviceTestNotConnectedService = it
                this.mUri = it.uri.toString()
            }
            .test()
            .awaitDone(10, TimeUnit.SECONDS)
            .assertValue { it.name == tvName }
        assertNotNull(mDeviceTestNotConnectedService)
        assertNotNull(mUri)

        //test Connect to Service
        connectToService(Uri.parse(mUri))
            .doOnNext {
                mDeviceTestConnectedService = it
                Log.d(TAG, "testConnectToService: >>>onNext: connect to Service success")
            }
            .test()
            .await()
            .assertComplete()
        assertNotNull(mDeviceTestConnectedService)

        //test connect to Remote Controller
        val remoteControl = mDeviceTestConnectedService!!.createRemoteControl()
        remoteControl.setSecurityMode(true, mToken)
        remoteControl.connectToTV()
            .test()
            .await()
            .assertValue(this::getClient)
            .assertComplete()

        //test sending remote control event
        remoteControl.sendRemoteKey("KEY_VOLUP", RemoteControl.KeyOperation.Click)
        Thread.sleep(2000)
        remoteControl.sendRemoteKey("KEY_VOLDOWN", RemoteControl.KeyOperation.Click)
        Thread.sleep(2000)
        remoteControl.sendRemoteKey("KEY_HOME", RemoteControl.KeyOperation.Click)
        Thread.sleep(2000)
        remoteControl.sendRemoteKey("KEY_POWER", RemoteControl.KeyOperation.Click)
        Thread.sleep(4000)
        remoteControl.sendRemoteKey("KEY_POWER", RemoteControl.KeyOperation.Click)
        Thread.sleep(2000)

    }

}