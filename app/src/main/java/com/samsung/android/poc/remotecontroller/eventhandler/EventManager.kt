package com.samsung.android.poc.remotecontroller.eventhandler

import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.util.Log
import android.widget.Toast
import com.samsung.android.poc.remotecontroller.presentation.MainActivity
import com.samsung.multiscreen.Device
import com.samsung.multiscreen.RemoteControl

class EventManager {

    companion object {
        var mRemoteControl: RemoteControl? = null
        var mDeviceInfo: Device? = null
    }

    fun handleKeyEvent(context: Context, action: String) {
        Log.d("EventManager", "handleKeyEvent: >>>action = $action")
        mRemoteControl?.let {
            if (it.isConnected) {
                it.sendRemoteKey(action, RemoteControl.KeyOperation.Click)
            } else {
                reconnect(context)
            }
            return
        }
        reconnect(context)
    }

    private fun reconnect(context: Context) {
        Toast.makeText(context, "The connection is timeout. Please reconnect to it", Toast.LENGTH_LONG).show()
        context.startActivity(Intent(context, MainActivity::class.java).setFlags(FLAG_ACTIVITY_NEW_TASK))
    }

}