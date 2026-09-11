package com.android.launcher3.smartspace.weather

import androidx.annotation.DrawableRes
import com.android.launcher3.R

/**
 * Weather condition types mapping to Pixel Material You vector icons.
 */
enum class WeatherCondition(
    @DrawableRes val iconRes: Int,
    val description: String
) {
    SUNNY(R.drawable.ic_weather_sunny, "Sunny"),
    CLEAR_NIGHT(R.drawable.ic_weather_sunny, "Clear"),
    MOSTLY_SUNNY(R.drawable.ic_weather_sunny, "Mostly Sunny"),
    PARTLY_CLOUDY(R.drawable.ic_weather_cloudy, "Partly Cloudy"),
    CLOUDY(R.drawable.ic_weather_cloudy, "Cloudy"),
    RAIN(R.drawable.ic_weather_rain, "Rain"),
    SHOWERS(R.drawable.ic_weather_rain, "Showers"),
    THUNDERSTORM(R.drawable.ic_weather_thunderstorm, "Thunderstorm"),
    SNOW(R.drawable.ic_weather_snow, "Snow"),
    FOGGY(R.drawable.ic_weather_cloudy, "Foggy");

    companion object {
        fun fromConditionCode(code: Int): WeatherCondition {
            return when (code) {
                1, 2, 3 -> SUNNY
                4, 5, 6 -> MOSTLY_SUNNY
                7, 8, 9, 10, 11 -> CLOUDY
                12, 13, 14, 15, 16, 17, 18, 19, 20 -> RAIN
                21, 22, 23, 24, 25, 26, 27, 28 -> SNOW
                29, 30, 31, 32, 33, 34 -> THUNDERSTORM
                else -> SUNNY
            }
        }
    }
}
