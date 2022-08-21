package com.rosebm.sharesheet

import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import timber.log.Timber

class ShareSheetBroadcastReceiver: BroadcastReceiver() {
    var appName = ""

    override fun onReceive(context: Context?, intent: Intent?) {
        val extra = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) Intent.EXTRA_CHOSEN_COMPONENT else ""
        val clickedComponent: ComponentName? = intent?.getParcelableExtra<ComponentName>(extra)
        val appNameSelected = getAppName(context?.packageManager, clickedComponent)

        context?.let {
            appName = context.getString(R.string.unknown_app)
        }

        Timber.tag("ShareSheetBroadcastReceiver").d("Sharing the link via: $appNameSelected")
    }

    private fun getAppName(packageManager: PackageManager?, clickedComponent: ComponentName?): String {
        try {
            appName = clickedComponent?.let { componentName ->
                    packageManager?.let {
                        val appInfo = packageManager.getApplicationInfo(
                            componentName.packageName, PackageManager.GET_META_DATA)
                        packageManager.getApplicationLabel(appInfo) as String
                    } ?: appName
            } ?: appName
        } catch (ex: Exception) {
            Timber.tag("ShareSheetBroadcastReceiver").e("Error: ${ex.localizedMessage}")
        }

        return appName
    }

}