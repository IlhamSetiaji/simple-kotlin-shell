package org.prasi.shell.bridges

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.webkit.WebView
import androidx.activity.result.ActivityResultLauncher
import androidx.core.content.FileProvider
import org.prasi.shell.MainActivity
import java.io.File
import java.util.UUID

class CameraHandler(private val activity: MainActivity) {

    var imagePath: String? = null

    fun launchCamera(cameraLauncher: ActivityResultLauncher<Intent>) {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val photoFile: File = createImageFile(activity)
        imagePath = photoFile.absolutePath

        val photoURI: Uri = FileProvider.getUriForFile(
            activity,
            "${activity.packageName}.fileprovider",
            photoFile
        )
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

        cameraLauncher.launch(intent)
    }

    fun handleCameraResult(resultCode: Int, fileMap: MutableMap<String, File>, imagePath: String?, webView: WebView) {
        if (resultCode == Activity.RESULT_OK && imagePath != null) {
            val imageFile = File(imagePath)
            val fileName = imageFile.name
            fileMap[fileName] = imageFile
            saveImageToGallery(activity, imageFile)

            val localUrl = "http://localhost:8080/$fileName"
            webView.evaluateJavascript("window.onCameraResult('$localUrl')", null)
        }
    }

    private fun createImageFile(context: Context): File {
        val storageDir: File = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        return File.createTempFile(
            "camera_image",  /* prefix */
            ".jpg",          /* suffix */
            storageDir       /* directory */
        )
    }

    private fun saveImageToGallery(context: Context, imageFile: File): String {
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, imageFile.name)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            put(MediaStore.MediaColumns.RELATIVE_PATH, "Pictures/MyApp")
        }
        val uri = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
        uri?.let {
            context.contentResolver.openOutputStream(it).use { outputStream ->
                imageFile.inputStream().copyTo(outputStream!!)
            }
        }
        return uri.toString()
    }
}