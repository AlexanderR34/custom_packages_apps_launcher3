package com.android.launcher3.util

import android.content.Context
import android.os.IBinder
import android.os.PowerManager
import android.os.SystemClock
import android.util.Log

/**
 * Helper to put the device to sleep on Double Tap gestures.
 * Uses system ROM IPowerManager or PowerManager directly.
 */
object DoubleTapSleepHelper {
    private const val TAG = "DoubleTapSleepHelper"

    fun sleep(context: Context) {
        val now = SystemClock.uptimeMillis()
        try {
            // 1. Try IPowerManager.goToSleep via ServiceManager (standard for system launcher in ROMs)
            val serviceManagerClass = Class.forName("android.os.ServiceManager")
            val getServiceMethod = serviceManagerClass.getMethod("getService", String::class.java)
            val powerBinder = getServiceMethod.invoke(null, Context.POWER_SERVICE) as? IBinder
            if (powerBinder != null) {
                val iPowerManagerStub = Class.forName("android.os.IPowerManager\$Stub")
                val asInterfaceMethod = iPowerManagerStub.getMethod("asInterface", IBinder::class.java)
                val powerManagerService = asInterfaceMethod.invoke(null, powerBinder)
                
                try {
                    val goToSleepMethod = powerManagerService.javaClass.getMethod(
                        "goToSleep",
                        Long::class.javaPrimitiveType,
                        Int::class.javaPrimitiveType,
                        Int::class.javaPrimitiveType
                    )
                    goToSleepMethod.invoke(powerManagerService, now, 0, 0)
                    return
                } catch (ignored: NoSuchMethodException) {
                    val goToSleepMethod = powerManagerService.javaClass.getMethod(
                        "goToSleep",
                        Long::class.javaPrimitiveType
                    )
                    goToSleepMethod.invoke(powerManagerService, now)
                    return
                }
            }
        } catch (e: Exception) {
            Log.d(TAG, "IPowerManager goToSleep failed: ${e.message}")
        }

        try {
            // 2. Try PowerManager.goToSleep
            val pm = context.getSystemService(Context.POWER_SERVICE) as? PowerManager
            if (pm != null) {
                try {
                    val goToSleepMethod = pm.javaClass.getMethod(
                        "goToSleep",
                        Long::class.javaPrimitiveType,
                        Int::class.javaPrimitiveType,
                        Int::class.javaPrimitiveType
                    )
                    goToSleepMethod.invoke(pm, now, 0, 0)
                    return
                } catch (ignored: NoSuchMethodException) {
                    val goToSleepMethod = pm.javaClass.getMethod(
                        "goToSleep",
                        Long::class.javaPrimitiveType
                    )
                    goToSleepMethod.invoke(pm, now)
                    return
                }
            }
        } catch (e: Exception) {
            Log.d(TAG, "PowerManager goToSleep failed: ${e.message}")
        }

        try {
            // 3. Fallback: input keyevent KEYCODE_POWER (26)
            Runtime.getRuntime().exec("input keyevent 26")
        } catch (e: Exception) {
            Log.d(TAG, "Keyevent power failed: ${e.message}")
        }
    }
}
