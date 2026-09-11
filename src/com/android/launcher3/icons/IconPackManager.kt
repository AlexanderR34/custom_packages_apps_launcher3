package com.android.launcher3.icons

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.content.res.Resources
import android.content.res.XmlResourceParser
import android.graphics.drawable.Drawable
import android.util.Log
import com.android.launcher3.LauncherAppState
import com.android.launcher3.LauncherFiles
import org.xmlpull.v1.XmlPullParser
import java.util.concurrent.ConcurrentHashMap

data class IconPackInfo(
    val packageName: String,
    val label: String,
    val icon: Drawable?
)

object IconPackManager {
    private const val TAG = "IconPackManager"
    const val KEY_SELECTED_ICON_PACK = "pref_selected_icon_pack"

    private val ICON_PACK_INTENTS = arrayOf(
        "org.adw.launcher.THEMES",
        "com.gau.go.launcherex.theme",
        "com.novalauncher.THEME",
        "com.teslacoilsw.launcher.THEME",
        "com.fede.launcher.THEME_ICONPACK",
        "app.lawnchair.icons.PACK"
    )

    private val iconPackAppFilter = ConcurrentHashMap<String, String>()
    private var currentLoadedPack: String? = null
    private var packResources: Resources? = null

    fun getAvailableIconPacks(context: Context): List<IconPackInfo> {
        val pm = context.packageManager
        val packages = mutableMapOf<String, IconPackInfo>()

        for (action in ICON_PACK_INTENTS) {
            val intent = Intent(action)
            val resolveInfos = pm.queryIntentActivities(intent, PackageManager.GET_META_DATA)
            for (info in resolveInfos) {
                val pkgName = info.activityInfo.packageName
                if (!packages.containsKey(pkgName)) {
                    val label = info.loadLabel(pm).toString()
                    val icon = info.loadIcon(pm)
                    packages[pkgName] = IconPackInfo(pkgName, label, icon)
                }
            }
        }
        return packages.values.sortedBy { it.label }
    }

    fun getSelectedIconPack(context: Context): String {
        return context.getSharedPreferences(LauncherFiles.SHARED_PREFERENCES_KEY, Context.MODE_PRIVATE)
            .getString(KEY_SELECTED_ICON_PACK, "") ?: ""
    }

    fun setSelectedIconPack(context: Context, packageName: String) {
        context.getSharedPreferences(LauncherFiles.SHARED_PREFERENCES_KEY, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_SELECTED_ICON_PACK, packageName)
            .apply()

        // Clear in-memory map
        iconPackAppFilter.clear()
        currentLoadedPack = null
        packResources = null

        // Reload icons in Launcher
        try {
            LauncherAppState.getInstance(context).iconCache.close()
            LauncherAppState.getInstance(context).model.forceReload("IconPackManager")
        } catch (e: Exception) {
            Log.e(TAG, "Error reloading launcher models: ${e.message}")
        }
    }

    @Synchronized
    private fun ensurePackLoaded(context: Context, packPackage: String) {
        if (currentLoadedPack == packPackage && packResources != null) return

        iconPackAppFilter.clear()
        currentLoadedPack = packPackage
        if (packPackage.isEmpty()) {
            packResources = null
            return
        }

        try {
            val pm = context.packageManager
            packResources = pm.getResourcesForApplication(packPackage)
            val resId = packResources?.getIdentifier("appfilter", "xml", packPackage) ?: 0
            if (resId != 0) {
                val parser: XmlResourceParser = packResources!!.getXml(resId)
                var eventType = parser.eventType
                while (eventType != XmlPullParser.END_DOCUMENT) {
                    if (eventType == XmlPullParser.START_TAG && parser.name == "item") {
                        val component = parser.getAttributeValue(null, "component")
                        val drawable = parser.getAttributeValue(null, "drawable")
                        if (component != null && drawable != null) {
                            // component is formatted like "ComponentInfo{com.pkg/com.pkg.Activity}"
                            val cleanComp = component.removePrefix("ComponentInfo{").removeSuffix("}")
                            iconPackAppFilter[cleanComp] = drawable
                        }
                    }
                    eventType = parser.next()
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to load icon pack resources for $packPackage", e)
            packResources = null
        }
    }

    fun getIconForComponent(context: Context, componentName: ComponentName): Drawable? {
        val selectedPack = getSelectedIconPack(context)
        if (selectedPack.isEmpty()) return null

        ensurePackLoaded(context, selectedPack)
        val res = packResources ?: return null

        val key = "${componentName.packageName}/${componentName.className}"
        val drawableName = iconPackAppFilter[key] ?: iconPackAppFilter[componentName.packageName] ?: return null

        val resId = res.getIdentifier(drawableName, "drawable", selectedPack)
        if (resId != 0) {
            return try {
                res.getDrawable(resId, null)
            } catch (e: Exception) {
                null
            }
        }
        return null
    }
}
