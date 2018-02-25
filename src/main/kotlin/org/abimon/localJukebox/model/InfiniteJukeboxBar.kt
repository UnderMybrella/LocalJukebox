package org.abimon.localJukebox.model

data class InfiniteJukeboxBar(
        override var duration: Double,
        override val start: Double,
        override val confidence: Double
) : InfiniteJukeboxComponent<InfiniteJukeboxBar>() {
    override val componentList: Array<InfiniteJukeboxBar>
        get() = track.analysis.bars
}