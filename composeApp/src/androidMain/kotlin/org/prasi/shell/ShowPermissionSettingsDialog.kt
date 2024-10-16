package org.prasi.shell

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.appcompat.app.AlertDialog

fun showPermissionSettingsDialog(context: Context) {
    AlertDialog.Builder(context)
        .setTitle("Permission Required")
        .setMessage("Camera permission is required to use this feature. Please enable it in the app settings and then reload the application")
        .setPositiveButton("Go to Settings") { _, _ ->
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.fromParts("package", context.packageName, null)
            }
            context.startActivity(intent)
        }
        .setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }
        .show()
}