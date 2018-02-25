package org.abimon.localJukebox.model

data class InfiniteJukeboxTrack(
        val info: InfiniteJukeboxInfo,
        val analysis: InfiniteJukeboxAnalysis,
        val summary: EternalJukeboxSummary?
)