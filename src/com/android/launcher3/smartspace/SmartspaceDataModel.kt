package com.android.launcher3.smartspace

import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.CalendarContract
import android.text.format.DateFormat
import com.android.launcher3.smartspace.weather.WeatherCondition
import com.android.launcher3.smartspace.weather.WeatherProvider
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/**
 * Data model for Google At a Glance (Smartspace).
 */
data class SmartspaceData(
    val dateText: String,
    val weatherData: WeatherProvider.WeatherData,
    val subtitleText: String? = null,
    val subtitleIconRes: Int? = null,
    val subtitleIntent: Intent? = null
) {
    companion object {
        fun createCurrent(context: Context): SmartspaceData {
            val now = System.currentTimeMillis()
            val cal = Calendar.getInstance()
            cal.timeInMillis = now

            // Localized Google Pixel Date format, e.g. "Wed, Sep 10" or "Mié, 10 de sep"
            val formatPattern = DateFormat.getBestDateTimePattern(Locale.getDefault(), "EEEEMMMMd")
            val sdf = SimpleDateFormat(formatPattern, Locale.getDefault())
            val dateFormatted = sdf.format(cal.time)

            // Capitalize first letter if lowercase
            val capitalizedDate = dateFormatted.replaceFirstChar {
                if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
            }

            val weather = WeatherProvider.getCurrentWeather(context)
            val mediaTrack = MediaSmartspaceCard.getActiveTrack(context)
            val btBattery = if (mediaTrack == null || !mediaTrack.isPlaying) {
                BluetoothBatterySmartspaceCard.getConnectedDeviceBattery(context)
            } else null

            val subtitleText: String?
            val subtitleIconRes: Int?
            val subtitleIntent: Intent?

            if (mediaTrack != null && mediaTrack.isPlaying) {
                subtitleText = if (mediaTrack.artist.isNullOrEmpty()) mediaTrack.title else "${mediaTrack.title} • ${mediaTrack.artist}"
                subtitleIconRes = com.android.launcher3.R.drawable.ic_smartspace_alarm
                subtitleIntent = context.packageManager.getLaunchIntentForPackage(mediaTrack.packageName)
            } else if (btBattery != null) {
                subtitleText = "${btBattery.deviceName} • ${btBattery.batteryPercent}%"
                subtitleIconRes = com.android.launcher3.R.drawable.ic_smartspace_alarm
                subtitleIntent = Intent(android.provider.Settings.ACTION_BLUETOOTH_SETTINGS)
            } else {
                subtitleText = null
                subtitleIconRes = null
                subtitleIntent = null
            }

            return SmartspaceData(
                dateText = capitalizedDate,
                weatherData = weather,
                subtitleText = subtitleText,
                subtitleIconRes = subtitleIconRes,
                subtitleIntent = subtitleIntent
            )
        }

        fun getCalendarIntent(): Intent {
            val startMillis = System.currentTimeMillis()
            val builder = CalendarContract.CONTENT_URI.buildUpon()
            builder.appendPath("time")
            ContentUris.appendId(builder, startMillis)
            return Intent(Intent.ACTION_VIEW).apply {
                data = builder.build()
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED)
            }
        }
    }
}
