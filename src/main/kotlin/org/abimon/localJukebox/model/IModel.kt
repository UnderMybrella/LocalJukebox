package org.abimon.localJukebox.model

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager
import com.sedmelluq.discord.lavaplayer.track.playback.AudioFrame
import org.abimon.localJukebox.LocalJukebox
import org.abimon.localJukebox.controller.IController
import org.abimon.localJukebox.view.IView
import java.util.*

interface IModel {
    val view: IView
        get() = LocalJukebox.view
    val controller: IController
        get() = LocalJukebox.controller

    val manager: AudioPlayerManager

    val currentTrack: InfiniteJukeboxTrack?
    val beatMap: Map<InfiniteJukeboxBeat, Array<AudioFrame>>
    val audioFrames: Queue<AudioFrame>

    val continueLooping: Boolean
    val loopBranchPercentage: Int
    val isPaused: Boolean

    var isActive: Boolean
}