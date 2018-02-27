package org.abimon.localJukebox.view

import com.sedmelluq.discord.lavaplayer.format.AudioDataFormat
import com.sedmelluq.discord.lavaplayer.format.AudioDataFormatTools
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import org.abimon.localJukebox.model.InfiniteJukeboxTrack
import javax.sound.sampled.AudioFormat
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.DataLine
import javax.sound.sampled.SourceDataLine

object SystemAudioOutputView: IView {
    override var isActive: Boolean
        get() = _isActive
        set(value) {
            if(value) {
                playing = launch {
                    while (isActive) {
                        while (model.audioFrames.size < 2) delay(10)

                        val data = model.audioFrames.poll()?.data ?: continue
                        line.write(data, 0, data.size)
                    }
                }
            } else {
                playing?.cancel()
                playing = null
            }

            _isActive = value
        }

    private val format: AudioDataFormat
    private val jdkFormat: AudioFormat
    private val info: DataLine.Info
    private val line: SourceDataLine

    private var playing: Job?
    private var _isActive: Boolean = true

    override fun newTrackPlaying(track: InfiniteJukeboxTrack) {}
    override fun trackStoppedPlaying(track: InfiniteJukeboxTrack?) {}
    override fun trackPaused() {}
    override fun trackUnpaused() {}
    override fun stopLooping() {}
    override fun startLooping() {}

    init {
        format = model.manager.configuration.outputFormat
        jdkFormat = AudioDataFormatTools.toAudioFormat(format)
        info = DataLine.Info(SourceDataLine::class.java, jdkFormat)
        line = AudioSystem.getLine(info) as SourceDataLine

        line.open(jdkFormat)
        line.start()

        playing = launch {
            while (isActive) {
                while (model.audioFrames.size < 2 || model.isPaused) delay(10)

                val data = model.audioFrames.poll()?.data ?: continue
                line.write(data, 0, data.size)
            }
        }
    }
}