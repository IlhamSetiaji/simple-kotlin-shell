package org.prasi.shell.bridges

import android.content.Context
import android.net.Uri
import android.webkit.WebView
import androidx.activity.result.ActivityResultLauncher
import org.prasi.shell.MainActivity
import java.io.File
import java.util.UUID

class FilePickerHandler(private val activity: MainActivity) {

    fun launchFilePicker(filePickerLauncher: ActivityResultLauncher<String>) {
        filePickerLauncher.launch("image/*")
    }

    fun handleFilePicked(uri: Uri?, fileMap: MutableMap<String, File>, webView: WebView, context: Context) {
        uri?.let {
            val file = saveUriToFile(context, it)
            val fileName = file.name
            fileMap[fileName] = file

            val localUrl = "http://localhost:8080/$fileName"
            webView.evaluateJavascript("window.onFilePicked('$localUrl')", null)
        }
    }

    private fun saveUriToFile(context: Context, uri: Uri): File {
        val inputStream = context.contentResolver.openInputStream(uri)
        val storageDir: File = context.getExternalFilesDir(null)!!
        val fileName = UUID.randomUUID().toString() + ".jpg"
        val file = File(storageDir, fileName)
        inputStream.use { input ->
            file.outputStream().use { output ->
                input?.copyTo(output)
            }
        }
        return file
    }
}