package com.android.launcher3.smartspace

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.android.launcher3.R
import com.android.launcher3.smartspace.weather.WeatherProvider
import com.android.launcher3.views.ActivityContext

/**
 * Pixel Smartspace container view (At a Glance).
 * Displays current date, weather, and upcoming events/alarms.
 */
class SmartspaceView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr), SmartspaceController.OnDataUpdatedListener {

    private val activityContext: ActivityContext? = ActivityContext.lookupContext<ActivityContext>(context)
    private var dateView: TextView? = null
    private var weatherChip: LinearLayout? = null
    private var weatherIcon: ImageView? = null
    private var weatherTemp: TextView? = null
    private var subtitleContainer: LinearLayout? = null
    private var subtitleIcon: ImageView? = null
    private var subtitleText: TextView? = null

    init {
        val view = LayoutInflater.from(context).inflate(R.layout.smartspace_enhanced, this, true)
        dateView = view.findViewById(R.id.smartspace_date)
        weatherChip = view.findViewById(R.id.smartspace_weather_chip)
        weatherIcon = view.findViewById(R.id.smartspace_weather_icon)
        weatherTemp = view.findViewById(R.id.smartspace_weather_temp)
        subtitleContainer = view.findViewById(R.id.smartspace_subtitle_container)
        subtitleIcon = view.findViewById(R.id.smartspace_subtitle_icon)
        subtitleText = view.findViewById(R.id.smartspace_subtitle_text)

        setupClickListeners()
    }

    private fun setupClickListeners() {
        dateView?.setOnClickListener {
            try {
                context.startActivity(SmartspaceData.getCalendarIntent())
            } catch (e: Exception) {
                // Fallback to opening Google Clock or default clock
                try {
                    val clockIntent = context.packageManager.getLaunchIntentForPackage("com.google.android.deskclock")
                        ?: context.packageManager.getLaunchIntentForPackage("com.android.deskclock")
                    if (clockIntent != null) {
                        context.startActivity(clockIntent)
                    }
                } catch (ignored: Exception) {
                }
            }
        }

        weatherChip?.setOnClickListener {
            WeatherProvider.openWeatherApp(context)
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        SmartspaceController.get(context).addListener(this)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        SmartspaceController.get(context).removeListener(this)
    }

    override fun onDataUpdated(data: SmartspaceData) {
        post {
            dateView?.text = data.dateText

            if (data.weatherData.isAvailable) {
                weatherChip?.visibility = View.VISIBLE
                weatherTemp?.text = data.weatherData.temperature
                weatherIcon?.setImageResource(data.weatherData.condition.iconRes)
            } else {
                weatherChip?.visibility = View.GONE
            }

            if (data.subtitleText != null) {
                subtitleContainer?.visibility = View.VISIBLE
                subtitleText?.text = data.subtitleText
                if (data.subtitleIconRes != null) {
                    subtitleIcon?.setImageResource(data.subtitleIconRes)
                }
                if (data.subtitleIntent != null) {
                    subtitleContainer?.setOnClickListener {
                        try {
                            context.startActivity(data.subtitleIntent)
                        } catch (ignored: Exception) {
                        }
                    }
                }
            } else {
                subtitleContainer?.visibility = View.GONE
            }
        }
    }
}
