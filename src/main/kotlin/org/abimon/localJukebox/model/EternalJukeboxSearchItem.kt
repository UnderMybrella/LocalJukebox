package org.abimon.localJukebox.model

data class EternalJukeboxSearchItem(
        val id: String,
        val name: String,
        val title: String,
        val artist: String,
        val url: String,
        val duration: Double
)