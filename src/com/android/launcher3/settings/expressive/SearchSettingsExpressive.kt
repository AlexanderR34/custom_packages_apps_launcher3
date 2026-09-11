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

class SearchSettingsExpressiveActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ExpressiveLauncherTheme {
                SearchSettingsExpressiveScreen(
                    onBackClick = { finish() }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchSettingsExpressiveScreen(onBackClick: () -> Unit) {
    val context = LocalContext.current
    val prefs = remember {
        context.getSharedPreferences(LauncherFiles.SHARED_PREFERENCES_KEY, Context.MODE_PRIVATE)
    }

    var alwaysShowKeyboard by remember { mutableStateOf(prefs.getBoolean("pref_always_show_keyboard", false)) }
    var webSearch by remember { mutableStateOf(prefs.getBoolean("pref_web_search", true)) }

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
                text = stringResource(R.string.search_settings_title),
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

            M3PreferenceRow(
                title = stringResource(R.string.always_show_keyboard_title),
                summary = stringResource(R.string.always_show_keyboard_summary),
                checked = alwaysShowKeyboard,
                onCheckedChange = {
                    alwaysShowKeyboard = it
                    prefs.edit().putBoolean("pref_always_show_keyboard", it).apply()
                }
            )

            M3PreferenceRow(
                title = stringResource(R.string.web_search_title),
                summary = stringResource(R.string.web_search_summary),
                checked = webSearch,
                onCheckedChange = {
                    webSearch = it
                    prefs.edit().putBoolean("pref_web_search", it).apply()
                }
            )

            M3PreferenceRow(
                title = stringResource(R.string.web_search_prefs_title),
                summary = stringResource(R.string.web_search_prefs_summary),
                onClick = { }
            )

            M3PreferenceRow(
                title = stringResource(R.string.control_search_results_title),
                summary = stringResource(R.string.control_search_results_summary),
                onClick = { }
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
