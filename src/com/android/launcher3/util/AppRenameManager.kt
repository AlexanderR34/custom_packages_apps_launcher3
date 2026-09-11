package com.android.launcher3.util

import android.app.AlertDialog
import android.content.ComponentName
import android.content.Context
import android.content.SharedPreferences
import android.text.InputType
import android.util.Log
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.Toast
import com.android.launcher3.LauncherAppState
import com.android.launcher3.LauncherFiles
import com.android.launcher3.model.data.ItemInfo
import com.android.launcher3.model.data.AppInfo
import com.android.launcher3.model.data.WorkspaceItemInfo

object AppRenameManager {
    private const val TAG = "AppRenameManager"
    private const val PREFS_CUSTOM_TITLES = "pref_app_custom_titles"

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(LauncherFiles.SHARED_PREFERENCES_KEY, Context.MODE_PRIVATE)
    }

    fun getCustomLabel(context: Context, componentName: ComponentName?): String? {
        if (componentName == null) return null
        val key = "${PREFS_CUSTOM_TITLES}_${componentName.flattenToString()}"
        return getPrefs(context).getString(key, null)
    }

    fun setCustomLabel(context: Context, componentName: ComponentName?, newLabel: String?) {
        if (componentName == null) return
        val key = "${PREFS_CUSTOM_TITLES}_${componentName.flattenToString()}"
        val editor = getPrefs(context).edit()
        if (newLabel.isNullOrBlank()) {
            editor.remove(key)
        } else {
            editor.putString(key, newLabel.trim())
        }
        editor.apply()

        // Force reload launcher model to apply renamed label
        try {
            LauncherAppState.getInstance(context).model.forceReload("AppRenameManager")
        } catch (e: Exception) {
            Log.e(TAG, "Error forceReloading after renaming: ${e.message}")
        }
    }

    fun showRenameDialog(context: Context, itemInfo: ItemInfo) {
        val componentName = itemInfo.targetComponent
        val currentLabel = getCustomLabel(context, componentName) ?: itemInfo.title?.toString() ?: ""

        val input = EditText(context).apply {
            setText(currentLabel)
            setSelection(currentLabel.length)
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_WORDS
            setSingleLine(true)
        }

        val container = FrameLayout(context).apply {
            val padding = (24 * context.resources.displayMetrics.density).toInt()
            setPadding(padding, padding / 2, padding, padding / 2)
            addView(input)
        }

        AlertDialog.Builder(context)
            .setTitle("Editar nombre de la app")
            .setView(container)
            .setPositiveButton("Guardar") { _, _ ->
                val text = input.text.toString().trim()
                setCustomLabel(context, componentName, text)
                Toast.makeText(context, "Nombre actualizado", Toast.LENGTH_SHORT).show()
            }
            .setNeutralButton("Restaurar") { _, _ ->
                setCustomLabel(context, componentName, null)
                Toast.makeText(context, "Nombre restaurado por defecto", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
}
