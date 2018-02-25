package org.abimon.localJukebox.model

data class InfiniteJukeboxBranch(
        val percent: Double,
        val i: Int,
        val which: Int,
        val q: InfiniteJukeboxComponent<*>,
        val neighbor: InfiniteJukeboxEdge
)