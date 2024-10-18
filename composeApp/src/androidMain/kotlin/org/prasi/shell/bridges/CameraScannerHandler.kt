package org.prasi.shell.bridges

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.webkit.WebView
import androidx.activity.result.ActivityResultLauncher
import com.google.zxing.integration.android.IntentIntegrator
import org.prasi.shell.MainActivity

class CameraScannerHandler(private val activity: MainActivity) {

    fun launchCameraScanner(scannerLauncher: ActivityResultLauncher<Intent>) {
        val integrator = IntentIntegrator(activity).apply {
            setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES)
            setPrompt("Scan a QR code or barcode")
            setCameraId(0)
            setBeepEnabled(true)
            setBarcodeImageEnabled(true)
        }
        integrator.initiateScan()
    }

    fun handleScannerResult(resultCode: Int, data: Intent?, webView: WebView) {
        val result = IntentIntegrator.parseActivityResult(resultCode, data)
        if (result != null && result.contents != null) {
            val scannedData = result.contents
            webView.evaluateJavascript("window.onScannerResult('$scannedData')", null)
        } else {
            webView.evaluateJavascript("window.onScannerResult(null)", null)
        }
    }
}
