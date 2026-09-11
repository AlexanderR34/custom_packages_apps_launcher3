package com.android.launcher3.smartspace

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothProfile
import android.content.Context
import android.util.Log

data class BluetoothBatteryInfo(
    val deviceName: String,
    val batteryPercent: Int
)

object BluetoothBatterySmartspaceCard {
    private const val TAG = "BtBatterySmartspace"

    fun getConnectedDeviceBattery(context: Context): BluetoothBatteryInfo? {
        return try {
            val adapter = BluetoothAdapter.getDefaultAdapter() ?: return null
            if (!adapter.isEnabled) return null

            val bondedDevices = adapter.bondedDevices ?: return null
            for (device in bondedDevices) {
                // Check if device is connected via isConnected reflection
                val isConnectedMethod = device.javaClass.getMethod("isConnected")
                val isConnected = isConnectedMethod.invoke(device) as? Boolean ?: false
                if (isConnected) {
                    val getBatteryLevelMethod = device.javaClass.getMethod("getBatteryLevel")
                    val batteryLevel = getBatteryLevelMethod.invoke(device) as? Int ?: -1
                    if (batteryLevel in 0..100) {
                        val name = device.alias ?: device.name ?: "Auriculares"
                        return BluetoothBatteryInfo(name, batteryLevel)
                    }
                }
            }
            null
        } catch (e: SecurityException) {
            null
        } catch (e: Exception) {
            Log.d(TAG, "Error checking bluetooth battery: ${e.message}")
            null
        }
    }
}
