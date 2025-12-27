package com.bearminds.bearguard.receiver

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import com.bearminds.bearguard.MainActivity
import com.bearminds.bearguard.rules.data.RulesRepository
import com.bearminds.bearguard.settings.data.SettingsRepository
import com.bearminds.bearguard.settings.SettingsContract.DefaultRule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

/**
 * BroadcastReceiver that handles package installation and uninstallation events.
 *
 * - On PACKAGE_ADDED: Shows a notification to configure rules for the new app
 * - On PACKAGE_REMOVED: Cleans up any existing rules for the uninstalled app
 */
class PackageChangeReceiver : BroadcastReceiver(), KoinComponent {

    companion object {
        private const val NOTIFICATION_CHANNEL_ID = "bearguard_new_app"
        private const val NOTIFICATION_ID_BASE = 10000
    }

    private val receiverScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onReceive(context: Context, intent: Intent) {
        val packageName = intent.data?.schemeSpecificPart ?: return

        // Ignore package changes for ourselves
        if (packageName == context.packageName) return

        when (intent.action) {
            Intent.ACTION_PACKAGE_ADDED -> {
                val isReplacing = intent.getBooleanExtra(Intent.EXTRA_REPLACING, false)
                if (!isReplacing) {
                    handleNewApp(context, packageName)
                }
            }
            Intent.ACTION_PACKAGE_REMOVED -> {
                val isReplacing = intent.getBooleanExtra(Intent.EXTRA_REPLACING, false)
                if (!isReplacing) {
                    handleAppRemoved(packageName)
                }
            }
        }
    }

    /**
     * Handle a newly installed app - show notification to configure rules.
     */
    private fun handleNewApp(context: Context, packageName: String) {
        val appName = getAppName(context, packageName)

        receiverScope.launch {
            val settingsRepository: SettingsRepository = get()
            val rulesRepository: RulesRepository = get()

            // Check if the default rule is to block new apps
            val defaultRule = settingsRepository.getDefaultRuleForNewApps()

            // Create a rule based on default settings
            val isAllowed = defaultRule == DefaultRule.ALLOW
            rulesRepository.saveRule(
                com.bearminds.bearguard.rules.model.Rule(
                    packageName = packageName,
                    isAllowed = isAllowed,
                    allowWifi = isAllowed,
                    allowMobileData = isAllowed
                )
            )

            // Show notification
            showNewAppNotification(context, packageName, appName, isAllowed)
        }
    }

    /**
     * Handle an uninstalled app - remove its rules.
     */
    private fun handleAppRemoved(packageName: String) {
        receiverScope.launch {
            val rulesRepository: RulesRepository = get()
            rulesRepository.deleteRule(packageName)
        }
    }

    /**
     * Get the human-readable app name from package name.
     */
    private fun getAppName(context: Context, packageName: String): String {
        return try {
            val packageManager = context.packageManager
            val appInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                packageManager.getApplicationInfo(
                    packageName,
                    PackageManager.ApplicationInfoFlags.of(0)
                )
            } else {
                @Suppress("DEPRECATION")
                packageManager.getApplicationInfo(packageName, 0)
            }
            packageManager.getApplicationLabel(appInfo).toString()
        } catch (e: Exception) {
            packageName
        }
    }

    /**
     * Show a notification about the new app.
     */
    private fun showNewAppNotification(
        context: Context,
        packageName: String,
        appName: String,
        isAllowed: Boolean
    ) {
        createNotificationChannel(context)

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("navigate_to", "apps")
            putExtra("highlight_package", packageName)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            packageName.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val statusText = if (isAllowed) {
            "Internet access allowed"
        } else {
            "Internet access blocked"
        }

        val notification = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
            .setContentTitle("New app: $appName")
            .setContentText(statusText)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(
            NOTIFICATION_ID_BASE + packageName.hashCode(),
            notification
        )
    }

    /**
     * Create the notification channel for new app notifications.
     */
    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "New App Notifications",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notifications about newly installed apps"
            }

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}
