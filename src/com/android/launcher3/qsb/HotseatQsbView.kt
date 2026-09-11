package com.android.launcher3.qsb

import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.speech.RecognizerIntent
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import com.android.launcher3.R

/**
 * Pixel Search Bar in the Hotseat Dock (Google G, Mic, Lens).
 */
class HotseatQsbView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val GSA_PACKAGE = "com.google.android.googlequicksearchbox"
    private val LENS_ACTIVITY = "com.google.android.apps.search.lens.LensLauncherActivity"

    override fun onFinishInflate() {
        super.onFinishInflate()

        val micIcon = findViewById<ImageView>(R.id.mic_icon)
        val lensIcon = findViewById<ImageView>(R.id.lens_icon)

        setOnClickListener {
            openGoogleSearch()
        }

        micIcon?.setOnClickListener {
            openVoiceSearch()
        }

        lensIcon?.setOnClickListener {
            openGoogleLens()
        }
    }

    private fun openGoogleSearch() {
        val prefs = context.getSharedPreferences(com.android.launcher3.LauncherFiles.SHARED_PREFERENCES_KEY, Context.MODE_PRIVATE)
        val engine = prefs.getString("pref_search_engine", "google") ?: "google"

        val searchUrl = when (engine) {
            "brave" -> "https://search.brave.com"
            "duckduckgo" -> "https://duckduckgo.com"
            "bing" -> "https://www.bing.com"
            else -> null
        }

        if (searchUrl != null) {
            try {
                val webSearch = Intent(Intent.ACTION_VIEW, Uri.parse(searchUrl)).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(webSearch)
                return
            } catch (ignored: Exception) {
            }
        }

        val searchIntent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
            component = ComponentName(GSA_PACKAGE, "com.google.android.googlequicksearchbox.SearchActivity")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED)
        }
        try {
            context.startActivity(searchIntent)
        } catch (e: ActivityNotFoundException) {
            val webSearch = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com")).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            try {
                context.startActivity(webSearch)
            } catch (ignored: Exception) {
            }
        }
    }

    private fun openVoiceSearch() {
        val voiceIntents = listOf(
            Intent(Intent.ACTION_VOICE_COMMAND).apply {
                setPackage(GSA_PACKAGE)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            },
            Intent(RecognizerIntent.ACTION_WEB_SEARCH).apply {
                setPackage(GSA_PACKAGE)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            },
            Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        )

        for (intent in voiceIntents) {
            try {
                if (intent.resolveActivity(context.packageManager) != null) {
                    context.startActivity(intent)
                    return
                }
            } catch (ignored: Exception) {
            }
        }
    }

    private fun openGoogleLens() {
        val lensIntents = listOf(
            Intent(Intent.ACTION_MAIN).apply {
                component = ComponentName(GSA_PACKAGE, LENS_ACTIVITY)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            },
            Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("googleapp://lens")
                setPackage(GSA_PACKAGE)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            },
            context.packageManager.getLaunchIntentForPackage("com.google.ar.lens")
        )

        for (intent in lensIntents) {
            if (intent != null) {
                try {
                    if (intent.resolveActivity(context.packageManager) != null) {
                        context.startActivity(intent)
                        return
                    }
                } catch (ignored: Exception) {
                }
            }
        }
    }
}
