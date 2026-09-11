package com.android.launcher3.settings.expressive

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.launcher3.LauncherFiles
import com.android.launcher3.R

class AtAGlanceSettingsExpressiveActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ExpressiveLauncherTheme {
                AtAGlanceSettingsExpressiveScreen(
                    onBackClick = { finish() }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AtAGlanceSettingsExpressiveScreen(onBackClick: () -> Unit) {
    val context = LocalContext.current
    val prefs = remember {
        context.getSharedPreferences(LauncherFiles.SHARED_PREFERENCES_KEY, Context.MODE_PRIVATE)
    }

    var useAtAGlance by remember { mutableStateOf(prefs.getBoolean("pref_use_at_a_glance", true)) }
    var showOnHome by remember { mutableStateOf(prefs.getBoolean("pref_show_on_home", true)) }
    var highContrastBg by remember { mutableStateOf(prefs.getBoolean("pref_high_contrast_bg", false)) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.at_a_glance_title),
                        style = MaterialTheme.typography.titleLarge.copy(
                            color = M3OnBackground,
                            fontWeight = FontWeight.Normal,
                            fontSize = 22.sp
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = M3OnBackground
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = M3Background
                )
            )
        },
        containerColor = M3Background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            // Header Description
            Text(
                text = stringResource(R.string.at_a_glance_header_desc),
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = M3OnSurfaceVariant,
                    lineHeight = 20.sp
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 12.dp)
            )

            // Top Grouped Card (Switches)
            M3GroupedPreferenceCard(position = CardPosition.TOP) {
                M3PreferenceRow(
                    title = stringResource(R.string.use_at_a_glance_title),
                    checked = useAtAGlance,
                    onCheckedChange = {
                        useAtAGlance = it
                        prefs.edit().putBoolean("pref_use_at_a_glance", it).apply()
                    }
                )
            }
            M3GroupedPreferenceCard(position = CardPosition.MIDDLE) {
                M3PreferenceRow(
                    title = stringResource(R.string.show_on_home_title),
                    checked = showOnHome,
                    onCheckedChange = {
                        showOnHome = it
                        prefs.edit().putBoolean("pref_show_on_home", it).apply()
                    }
                )
            }
            M3GroupedPreferenceCard(position = CardPosition.BOTTOM) {
                M3PreferenceRow(
                    title = stringResource(R.string.high_contrast_bg_title),
                    checked = highContrastBg,
                    onCheckedChange = {
                        highContrastBg = it
                        prefs.edit().putBoolean("pref_high_contrast_bg", it).apply()
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Category: Administrar personalización
            M3CategoryHeader(text = stringResource(R.string.manage_customization_header))
            Text(
                text = stringResource(R.string.manage_customization_desc),
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = M3OnSurfaceVariant,
                    lineHeight = 20.sp
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 6.dp)
            )

            val mgmtItems = listOf(
                Pair(Icons.Outlined.StayCurrentPortrait, Pair(stringResource(R.string.sensitive_lockscreen_title), stringResource(R.string.sensitive_lockscreen_summary))),
                Pair(Icons.Outlined.Shield, Pair(stringResource(R.string.app_data_customization_title), stringResource(R.string.app_data_customization_summary))),
                Pair(Icons.Outlined.AccountCircle, Pair(stringResource(R.string.google_account_customization_title), stringResource(R.string.google_account_customization_summary))),
                Pair(Icons.Outlined.Security, Pair(stringResource(R.string.google_activity_title), stringResource(R.string.google_activity_summary))),
                Pair(Icons.Outlined.AutoAwesome, Pair(stringResource(R.string.assistant_results_title), stringResource(R.string.assistant_results_summary))),
                Pair(Icons.Outlined.HomeWork, Pair(stringResource(R.string.home_work_address_title), stringResource(R.string.home_work_address_summary))),
                Pair(Icons.Outlined.Email, Pair(stringResource(R.string.gmail_smart_features_title), stringResource(R.string.gmail_smart_features_summary))),
                Pair(Icons.Outlined.LocationOn, Pair(stringResource(R.string.location_title), stringResource(R.string.location_summary)))
            )

            mgmtItems.forEachIndexed { index, (icon, textPair) ->
                val pos = when {
                    mgmtItems.size == 1 -> CardPosition.SINGLE
                    index == 0 -> CardPosition.TOP
                    index == mgmtItems.size - 1 -> CardPosition.BOTTOM
                    else -> CardPosition.MIDDLE
                }
                M3GroupedPreferenceCard(position = pos) {
                    M3PreferenceRow(
                        title = textPair.first,
                        summary = textPair.second,
                        icon = icon,
                        onClick = { }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Category: Personalizar
            M3CategoryHeader(text = stringResource(R.string.customize_header))

            val customizeItems = listOf(
                GlanceToggleData("pref_toggle_weather", R.string.toggle_weather_title, R.string.toggle_weather_summary, Icons.Outlined.WbSunny),
                GlanceToggleData("pref_toggle_sports", R.string.toggle_sports_title, R.string.toggle_sports_summary, Icons.Outlined.SportsSoccer),
                GlanceToggleData("pref_toggle_finance", R.string.toggle_finance_title, R.string.toggle_finance_summary, Icons.Outlined.ShowChart),
                GlanceToggleData("pref_toggle_air_quality", R.string.toggle_air_quality_title, R.string.toggle_air_quality_summary, Icons.Outlined.Air),
                GlanceToggleData("pref_toggle_alerts", R.string.toggle_alerts_title, R.string.toggle_alerts_summary, Icons.Outlined.WarningAmber),
                GlanceToggleData("pref_toggle_earthquake", R.string.toggle_earthquake_title, R.string.toggle_earthquake_summary, Icons.Outlined.WarningAmber),
                GlanceToggleData("pref_toggle_upcoming", R.string.toggle_upcoming_title, R.string.toggle_upcoming_summary, Icons.Outlined.Update),
                GlanceToggleData("pref_toggle_work_profile", R.string.toggle_work_profile_title, R.string.toggle_work_profile_summary, Icons.Outlined.WorkOutline),
                GlanceToggleData("pref_toggle_food_orders", R.string.toggle_food_orders_title, R.string.toggle_food_orders_summary, Icons.Outlined.Fastfood),
                GlanceToggleData("pref_toggle_packages", R.string.toggle_packages_title, R.string.toggle_packages_summary, Icons.Outlined.LocalShipping),
                GlanceToggleData("pref_toggle_commute", R.string.toggle_commute_title, R.string.toggle_commute_summary, Icons.Outlined.DirectionsCar),
                GlanceToggleData("pref_toggle_departure_time", R.string.toggle_departure_time_title, R.string.toggle_departure_time_summary, Icons.Outlined.TimeToLeave),
                GlanceToggleData("pref_toggle_rideshare", R.string.toggle_rideshare_title, R.string.toggle_rideshare_summary, Icons.Outlined.LocalTaxi),
                GlanceToggleData("pref_toggle_trips", R.string.toggle_trips_title, R.string.toggle_trips_summary, Icons.Outlined.Flight),
                GlanceToggleData("pref_toggle_bedtime", R.string.toggle_bedtime_title, R.string.toggle_bedtime_summary, Icons.Outlined.NightsStay),
                GlanceToggleData("pref_toggle_fitness", R.string.toggle_fitness_title, R.string.toggle_fitness_summary, Icons.Outlined.FitnessCenter),
                GlanceToggleData("pref_toggle_safety", R.string.toggle_safety_title, R.string.toggle_safety_summary, Icons.Outlined.HealthAndSafety),
                GlanceToggleData("pref_toggle_timer", R.string.toggle_timer_title, R.string.toggle_timer_summary, Icons.Outlined.Timer),
                GlanceToggleData("pref_toggle_connected_devices", R.string.toggle_connected_devices_title, R.string.toggle_connected_devices_summary, Icons.Outlined.Headphones),
                GlanceToggleData("pref_toggle_multi_device_timer", R.string.toggle_multi_device_timer_title, R.string.toggle_multi_device_timer_summary, Icons.Outlined.DevicesOther),
                GlanceToggleData("pref_toggle_doorbell", R.string.toggle_doorbell_title, R.string.toggle_doorbell_summary, Icons.Outlined.Doorbell),
                GlanceToggleData("pref_toggle_flashlight", R.string.toggle_flashlight_title, R.string.toggle_flashlight_summary, Icons.Outlined.FlashlightOn)
            )

            customizeItems.forEachIndexed { index, item ->
                var isChecked by remember { mutableStateOf(prefs.getBoolean(item.key, true)) }
                val pos = when {
                    index == 0 -> CardPosition.TOP
                    index == customizeItems.size - 1 -> CardPosition.BOTTOM
                    else -> CardPosition.MIDDLE
                }
                M3GroupedPreferenceCard(position = pos) {
                    M3PreferenceRow(
                        title = stringResource(item.titleRes),
                        summary = stringResource(item.summaryRes),
                        icon = item.icon,
                        checked = isChecked,
                        onCheckedChange = {
                            isChecked = it
                            prefs.edit().putBoolean(item.key, it).apply()
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

data class GlanceToggleData(
    val key: String,
    val titleRes: Int,
    val summaryRes: Int,
    val icon: ImageVector
)
