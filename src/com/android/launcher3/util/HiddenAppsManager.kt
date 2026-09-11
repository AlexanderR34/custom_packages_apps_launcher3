package com.android.launcher3.util

import android.content.ComponentName
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.android.launcher3.LauncherAppState
import com.android.launcher3.LauncherFiles
import java.util.Collections

object HiddenAppsManager {
    private const val TAG = "HiddenAppsManager"
    const val KEY_HIDDEN_APPS = "pref_hidden_apps_set"

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(LauncherFiles.SHARED_PREFERENCES_KEY, Context.MODE_PRIVATE)
    }

    fun getHiddenApps(context: Context): Set<String> {
        return getPrefs(context).getStringSet(KEY_HIDDEN_APPS, Collections.emptySet()) ?: Collections.emptySet()
    }

    fun isAppHidden(context: Context, componentName: ComponentName): Boolean {
        val hiddenSet = getHiddenApps(context)
        val fullComp = componentName.flattenToString()
        return hiddenSet.contains(fullComp) || hiddenSet.contains(componentName.packageName)
    }

    fun setAppHidden(context: Context, componentName: ComponentName, hidden: Boolean) {
        val currentSet = HashSet(getHiddenApps(context))
        val fullComp = componentName.flattenToString()
        if (hidden) {
            currentSet.add(fullComp)
        } else {
            currentSet.remove(fullComp)
            currentSet.remove(componentName.packageName)
        }

        getPrefs(context).edit().putStringSet(KEY_HIDDEN_APPS, currentSet).apply()

        try {
            LauncherAppState.getInstance(context).model.forceReload()
        } catch (e: Exception) {
            Log.e(TAG, "Error forceReloading model after hiding app: ${e.message}")
        }
    }
}
