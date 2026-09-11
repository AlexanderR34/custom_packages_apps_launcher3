package com.android.launcher3.smartspace

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Handler
import android.os.Looper

/**
 * Controller for managing Smartspace lifecycle, time changes, and data updates.
 */
class SmartspaceController private constructor(private val context: Context) {

    interface OnDataUpdatedListener {
        fun onDataUpdated(data: SmartspaceData)
    }

    private val listeners = mutableListOf<OnDataUpdatedListener>()
    private val mainHandler = Handler(Looper.getMainLooper())
    private var isReceiverRegistered = false

    private val timeReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            notifyDataChanged()
        }
    }

    fun addListener(listener: OnDataUpdatedListener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener)
            if (listeners.size == 1) {
                registerReceiver()
            }
            // Trigger initial update
            listener.onDataUpdated(SmartspaceData.createCurrent(context))
        }
    }

    fun removeListener(listener: OnDataUpdatedListener) {
        listeners.remove(listener)
        if (listeners.isEmpty()) {
            unregisterReceiver()
        }
    }

    private fun registerReceiver() {
        if (!isReceiverRegistered) {
            val filter = IntentFilter().apply {
                addAction(Intent.ACTION_TIME_TICK)
                addAction(Intent.ACTION_TIME_CHANGED)
                addAction(Intent.ACTION_TIMEZONE_CHANGED)
                addAction(Intent.ACTION_DATE_CHANGED)
            }
            context.registerReceiver(timeReceiver, filter)
            isReceiverRegistered = true
        }
    }

    private fun unregisterReceiver() {
        if (isReceiverRegistered) {
            try {
                context.unregisterReceiver(timeReceiver)
            } catch (ignored: Exception) {
            }
            isReceiverRegistered = false
        }
    }

    fun notifyDataChanged() {
        mainHandler.post {
            val currentData = SmartspaceData.createCurrent(context)
            for (listener in listeners) {
                listener.onDataUpdated(currentData)
            }
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: SmartspaceController? = null

        fun get(context: Context): SmartspaceController {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: SmartspaceController(context.applicationContext).also { INSTANCE = it }
            }
        }
    }
}
