package com.android.launcher3.settings.expressive

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.launcher3.LauncherFiles
import com.android.launcher3.R

class SuggestionsSettingsExpressiveActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ExpressiveLauncherTheme {
                SuggestionsSettingsExpressiveScreen(
                    onBackClick = { finish() }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SuggestionsSettingsExpressiveScreen(onBackClick: () -> Unit) {
    val context = LocalContext.current
    val prefs = remember {
        context.getSharedPreferences(LauncherFiles.SHARED_PREFERENCES_KEY, Context.MODE_PRIVATE)
    }

    var suggestionsInAllApps by remember { mutableStateOf(prefs.getBoolean("pref_suggestions_in_all_apps", true)) }
    var suggestionsOnHome by remember { mutableStateOf(prefs.getBoolean("pref_suggestions_on_home", true)) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.suggestions_title),
                        style = MaterialTheme.typography.titleLarge.copy(
                            color = M3OnBackground,
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
            // Illustration Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(Color(0xFFF6F3FF))
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val strokeColor = Color(0xFFD6C8F5)
                    val purpleColor = Color(0xFF8B5CF6)

                    val phoneWidth = size.width * 0.45f
                    val phoneHeight = size.height * 0.85f
                    val phoneLeft = (size.width - phoneWidth) / 2
                    val phoneTop = size.height - phoneHeight

                    drawRoundRect(
                        color = Color.White,
                        topLeft = Offset(phoneLeft, phoneTop),
                        size = Size(phoneWidth, phoneHeight),
                        cornerRadius = CornerRadius(24f, 24f)
                    )

                    drawRoundRect(
                        color = strokeColor,
                        topLeft = Offset(phoneLeft, phoneTop),
                        size = Size(phoneWidth, phoneHeight),
                        cornerRadius = CornerRadius(24f, 24f),
                        style = Stroke(width = 4f)
                    )

                    drawRoundRect(
                        color = Color(0xFFEFEAF9),
                        topLeft = Offset(phoneLeft + 20f, size.height - 35f),
                        size = Size(phoneWidth - 40f, 20f),
                        cornerRadius = CornerRadius(10f, 10f)
                    )

                    val dotSpacing = (phoneWidth - 40f) / 6
                    for (i in 0..4) {
                        drawCircle(
                            color = purpleColor,
                            radius = 8f,
                            center = Offset(phoneLeft + 20f + (i + 1) * dotSpacing, size.height - 65f)
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .offset(y = (-20).dp)
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFDECFF8)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Home,
                        contentDescription = null,
                        tint = Color(0xFF7C3AED),
                        modifier = Modifier.size(36.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            M3CategoryHeader(text = stringResource(R.string.app_suggestions_header))

            M3GroupedPreferenceCard(position = CardPosition.TOP) {
                M3PreferenceRow(
                    title = stringResource(R.string.suggestions_in_all_apps_title),
                    checked = suggestionsInAllApps,
                    onCheckedChange = {
                        suggestionsInAllApps = it
                        prefs.edit().putBoolean("pref_suggestions_in_all_apps", it).apply()
                    }
                )
            }

            M3GroupedPreferenceCard(position = CardPosition.MIDDLE) {
                M3PreferenceRow(
                    title = stringResource(R.string.suggestions_on_home_title),
                    summary = stringResource(R.string.suggestions_on_home_summary),
                    checked = suggestionsOnHome,
                    onCheckedChange = {
                        suggestionsOnHome = it
                        prefs.edit().putBoolean("pref_suggestions_on_home", it).apply()
                    }
                )
            }

            M3GroupedPreferenceCard(position = CardPosition.BOTTOM) {
                M3PreferenceRow(
                    title = stringResource(R.string.blocked_apps_title),
                    onClick = { }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                shape = RoundedCornerShape(24.dp),
                color = M3CardBackground
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Info,
                        contentDescription = null,
                        tint = M3OnSurfaceVariant,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = stringResource(R.string.suggestions_info_footer),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = M3OnSurfaceVariant,
                            lineHeight = 20.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
