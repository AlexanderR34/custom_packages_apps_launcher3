package com.android.launcher3.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.hardware.biometrics.BiometricManager
import android.hardware.biometrics.BiometricPrompt
import android.os.CancellationSignal
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import com.android.launcher3.LauncherPrefs

object AppLockManager {
    private const val PREF_KEY_LOCKED_APPS = "pref_locked_apps_list"

    @JvmStatic
    fun isAppLocked(context: Context, packageName: String?): Boolean {
        if (packageName.isNullOrEmpty()) return false
        val lockedSet = getLockedPackages(context)
        return lockedSet.contains(packageName)
    }

    @JvmStatic
    fun isIntentLocked(context: Context, intent: Intent?): Boolean {
        if (intent == null) return false
        val pkg = intent.component?.packageName ?: intent.`package`
        return isAppLocked(context, pkg)
    }

    @JvmStatic
    fun getLockedPackages(context: Context): Set<String> {
        val prefs = LauncherPrefs.getPrefs(context)
        val raw = prefs.getString(PREF_KEY_LOCKED_APPS, "") ?: ""
        if (raw.isEmpty()) return emptySet()
        return raw.split(",").filter { it.isNotEmpty() }.toSet()
    }

    @JvmStatic
    fun setAppLocked(context: Context, packageName: String, locked: Boolean) {
        val current = getLockedPackages(context).toMutableSet()
        if (locked) {
            current.add(packageName)
        } else {
            current.remove(packageName)
        }
        val prefs = LauncherPrefs.getPrefs(context)
        prefs.edit().putString(PREF_KEY_LOCKED_APPS, current.joinToString(",")).apply()
    }

    @JvmStatic
    fun toggleAppLock(context: Context, packageName: String): Boolean {
        val currentlyLocked = isAppLocked(context, packageName)
        val newState = !currentlyLocked
        setAppLocked(context, packageName, newState)
        return newState
    }

    @JvmStatic
    fun authenticateAndLaunch(
        activity: Activity,
        appLabel: String,
        onSuccess: Runnable
    ) {
        val mainHandler = Handler(Looper.getMainLooper())
        val cancellationSignal = CancellationSignal()

        try {
            val biometricPrompt = BiometricPrompt.Builder(activity)
                .setTitle("Desbloquear $appLabel")
                .setSubtitle("Usa tu huella dactilar o PIN para acceder")
                .setAllowedAuthenticators(
                    BiometricManager.Authenticators.BIOMETRIC_STRONG or
                    BiometricManager.Authenticators.DEVICE_CREDENTIAL
                )
                .build()

            biometricPrompt.authenticate(
                cancellationSignal,
                { command -> mainHandler.post(command) },
                object : BiometricPrompt.AuthenticationCallback() {
                    override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult?) {
                        super.onAuthenticationSucceeded(result)
                        mainHandler.post { onSuccess.run() }
                    }

                    override fun onAuthenticationError(errorCode: Int, errString: CharSequence?) {
                        super.onAuthenticationError(errorCode, errString)
                        mainHandler.post {
                            if (errorCode != BiometricPrompt.BIOMETRIC_ERROR_USER_CANCELED &&
                                errorCode != BiometricPrompt.BIOMETRIC_ERROR_CANCELED
                            ) {
                                Toast.makeText(activity, "Autenticación fallida: $errString", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            )
        } catch (e: Exception) {
            // If biometrics not set up or permission error, proceed
            mainHandler.post { onSuccess.run() }
        }
    }
}
