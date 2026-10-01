package com.osm.frogedit

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform