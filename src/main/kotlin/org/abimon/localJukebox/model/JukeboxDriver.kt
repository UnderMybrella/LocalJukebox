package org.abimon.localJukebox.model

import com.sedmelluq.discord.lavaplayer.format.AudioDataFormat
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers
import com.sedmelluq.discord.lavaplayer.track.playback.AudioFrame
import kotlinx.coroutines.experimental.Job
import java.util.concurrent.ConcurrentLinkedQueue

object JukeboxDriver: IModel {
    override val manager: AudioPlayerManager
    override val currentTrack: InfiniteJukeboxTrack? = null
    override val beatMap: Map<InfiniteJukeboxBeat, Array<AudioFrame>> = emptyMap()
    override val audioFrames: ConcurrentLinkedQueue<AudioFrame> = ConcurrentLinkedQueue()

    override val continueLooping: Boolean = false
    override val loopBranchPercentage: Int = 0
    override val isPaused: Boolean = false

    override var isActive: Boolean
        get() = _isActive
        set(value) {
            if(!value) {
                loopJob?.cancel()
                loopJob = null
            }

            _isActive = value
        }

    private val player: AudioPlayer
    private var loopJob: Job?
    private var _isActive: Boolean = true

    init {
        manager = DefaultAudioPlayerManager()
        manager.setUseSeekGhosting(false)
        manager.configuration.outputFormat = AudioDataFormat(2, 44100, 960, AudioDataFormat.Codec.PCM_S16_BE)

        AudioSourceManagers.registerLocalSource(manager)

        player = manager.createPlayer()

        loopJob = null
    }
}