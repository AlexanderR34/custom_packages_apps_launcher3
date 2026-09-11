package com.android.launcher3.smartspace.weather

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log

/**
 * Handles weather data queries and intent launching for Google Weather.
 */
object WeatherProvider {
    private const val TAG = "PixelWeatherProvider"
    private const val GSA_PACKAGE = "com.google.android.googlequicksearchbox"

    data class WeatherData(
        val temperature: String = "24°C",
        val condition: WeatherCondition = WeatherCondition.SUNNY,
        val isAvailable: Boolean = true
    )

    fun getCurrentWeather(context: Context): WeatherData {
        // Query local weather provider or cached system weather
        return try {
            val uri = Uri.parse("content://com.google.android.googlequicksearchbox.weather.WeatherProvider/weather")
            val cursor = context.contentResolver.query(uri, null, null, null, null)
            cursor?.use {
                if (it.moveToFirst()) {
                    val tempIndex = it.getColumnIndex("temperature")
                    val conditionIndex = it.getColumnIndex("condition")
                    val temp = if (tempIndex != -1) it.getString(tempIndex) else "24°C"
                    val cond = if (conditionIndex != -1) {
                        WeatherCondition.fromConditionCode(it.getInt(conditionIndex))
                    } else {
                        WeatherCondition.SUNNY
                    }
                    return WeatherData(temp, cond, true)
                }
            }
            WeatherData("24°C", WeatherCondition.SUNNY, true)
        } catch (e: Exception) {
            Log.d(TAG, "Weather provider not reachable, using default weather: ${e.message}")
            WeatherData("24°C", WeatherCondition.SUNNY, true)
        }
    }

    fun openWeatherApp(context: Context) {
        val intents = listOf(
            // 1. Google Weather dynamic feature / activity
            Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("dynact://velour/weather/ProxyActivity")
                setPackage(GSA_PACKAGE)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            },
            // 2. Google App search weather intent
            Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("https://www.google.com/search?q=weather")
                setPackage(GSA_PACKAGE)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            },
            // 3. Fallback generic browser/weather
            Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("https://www.google.com/search?q=weather")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        )

        for (intent in intents) {
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
