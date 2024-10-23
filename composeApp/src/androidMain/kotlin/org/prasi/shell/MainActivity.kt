package org.prasi.shell

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.webkit.PermissionRequest
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import co.touchlab.kermit.Logger
import java.io.File
import org.prasi.shell.bridges.CameraHandler
import org.prasi.shell.bridges.CameraScannerHandler
import org.prasi.shell.bridges.FilePickerHandler
import android.widget.Toast
import android.Manifest

// class MainActivity : ComponentActivity() {

class MainActivity : AppCompatActivity() {
    private lateinit var webView: WebView
    private lateinit var cameraLauncher: ActivityResultLauncher<Intent>
    private lateinit var filePickerLauncher: ActivityResultLauncher<String>
    private lateinit var scannerLauncher: ActivityResultLauncher<Intent>
    private var localWebServer: LocalWebServer? = null
    private val fileMap = mutableMapOf<String, File>()
    private val cameraHandler = CameraHandler(this)
    private val filePickerHandler = FilePickerHandler(this)
    private val scannerHandler = CameraScannerHandler(this)
    private lateinit var context: Context
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            Toast.makeText(this, "Camera and file permission granted", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Camera and file permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        context = this

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED) {
        } else {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED) {
        } else {
            requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
        }

        cameraLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK && cameraHandler.imagePath != null) {
                Logger.i { "Camera result: $result" }
                cameraHandler.handleCameraResult(result.resultCode, fileMap, cameraHandler.imagePath, webView)
            }
        }

        filePickerLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            filePickerHandler.handleFilePicked(uri, fileMap, webView, context)
        }

        scannerLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            scannerHandler.handleScannerResult(result.resultCode, result.data, webView)
        }

        setupLocalWebServer()
        setupWebView(cameraLauncher, filePickerLauncher)
    }

    private fun setupWebView(cameraLauncher: ActivityResultLauncher<Intent>, filePickerLauncher: ActivityResultLauncher<String>) {
        webView = findViewById(R.id.webView)
        webView.clearCache(true)
        webView.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            allowFileAccess = true
            allowContentAccess = true
            pluginState = WebSettings.PluginState.ON
            allowFileAccessFromFileURLs = true
            allowUniversalAccessFromFileURLs = true
            mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
            mediaPlaybackRequiresUserGesture = false
        }
        webView.webChromeClient = object : WebChromeClient() {
            override fun onPermissionRequest(request: PermissionRequest) {
                val grantedPermissions = mutableListOf<String>()
                Logger.d { "onPermissionRequest received request for resources [${request.resources}]" }

                request.resources.forEach { resource ->
                    var androidPermission: String? = null

                    when (resource) {
                        PermissionRequest.RESOURCE_AUDIO_CAPTURE -> {
                            androidPermission = android.Manifest.permission.RECORD_AUDIO
                        }

                        PermissionRequest.RESOURCE_VIDEO_CAPTURE -> {
                            androidPermission = android.Manifest.permission.CAMERA
                        }

                        PermissionRequest.RESOURCE_MIDI_SYSEX -> {
                            // MIDI sysex is only available on Android M and above
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                                grantedPermissions.add(PermissionRequest.RESOURCE_MIDI_SYSEX)

                            }
                        }

                        PermissionRequest.RESOURCE_PROTECTED_MEDIA_ID -> {

                            grantedPermissions.add(PermissionRequest.RESOURCE_PROTECTED_MEDIA_ID)

                        }

                    }

                    if (androidPermission != null) {
                        if (ContextCompat.checkSelfPermission(context, androidPermission) == PackageManager.PERMISSION_GRANTED) {
                            grantedPermissions.add(resource)
                            Logger.d {
                                "onPermissionRequest permission [$androidPermission] was already granted for resource [$resource]"
                            }
                        } else {
                            Logger.w {
                                "onPermissionRequest didn't find already granted permission [$androidPermission] for resource [$resource]"
                            }
                            showPermissionSettingsDialog(context)
                        }
                    }
                }

                if (grantedPermissions.isNotEmpty()) {
                    request.grant(grantedPermissions.toTypedArray())
                    Logger.d { "onPermissionRequest granted permissions: ${grantedPermissions.joinToString()}" }
                } else {
                    request.deny()
                    Logger.d { "onPermissionRequest denied permissions: ${request.resources}" }
                }
            }
        }
        webView.webViewClient = WebViewClient()
        webView.addJavascriptInterface(WebAppInterface(this, cameraHandler, filePickerHandler, scannerHandler, cameraLauncher, filePickerLauncher, scannerLauncher), "AndroidBridge")
        webView.loadUrl("https://prasi.avolut.com/prod/02774758-4585-41e2-b74d-707329cce21d/home")
    }

    private fun setupLocalWebServer() {
        localWebServer = LocalWebServer(8080, fileMap)
        localWebServer?.start()
        loadSavedImages()
    }

    override fun onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack()
        } else {
            super.onBackPressed()
        }
    }
     fun openWithChrome(url: String) {
        Logger.i { "Opening URL: $url" }
        if (url.isNotEmpty()) {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("$url")).apply {
                setPackage("com.android.chrome")
            }
            try {
                startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                Logger.i(e) { "$e" }
                val fallbackIntent = Intent(Intent.ACTION_VIEW, Uri.parse("http://localhost:8080/$url"))
                startActivity(fallbackIntent)
            }
        } else {
            Logger.e { "Invalid URL: $url" }
        }
    }

    private fun loadSavedImages() {
        val storageDir: File = getExternalFilesDir(null)!!
        storageDir.listFiles()?.forEach { file ->
            if (file.extension == "jpg") {
                fileMap[file.name] = file
            }
        }
    }
}
