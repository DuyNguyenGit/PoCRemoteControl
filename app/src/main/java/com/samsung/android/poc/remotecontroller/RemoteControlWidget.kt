package com.samsung.android.poc.remotecontroller

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.RemoteViews
import com.samsung.android.poc.remotecontroller.datastore.DataStore
import com.samsung.android.poc.remotecontroller.datastore.KEY_WORD
import com.samsung.android.poc.remotecontroller.datastore.TOGGLE_SWITCH_ACTION
import com.samsung.android.poc.remotecontroller.eventhandler.CommandInfo
import com.samsung.android.poc.remotecontroller.eventhandler.EventManager


/**
 * Implementation of App Widget functionality.
 */
class RemoteControlWidget : AppWidgetProvider() {
    private val TAG = RemoteControlWidget::class.simpleName

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        Log.d(TAG, "onUpdate: >>>")
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        Log.d(TAG, "onReceive: >>>")
        when (intent.action) {
            TOGGLE_SWITCH_ACTION -> {
                DataStore.saveSwitchButtonStatus(context, !DataStore.getSwitchButtonStatus(context))
                toggleVisibilityPad(context)
            }
            else -> {
                Log.d(TAG, "onReceive: >>>action = ${intent.action}")
                if (intent.action!!.startsWith(KEY_WORD, false)) {
                    EventManager().handleKeyEvent(context, intent.action!!)
                } else {
                    Log.d(TAG, "onReceive: >>>Do nothing")
                }
            }
        }
    }

    private fun toggleVisibilityPad(context: Context) {
        val views = RemoteViews(context.packageName, R.layout.remote_control_widget)
        val appWidget = ComponentName(context, RemoteControlWidget::class.java)
        val appWidgetManager = AppWidgetManager.getInstance(context)
        updateNumpadVisibility(context, views)
        appWidgetManager.updateAppWidget(appWidget, views)
    }
}

internal fun updateAppWidget(
    context: Context,
    appWidgetManager: AppWidgetManager,
    appWidgetId: Int
) {
    val views = RemoteViews(context.packageName, R.layout.remote_control_widget)
    val intent = Intent(context, RemoteControlWidget::class.java)
    setKeyPadEvent(intent, context, views)
    updateNumpadSwitchButtonEvent(intent, views, context)
    updateNumpadVisibility(context, views)
    appWidgetManager.updateAppWidget(appWidgetId, views)
}

private fun setKeyPadEvent(intent: Intent, context: Context, views: RemoteViews) {
    Log.d(
        "RemoteControlWidget",
        "setKeyPadEvent: >>>total key = ${CommandInfo.rcKeySparseArray.size()}"
    )
    for (i in 0 until CommandInfo.rcKeySparseArray.size()) {
        val key: Int = CommandInfo.rcKeySparseArray.keyAt(i)
        val value: String = CommandInfo.rcKeySparseArray.get(key)
        intent.action = value
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        Log.d("RemoteControlWidget", "setKeyPadEvent: >>>key = $key - action = $value")
        views.setOnClickPendingIntent(key, pendingIntent)
    }
}

private fun updateNumpadVisibility(
    context: Context,
    views: RemoteViews
) {
    val switchButtonStatus = DataStore.getSwitchButtonStatus(context)
    views.setViewVisibility(
        R.id.numberpad_area,
        if (switchButtonStatus) View.VISIBLE else View.GONE
    )
    views.setViewVisibility(R.id.touchpad_area, if (switchButtonStatus) View.GONE else View.VISIBLE)
}

private fun updateNumpadSwitchButtonEvent(
    intent: Intent,
    views: RemoteViews,
    context: Context
) {
    intent.action = TOGGLE_SWITCH_ACTION
    views.setOnClickPendingIntent(
        R.id.rc_switch_d,
        PendingIntent.getBroadcast(
            context,
            0,
            intent,
            0
        )
    )
}


