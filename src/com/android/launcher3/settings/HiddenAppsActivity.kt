package com.android.launcher3.settings

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.LauncherApps
import android.os.Bundle
import android.os.Process
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
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
import com.android.launcher3.settings.expressive.*
import com.android.launcher3.util.HiddenAppsManager

data class AppItem(
    val componentName: ComponentName,
    val label: String,
    val iconBitmap: android.graphics.Bitmap?
)

class HiddenAppsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ExpressiveLauncherTheme {
                HiddenAppsScreen(onBackClick = { finish() })
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HiddenAppsScreen(onBackClick: () -> Unit) {
    val context = LocalContext.current
    var appsList by remember { mutableStateOf<List<AppItem>>(emptyList()) }
    var hiddenApps by remember { mutableStateOf(HiddenAppsManager.getHiddenApps(context)) }

    LaunchedEffect(Unit) {
        val launcherApps = context.getSystemService(LauncherApps::class.java)
        val userHandle = Process.myUserHandle()
        val activities = launcherApps?.getActivityList(null, userHandle) ?: emptyList()
        val items = activities.map { act ->
            val comp = act.componentName
            val label = act.label.toString()
            val icon = act.getBadgedIcon(0)
            AppItem(comp, label, icon?.toBitmap(128, 128))
        }.sortedBy { it.label }
        appsList = items
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Ocultar Aplicaciones",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = M3OnBackground
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Atrás",
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            item {
                Text(
                    text = "Selecciona las aplicaciones que deseas ocultar del cajón de aplicaciones",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = M3OnSurfaceVariant,
                        fontSize = 14.sp
                    ),
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp)
                )
            }

            items(appsList) { item ->
                val isHidden = hiddenApps.contains(item.componentName.flattenToString()) ||
                        hiddenApps.contains(item.componentName.packageName)

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            val newHidden = !isHidden
                            HiddenAppsManager.setAppHidden(context, item.componentName, newHidden)
                            hiddenApps = HiddenAppsManager.getHiddenApps(context)
                        }
                        .padding(horizontal = 24.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    item.iconBitmap?.let { bmp ->
                        Image(
                            bitmap = bmp.asImageBitmap(),
                            contentDescription = item.label,
                            modifier = Modifier.size(44.dp)
                        )
                    } ?: Spacer(modifier = Modifier.size(44.dp))

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = item.label,
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = M3OnSurface,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Normal
                            )
                        )
                        Text(
                            text = item.componentName.packageName,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = M3OnSurfaceVariant,
                                fontSize = 12.sp
                            )
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Checkbox(
                        checked = isHidden,
                        onCheckedChange = { checked ->
                            HiddenAppsManager.setAppHidden(context, item.componentName, checked)
                            hiddenApps = HiddenAppsManager.getHiddenApps(context)
                        },
                        colors = CheckboxDefaults.colors(
                            checkedColor = M3Primary,
                            uncheckedColor = M3OnSurfaceVariant
                        )
                    )
                }
            }
        }
    }
}
