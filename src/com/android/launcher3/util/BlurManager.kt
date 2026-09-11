package com.android.launcher3.util

import android.content.Context
import com.android.launcher3.LauncherFiles
import com.android.launcher3.LauncherPrefs

/**
 * Manager to control Blur settings for Dock (Hotseat) and All Apps Drawer.
 */
object BlurManager {
    const val KEY_DOCK_BLUR_ENABLED = "pref_dock_blur_enabled"
    const val KEY_ALL_APPS_BLUR_ENABLED = "pref_all_apps_blur_enabled"
    const val KEY_BLUR_INTENSITY = "pref_blur_intensity" // 0.0f to 2.0f (default 1.0f)
    const val KEY_BLUR_INTENSITY_SEEKBAR = "pref_blur_intensity_seekbar" // 0 to 100%

    fun isDockBlurEnabled(context: Context): Boolean {
        return context.getSharedPreferences(LauncherFiles.SHARED_PREFERENCES_KEY, Context.MODE_PRIVATE)
            .getBoolean(KEY_DOCK_BLUR_ENABLED, true)
    }

    fun isAllAppsBlurEnabled(context: Context): Boolean {
        return context.getSharedPreferences(LauncherFiles.SHARED_PREFERENCES_KEY, Context.MODE_PRIVATE)
            .getBoolean(KEY_ALL_APPS_BLUR_ENABLED, true)
    }

    fun getBlurIntensity(context: Context): Float {
        val prefs = context.getSharedPreferences(LauncherFiles.SHARED_PREFERENCES_KEY, Context.MODE_PRIVATE)
        if (prefs.contains(KEY_BLUR_INTENSITY_SEEKBAR)) {
            val progress = prefs.getInt(KEY_BLUR_INTENSITY_SEEKBAR, 100)
            return progress / 100.0f
        }
        return prefs.getFloat(KEY_BLUR_INTENSITY, 1.0f)
    }

    fun setDockBlurEnabled(context: Context, enabled: Boolean) {
        context.getSharedPreferences(LauncherFiles.SHARED_PREFERENCES_KEY, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(KEY_DOCK_BLUR_ENABLED, enabled)
            .apply()
    }

    fun setAllAppsBlurEnabled(context: Context, enabled: Boolean) {
        context.getSharedPreferences(LauncherFiles.SHARED_PREFERENCES_KEY, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(KEY_ALL_APPS_BLUR_ENABLED, enabled)
            .apply()
    }

    fun setBlurIntensity(context: Context, intensity: Float) {
        context.getSharedPreferences(LauncherFiles.SHARED_PREFERENCES_KEY, Context.MODE_PRIVATE)
            .edit()
            .putFloat(KEY_BLUR_INTENSITY, intensity)
            .apply()
    }
}
