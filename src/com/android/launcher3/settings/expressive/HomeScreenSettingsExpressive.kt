package com.android.launcher3.settings.expressive

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.launcher3.LauncherFiles
import com.android.launcher3.R

class HomeScreenSettingsExpressiveActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ExpressiveLauncherTheme {
                HomeScreenSettingsExpressiveScreen(
                    onBackClick = { finish() }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenSettingsExpressiveScreen(onBackClick: () -> Unit) {
    val context = LocalContext.current
    val prefs = remember {
        context.getSharedPreferences(LauncherFiles.SHARED_PREFERENCES_KEY, Context.MODE_PRIVATE)
    }

    var notificationDots by remember { mutableStateOf(prefs.getBoolean("pref_icon_badging", true)) }
    var doubleTapToSleep by remember { mutableStateOf(prefs.getBoolean("pref_double_tap_to_sleep", true)) }
    var allAppsBlur by remember { mutableStateOf(prefs.getBoolean("pref_all_apps_blur_enabled", true)) }
    var dockBlur by remember { mutableStateOf(prefs.getBoolean("pref_dock_blur_enabled", true)) }
    var blurIntensity by remember {
        mutableStateOf(
            if (prefs.contains("pref_blur_intensity_seekbar")) {
                prefs.getInt("pref_blur_intensity_seekbar", 100) / 100f
            } else {
                prefs.getFloat("pref_blur_intensity", 1.0f)
            }
        )
    }
    var addIconsToHome by remember { mutableStateOf(prefs.getBoolean("pref_add_icon_to_home", true)) }
    var swipeToGoogle by remember { mutableStateOf(prefs.getBoolean("pref_swipe_to_google", true)) }
    var recentsSuggestions by remember { mutableStateOf(prefs.getBoolean("pref_recents_suggestions", true)) }
    val useAtAGlance = prefs.getBoolean("pref_use_at_a_glance", true)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
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
            Text(
                text = stringResource(R.string.home_settings_title),
                style = MaterialTheme.typography.displayLarge.copy(
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Normal,
                    color = M3OnBackground
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Puntos de notificación
            M3PreferenceRow(
                title = stringResource(R.string.notification_dots_title),
                summary = if (notificationDots) stringResource(R.string.notification_dots_summary_on) else stringResource(R.string.notification_dots_summary_off),
                onClick = {
                    notificationDots = !notificationDots
                    prefs.edit().putBoolean("pref_icon_badging", notificationDots).apply()
                }
            )

            // De un vistazo (with settings gear)
            M3PreferenceRow(
                title = stringResource(R.string.at_a_glance_title),
                summary = if (useAtAGlance) stringResource(R.string.at_a_glance_summary_yes) else stringResource(R.string.at_a_glance_summary_no),
                onClick = {
                    context.startActivity(Intent(context, AtAGlanceSettingsExpressiveActivity::class.java))
                },
                trailingContent = {
                    IconButton(onClick = {
                        context.startActivity(Intent(context, AtAGlanceSettingsExpressiveActivity::class.java))
                    }) {
                        Icon(
                            imageVector = Icons.Outlined.Settings,
                            contentDescription = stringResource(R.string.at_a_glance_title),
                            tint = M3OnBackground
                        )
                    }
                }
            )

            // Doble toque para suspender
            M3PreferenceRow(
                title = "Doble toque para suspender",
                summary = "Toca dos veces un espacio vacío en la pantalla de inicio para apagar la pantalla",
                checked = doubleTapToSleep,
                onCheckedChange = {
                    doubleTapToSleep = it
                    prefs.edit().putBoolean("pref_double_tap_to_sleep", it).apply()
                }
            )

            // Desenfoque del cajón de aplicaciones
            M3PreferenceRow(
                title = "Desenfoque en el cajón de apps",
                summary = "Aplica efecto de desenfoque (Blur) al fondo del cajón de aplicaciones",
                checked = allAppsBlur,
                onCheckedChange = {
                    allAppsBlur = it
                    prefs.edit().putBoolean("pref_all_apps_blur_enabled", it).apply()
                }
            )

            // Desenfoque del Dock
            M3PreferenceRow(
                title = "Desenfoque en el Dock",
                summary = "Efecto de desenfoque en la barra de búsqueda y hotseat",
                checked = dockBlur,
                onCheckedChange = {
                    dockBlur = it
                    prefs.edit().putBoolean("pref_dock_blur_enabled", it).apply()
                }
            )

            // Intensidad del desenfoque (Slider)
            M3SliderPreferenceRow(
                title = "Intensidad del desenfoque",
                summary = "Ajusta la fuerza del efecto Blur en el launcher",
                value = blurIntensity,
                onValueChange = {
                    blurIntensity = it
                    prefs.edit()
                        .putFloat("pref_blur_intensity", it)
                        .putInt("pref_blur_intensity_seekbar", (it * 100).toInt())
                        .apply()
                },
                valueRange = 0f..1.5f
            )

            // Agregar íconos de las apps a la pantalla principal
            M3PreferenceRow(
                title = stringResource(R.string.add_icon_to_home_title),
                summary = stringResource(R.string.add_icon_to_home_summary),
                checked = addIconsToHome,
                onCheckedChange = {
                    addIconsToHome = it
                    prefs.edit().putBoolean("pref_add_icon_to_home", it).apply()
                }
            )

            // Deslizar el dedo para acceder a la app de Google
            M3PreferenceRow(
                title = stringResource(R.string.swipe_to_google_app_title),
                summary = stringResource(R.string.swipe_to_google_app_summary),
                checked = swipeToGoogle,
                onCheckedChange = {
                    swipeToGoogle = it
                    prefs.edit().putBoolean("pref_swipe_to_google", it).apply()
                }
            )

            // Sugerencias en Recientes
            M3PreferenceRow(
                title = stringResource(R.string.recents_suggestions_title),
                summary = stringResource(R.string.recents_suggestions_summary),
                checked = recentsSuggestions,
                onCheckedChange = {
                    recentsSuggestions = it
                    prefs.edit().putBoolean("pref_recents_suggestions", it).apply()
                }
            )

            // Sugerencias
            M3PreferenceRow(
                title = stringResource(R.string.suggestions_title),
                summary = stringResource(R.string.suggestions_summary),
                onClick = {
                    context.startActivity(Intent(context, SuggestionsSettingsExpressiveActivity::class.java))
                }
            )

            // Configuración de búsqueda
            M3PreferenceRow(
                title = stringResource(R.string.search_settings_title),
                summary = stringResource(R.string.search_settings_summary),
                onClick = {
                    context.startActivity(Intent(context, SearchSettingsExpressiveActivity::class.java))
                }
            )

            // Configuración de la lista de apps
            M3PreferenceRow(
                title = stringResource(R.string.app_list_settings_title),
                onClick = {
                    context.startActivity(Intent(context, AppListSettingsExpressiveActivity::class.java))
                }
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
