package org.prasi.shell

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform