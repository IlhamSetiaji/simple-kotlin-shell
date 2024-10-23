package org.prasi.shell.utils

import android.content.Context
import android.content.SharedPreferences

object VersionUtils {
    private const val PREFS_NAME = "app_prefs"
    private const val KEY_VERSION = "app_version"

    fun getCurrentVersion(context: Context): String {
        return context.packageManager.getPackageInfo(context.packageName, 0).versionName
    }

    fun getStoredVersion(context: Context): String? {
        val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_VERSION, null)
    }

    fun storeVersion(context: Context, version: String) {
        val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_VERSION, version).apply()
    }
}