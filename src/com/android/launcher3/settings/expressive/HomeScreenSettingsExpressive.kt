package com.android.launcher3.settings.expressive

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
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
    var wallpaperScrolling by remember { mutableStateOf(prefs.getBoolean("pref_wallpaper_scrolling", true)) }
    var allAppsBlur by remember { mutableStateOf(prefs.getBoolean("pref_all_apps_blur_enabled", true)) }
    var dockBlur by remember { mutableStateOf(prefs.getBoolean("pref_dock_blur_enabled", true)) }
    var dockShelf by remember { mutableStateOf(prefs.getBoolean("pref_dock_shelf_enabled", false)) }
    var drawerOpacity by remember { mutableStateOf(prefs.getInt("pref_drawer_opacity", 100) / 100f) }
    var animationSpeed by remember { mutableStateOf(prefs.getString("pref_animation_speed", "1.0") ?: "1.0") }
    var iconShape by remember { mutableStateOf(prefs.getString("pref_icon_shape", "system") ?: "system") }
    var launcherFont by remember { mutableStateOf(prefs.getString("pref_launcher_font", "system") ?: "system") }
    var forceMonochrome by remember { mutableStateOf(prefs.getBoolean("pref_force_monochrome_icons", true)) }

    var showSpeedDialog by remember { mutableStateOf(false) }
    var showShapeDialog by remember { mutableStateOf(false) }
    var showFontDialog by remember { mutableStateOf(false) }

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

            // Velocidad de animaciones
            val speedLabel = when (animationSpeed) {
                "0.5" -> "0.5x (Ultra Rápido)"
                "0.75" -> "0.75x (Rápido)"
                "1.0" -> "1.0x (Por defecto)"
                "1.25" -> "1.25x (Relajado)"
                "1.5" -> "1.5x (Lento)"
                else -> "$animationSpeed x"
            }
            M3PreferenceRow(
                title = "Velocidad de animaciones",
                summary = speedLabel,
                onClick = { showSpeedDialog = true }
            )

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

            // Desplazamiento del fondo de pantalla
            M3PreferenceRow(
                title = "Desplazamiento del fondo de pantalla",
                summary = "Mueve el fondo suavemente entre escritorios",
                checked = wallpaperScrolling,
                onCheckedChange = {
                    wallpaperScrolling = it
                    prefs.edit().putBoolean("pref_wallpaper_scrolling", it).apply()
                }
            )

            // Deslizar abajo para notificaciones
            var swipeDownNotifications by remember { mutableStateOf(prefs.getBoolean("pref_swipe_down_notifications", true)) }
            M3PreferenceRow(
                title = "Deslizar abajo para notificaciones",
                summary = "Desliza hacia abajo en la pantalla de inicio para abrir el panel de notificaciones y ajustes rápidos",
                checked = swipeDownNotifications,
                onCheckedChange = {
                    swipeDownNotifications = it
                    prefs.edit().putBoolean("pref_swipe_down_notifications", it).apply()
                }
            )

            // Bloquear diseño del escritorio
            var lockDesktop by remember { mutableStateOf(prefs.getBoolean("pref_lock_desktop", false)) }
            M3PreferenceRow(
                title = "Bloquear diseño del escritorio",
                summary = "Evita mover, arrastrar o eliminar iconos y widgets en la pantalla de inicio",
                checked = lockDesktop,
                onCheckedChange = {
                    lockDesktop = it
                    prefs.edit().putBoolean("pref_lock_desktop", it).apply()
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

            // Opacidad del fondo del cajón
            M3SliderPreferenceRow(
                title = "Opacidad del fondo del cajón",
                summary = "Ajusta la transparencia del fondo en el cajón de aplicaciones",
                value = drawerOpacity,
                onValueChange = {
                    drawerOpacity = it
                    prefs.edit().putInt("pref_drawer_opacity", (it * 100).toInt()).apply()
                },
                valueRange = 0f..1.0f
            )

            // Fondo de cristal en el Dock
            M3PreferenceRow(
                title = "Fondo de cristal en el Dock (Glass Dock)",
                summary = "Panel translúcido esmerilado detrás de los iconos inferiores",
                checked = dockShelf,
                onCheckedChange = {
                    dockShelf = it
                    prefs.edit().putBoolean("pref_dock_shelf_enabled", it).apply()
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

            // Fuente del launcher
            val fontLabel = when (launcherFont) {
                "google_sans" -> "Google Sans (Pixel)"
                "nothing_ndot" -> "Nothing NDOT (Nothing OS)"
                "roboto" -> "Roboto"
                "inter" -> "Inter Medium"
                else -> "Por defecto del sistema"
            }
            M3PreferenceRow(
                title = "Fuente del launcher",
                summary = fontLabel,
                onClick = { showFontDialog = true }
            )

            // Forma de los iconos
            val shapeLabel = when (iconShape) {
                "circle" -> "Círculo (Circle)"
                "squircle" -> "Squircle (Nothing OS / OneUI)"
                "rounded_square" -> "Cuadrado redondeado"
                "teardrop" -> "Lágrima (Teardrop)"
                else -> "Por defecto del sistema"
            }
            M3PreferenceRow(
                title = "Forma de los iconos",
                summary = shapeLabel,
                onClick = { showShapeDialog = true }
            )

            // Forzar iconos temáticos (Monet)
            M3PreferenceRow(
                title = "Forzar iconos temáticos (Monet)",
                summary = "Aplica color dinámico Material You a todas las aplicaciones",
                checked = forceMonochrome,
                onCheckedChange = {
                    forceMonochrome = it
                    prefs.edit().putBoolean("pref_force_monochrome_icons", it).apply()
                }
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

            // Paquetes de iconos
            M3PreferenceRow(
                title = "Paquetes de iconos",
                summary = "Personaliza los iconos con packs de terceros instalados",
                onClick = {
                    context.startActivity(Intent(context, com.android.launcher3.settings.IconPackSettingsActivity::class.java))
                }
            )

            // Bloqueo de aplicaciones
            M3PreferenceRow(
                title = "Bloqueo de aplicaciones",
                summary = "Protege aplicaciones con huella dactilar o PIN",
                onClick = {
                    context.startActivity(Intent(context, com.android.launcher3.settings.AppLockSettingsActivity::class.java))
                }
            )

            // Ocultar aplicaciones
            M3PreferenceRow(
                title = "Ocultar aplicaciones",
                summary = "Gestiona las aplicaciones ocultas del cajón",
                onClick = {
                    context.startActivity(Intent(context, com.android.launcher3.settings.HiddenAppsActivity::class.java))
                }
            )

            // Conteo numérico de notificaciones
            var numberedBadges by remember { mutableStateOf(prefs.getBoolean("pref_numbered_badges_enabled", true)) }
            M3PreferenceRow(
                title = "Conteo numérico de notificaciones",
                summary = "Muestra el número de notificaciones no leídas en lugar de un punto",
                checked = numberedBadges,
                onCheckedChange = {
                    numberedBadges = it
                    prefs.edit().putBoolean("pref_numbered_badges_enabled", it).apply()
                }
            )

            // Ocultar nombres en escritorio
            var hideWorkspaceLabels by remember { mutableStateOf(prefs.getBoolean("pref_hide_workspace_labels", false)) }
            M3PreferenceRow(
                title = "Ocultar nombres en inicio",
                summary = "Oculta el texto de los iconos en el escritorio",
                checked = hideWorkspaceLabels,
                onCheckedChange = {
                    hideWorkspaceLabels = it
                    prefs.edit().putBoolean("pref_hide_workspace_labels", it).apply()
                }
            )

            // Ocultar nombres en el Dock
            var hideDockLabels by remember { mutableStateOf(prefs.getBoolean("pref_hide_dock_labels", true)) }
            M3PreferenceRow(
                title = "Ocultar nombres en el Dock",
                summary = "Oculta el texto de los iconos en el Hotseat",
                checked = hideDockLabels,
                onCheckedChange = {
                    hideDockLabels = it
                    prefs.edit().putBoolean("pref_hide_dock_labels", it).apply()
                }
            )

            // Carpetas grandes 2x2
            var bigFolders by remember { mutableStateOf(prefs.getBoolean("pref_big_folders_enabled", true)) }
            M3PreferenceRow(
                title = "Carpetas grandes 2x2",
                summary = "Carpetas ampliadas con acceso directo estilo Nothing OS y HyperOS",
                checked = bigFolders,
                onCheckedChange = {
                    bigFolders = it
                    prefs.edit().putBoolean("pref_big_folders_enabled", it).apply()
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

        // Diálogo de fuentes
        if (showFontDialog) {
            val fonts = listOf(
                "system" to "Por defecto del sistema",
                "google_sans" to "Google Sans (Pixel)",
                "nothing_ndot" to "Nothing NDOT (Nothing OS)",
                "roboto" to "Roboto",
                "inter" to "Inter Medium"
            )
            AlertDialog(
                onDismissRequest = { showFontDialog = false },
                title = { Text("Fuente del launcher") },
                text = {
                    Column {
                        fonts.forEach { (value, label) ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        launcherFont = value
                                        prefs.edit().putString("pref_launcher_font", value).apply()
                                        showFontDialog = false
                                    }
                                    .padding(vertical = 12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(label, color = M3OnBackground)
                                RadioButton(
                                    selected = (launcherFont == value),
                                    onClick = {
                                        launcherFont = value
                                        prefs.edit().putString("pref_launcher_font", value).apply()
                                        showFontDialog = false
                                    }
                                )
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showFontDialog = false }) {
                        Text("Cerrar")
                    }
                }
            )
        }

        // Diálogo de velocidad de animaciones
        if (showSpeedDialog) {
            val speeds = listOf(
                "0.5" to "0.5x (Ultra Rápido)",
                "0.75" to "0.75x (Rápido)",
                "1.0" to "1.0x (Por defecto)",
                "1.25" to "1.25x (Relajado)",
                "1.5" to "1.5x (Lento)"
            )
            AlertDialog(
                onDismissRequest = { showSpeedDialog = false },
                title = { Text("Velocidad de animaciones") },
                text = {
                    Column {
                        speeds.forEach { (value, label) ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        animationSpeed = value
                                        prefs.edit().putString("pref_animation_speed", value).apply()
                                        showSpeedDialog = false
                                    }
                                    .padding(vertical = 12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(label, color = M3OnBackground)
                                RadioButton(
                                    selected = (animationSpeed == value),
                                    onClick = {
                                        animationSpeed = value
                                        prefs.edit().putString("pref_animation_speed", value).apply()
                                        showSpeedDialog = false
                                    }
                                )
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showSpeedDialog = false }) {
                        Text("Cerrar")
                    }
                }
            )
        }

        // Diálogo de forma de iconos
        if (showShapeDialog) {
            val shapes = listOf(
                "system" to "Por defecto del sistema",
                "circle" to "Círculo (Circle)",
                "squircle" to "Squircle (Nothing OS / OneUI)",
                "rounded_square" to "Cuadrado redondeado",
                "teardrop" to "Lágrima (Teardrop)"
            )
            AlertDialog(
                onDismissRequest = { showShapeDialog = false },
                title = { Text("Forma de los iconos") },
                text = {
                    Column {
                        shapes.forEach { (value, label) ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        iconShape = value
                                        prefs.edit().putString("pref_icon_shape", value).apply()
                                        showShapeDialog = false
                                    }
                                    .padding(vertical = 12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(label, color = M3OnBackground)
                                RadioButton(
                                    selected = (iconShape == value),
                                    onClick = {
                                        iconShape = value
                                        prefs.edit().putString("pref_icon_shape", value).apply()
                                        showShapeDialog = false
                                    }
                                )
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showShapeDialog = false }) {
                        Text("Cerrar")
                    }
                }
            )
        }
    }
}
