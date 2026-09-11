package com.android.launcher3.settings

import android.content.pm.LauncherApps
import android.os.Bundle
import android.os.Process
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import com.android.launcher3.settings.expressive.ExpressiveLauncherTheme
import com.android.launcher3.settings.expressive.M3Background
import com.android.launcher3.settings.expressive.M3OnBackground
import com.android.launcher3.util.AppLockManager

class AppLockSettingsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ExpressiveLauncherTheme {
                AppLockScreen(onBackClick = { finish() })
            }
        }
    }
}

private data class LockableApp(
    val packageName: String,
    val label: String,
    val icon: android.graphics.drawable.Drawable?
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppLockScreen(onBackClick: () -> Unit) {
    val context = LocalContext.current
    var lockedApps by remember { mutableStateOf(AppLockManager.getLockedPackages(context)) }
    var appsList by remember { mutableStateOf<List<LockableApp>>(emptyList()) }

    LaunchedEffect(Unit) {
        val launcherApps = context.getSystemService(LauncherApps::class.java)
        val activities = launcherApps.getActivityList(null, Process.myUserHandle())
        val list = activities.map { info ->
            LockableApp(
                packageName = info.applicationInfo.packageName,
                label = info.label.toString(),
                icon = info.getIcon(0)
            )
        }.sortedBy { it.label.lowercase() }
        appsList = list
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Bloqueo de aplicaciones",
                        color = M3OnBackground,
                        fontWeight = FontWeight.SemiBold
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
        ) {
            Text(
                text = "Protege el acceso a tus aplicaciones seleccionadas solicitando tu huella dactilar o PIN al abrirlas.",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 14.sp,
                    color = M3OnBackground.copy(alpha = 0.7f)
                ),
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp)
            )

            HorizontalDivider(color = M3OnBackground.copy(alpha = 0.1f))

            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(appsList, key = { it.packageName }) { app ->
                    val isLocked = lockedApps.contains(app.packageName)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        app.icon?.let { drawable ->
                            Image(
                                bitmap = remember(drawable) { drawable.toBitmap(48, 48).asImageBitmap() },
                                contentDescription = app.label,
                                modifier = Modifier.size(40.dp)
                            )
                        } ?: Spacer(modifier = Modifier.size(40.dp))

                        Spacer(modifier = Modifier.width(16.dp))

                        Text(
                            text = app.label,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                color = M3OnBackground,
                                fontWeight = FontWeight.Medium
                            ),
                            modifier = Modifier.weight(1f)
                        )

                        Switch(
                            checked = isLocked,
                            onCheckedChange = { checked ->
                                AppLockManager.setAppLocked(context, app.packageName, checked)
                                lockedApps = AppLockManager.getLockedPackages(context)
                            }
                        )
                    }
                }
            }
        }
    }
}
