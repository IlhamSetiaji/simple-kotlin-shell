package org.prasi.shell.utils

import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import java.io.File
import java.util.UUID

object FileUtils {
    fun saveUriToFile(context: Context, uri: Uri): File {
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

    fun saveImageToGallery(context: Context, imageFile: File): Uri {
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
        return uri ?: Uri.EMPTY
    }
}
