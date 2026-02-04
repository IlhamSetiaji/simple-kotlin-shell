package org.prasi.shell

import fi.iki.elonen.NanoHTTPD
import java.io.File
import java.io.FileInputStream
import java.io.InputStream

class LocalWebServer(private val port: Int, private val fileMap: Map<String, File>) : NanoHTTPD(port) {
    override fun serve(session: IHTTPSession): Response {
        val uri = session.uri
        val file = fileMap[uri.substring(1)]

        return if (file != null && file.exists()) {
            val response = newFixedLengthResponse(Response.Status.OK, "image/jpeg", file.inputStream(), file.length())
            response.addHeader("Access-Control-Allow-Origin", "*")
            response
        } else {
            val response = newFixedLengthResponse(Response.Status.NOT_FOUND, MIME_PLAINTEXT, "File not found")
            response.addHeader("Access-Control-Allow-Origin", "*")
            response
        }
    }
}
