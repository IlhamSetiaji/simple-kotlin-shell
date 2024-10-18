package org.prasi.shell

import android.content.Intent
import android.webkit.JavascriptInterface
import androidx.activity.result.ActivityResultLauncher
import org.prasi.shell.bridges.CameraHandler
import org.prasi.shell.bridges.CameraScannerHandler
import org.prasi.shell.bridges.FilePickerHandler

class WebAppInterface(private val activity: MainActivity,  private val cameraHandler: CameraHandler,
                      private val filePickerHandler: FilePickerHandler, private val scannerHandler: CameraScannerHandler, private val cameraLauncher: ActivityResultLauncher<Intent>, private val filePickerLauncher: ActivityResultLauncher<String>, private val scannerLauncher: ActivityResultLauncher<Intent>) {
    @JavascriptInterface
    fun openCamera() {
        cameraHandler.launchCamera(cameraLauncher)
    }

    @JavascriptInterface
    fun openFilePicker() {
        filePickerHandler.launchFilePicker(filePickerLauncher)
    }

    @JavascriptInterface
    fun openWithChrome(url: String) {
        activity.openWithChrome(url)
    }

    @JavascriptInterface
    fun openCameraScanner() {
        scannerHandler.launchCameraScanner(scannerLauncher)
    }
}
