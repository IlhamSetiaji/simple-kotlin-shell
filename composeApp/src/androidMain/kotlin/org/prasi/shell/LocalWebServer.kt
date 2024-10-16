package org.prasi.shell

import fi.iki.elonen.NanoHTTPD
import java.io.File
import java.io.FileInputStream
import java.io.InputStream

class LocalWebServer(private val port: Int, private val fileMap: Map<String, File>) : NanoHTTPD(port) {
    override fun serve(session: IHTTPSession?): Response {
        val uri = session?.uri?.substring(1)
        val file = fileMap[uri]
        return if (file != null && file.exists()) {
            val fis: InputStream = FileInputStream(file)
            newChunkedResponse(Response.Status.OK, "image/jpeg", fis)
        } else {
            newFixedLengthResponse(Response.Status.NOT_FOUND, "text/plain", "File not found")
        }
    }
}
