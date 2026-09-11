package com.android.launcher3.settings

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
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
import com.android.launcher3.icons.IconPackInfo
import com.android.launcher3.icons.IconPackManager
import com.android.launcher3.settings.expressive.*

class IconPackSettingsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ExpressiveLauncherTheme {
                IconPackScreen(onBackClick = { finish() })
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IconPackScreen(onBackClick: () -> Unit) {
    val context = LocalContext.current
    var iconPacks by remember { mutableStateOf<List<IconPackInfo>>(emptyList()) }
    var selectedPack by remember { mutableStateOf(IconPackManager.getSelectedIconPack(context)) }

    LaunchedEffect(Unit) {
        val available = IconPackManager.getAvailableIconPacks(context)
        iconPacks = available
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Paquetes de Iconos",
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
                    text = "Elige un paquete de iconos instalado en el dispositivo para personalizar la apariencia de tus apps",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = M3OnSurfaceVariant,
                        fontSize = 14.sp
                    ),
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp)
                )

                // Opción: Por defecto del sistema
                val isDefault = selectedPack.isEmpty()
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            IconPackManager.setSelectedIconPack(context, "")
                            selectedPack = ""
                        }
                        .padding(horizontal = 24.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Por defecto (Sistema / Pixel)",
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = M3OnSurface,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Normal
                            )
                        )
                        Text(
                            text = "Iconos estándar adaptativos de Android",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = M3OnSurfaceVariant,
                                fontSize = 12.sp
                            )
                        )
                    }

                    if (isDefault) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Seleccionado",
                            tint = M3Primary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
                HorizontalDivider(color = M3SwitchTrackInactive.copy(alpha = 0.5f))
            }

            items(iconPacks) { pack ->
                val isSelected = selectedPack == pack.packageName

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            IconPackManager.setSelectedIconPack(context, pack.packageName)
                            selectedPack = pack.packageName
                        }
                        .padding(horizontal = 24.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    pack.icon?.let { d ->
                        val bmp = d.toBitmap(128, 128)
                        Image(
                            bitmap = bmp.asImageBitmap(),
                            contentDescription = pack.label,
                            modifier = Modifier.size(44.dp)
                        )
                    } ?: Spacer(modifier = Modifier.size(44.dp))

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = pack.label,
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = M3OnSurface,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Normal
                            )
                        )
                        Text(
                            text = pack.packageName,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = M3OnSurfaceVariant,
                                fontSize = 12.sp
                            )
                        )
                    }

                    if (isSelected) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Seleccionado",
                            tint = M3Primary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }
    }
}
