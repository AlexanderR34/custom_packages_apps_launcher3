package com.android.launcher3.util

import android.content.Context
import com.android.launcher3.LauncherPrefs

object AnimationSpeedHelper {
    const val PREF_ANIMATION_SPEED = "pref_animation_speed"

    @JvmStatic
    fun getMultiplier(context: Context): Float {
        return try {
            val speedStr = LauncherPrefs.getPrefs(context).getString(PREF_ANIMATION_SPEED, "1.0") ?: "1.0"
            speedStr.toFloatOrNull() ?: 1.0f
        } catch (e: Exception) {
            1.0f
        }
    }

    @JvmStatic
    fun scaleDuration(context: Context, defaultDurationMs: Long): Long {
        return (defaultDurationMs * getMultiplier(context)).toLong().coerceAtLeast(50L)
    }

    @JvmStatic
    fun scaleDuration(context: Context, defaultDurationMs: Int): Int {
        return (defaultDurationMs * getMultiplier(context)).toInt().coerceAtLeast(50)
    }
}
