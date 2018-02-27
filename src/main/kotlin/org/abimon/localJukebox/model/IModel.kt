package org.abimon.localJukebox.model

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager
import com.sedmelluq.discord.lavaplayer.track.playback.AudioFrame
import org.abimon.localJukebox.LocalJukebox
import org.abimon.localJukebox.controller.IController
import org.abimon.localJukebox.view.IView
import java.io.File
import java.util.*

interface IModel {
    val views: Array<IView>
        get() = LocalJukebox.views
    val controller: IController
        get() = LocalJukebox.controller

    val manager: AudioPlayerManager

    var currentTrack: InfiniteJukeboxTrack?
    val beatMap: Map<InfiniteJukeboxBeat, Array<AudioFrame>>
    val audioFrames: Queue<AudioFrame>

    var continueLooping: Boolean
    var loopBranchPercentage: Int
    var isPaused: Boolean

    var isActive: Boolean

    val cachedAnalysisFiles: Array<File>
    val cachedAudioFiles: Array<File>
}