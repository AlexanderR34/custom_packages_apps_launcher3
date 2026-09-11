package com.android.launcher3.util

import android.content.Context
import android.graphics.Typeface
import com.android.launcher3.LauncherPrefs

object LauncherFontManager {
    const val PREF_LAUNCHER_FONT = "pref_launcher_font"

    @JvmStatic
    fun getTypeface(context: Context, defaultTypeface: Typeface? = null): Typeface {
        val fontPref = try {
            LauncherPrefs.getPrefs(context).getString(PREF_LAUNCHER_FONT, "system") ?: "system"
        } catch (e: Exception) {
            "system"
        }

        return when (fontPref) {
            "google_sans" -> Typeface.create("google-sans", Typeface.NORMAL)
            "nothing_ndot" -> Typeface.create("monospace", Typeface.NORMAL)
            "roboto" -> Typeface.create("sans-serif", Typeface.NORMAL)
            "inter" -> Typeface.create("sans-serif-medium", Typeface.NORMAL)
            else -> defaultTypeface ?: Typeface.DEFAULT
        }
    }

    @JvmStatic
    fun applyFont(textView: android.widget.TextView) {
        val tf = getTypeface(textView.context, textView.typeface)
        textView.typeface = tf
    }
}
