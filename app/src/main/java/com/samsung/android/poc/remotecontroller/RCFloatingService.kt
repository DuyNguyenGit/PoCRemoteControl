package com.samsung.android.poc.remotecontroller

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import com.samsung.android.poc.remotecontroller.data.extensions.floatingLayoutParams
import com.samsung.android.poc.remotecontroller.eventhandler.CommandInfo
import com.samsung.android.poc.remotecontroller.eventhandler.EventManager
import kotlinx.android.synthetic.main.layout_floating_window.view.*

class RCFloatingService : Service() {

    private val mWindowManager: WindowManager by lazy {
        this.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    }
    private lateinit var mFloatingView: View

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        mFloatingView = LayoutInflater.from(this).inflate(R.layout.layout_floating_window, null)
        mWindowManager.addView(mFloatingView, WindowManager.LayoutParams().floatingLayoutParams())
        setViewTouchListener()
    }

    private fun setViewTouchListener() {
        with(mFloatingView) {
            findViewById<View>(R.id.close_btn).setOnClickListener { stopSelf() }
            findViewById<View>(R.id.collapse_view).setOnClickListener {
                collapse_view.visibility = View.GONE
                expanded_container.visibility = View.VISIBLE
            }
            for (i in 0 until CommandInfo.rcKeySparseArray.size()) {
                val viewId = CommandInfo.rcKeySparseArray.keyAt(i)
                val event = CommandInfo.getKeyById(viewId)
                findViewById<View>(viewId)?.setOnClickListener {
                    EventManager().handleKeyEvent(context, event)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mFloatingView.let { mWindowManager.removeView(it) }
    }
}
