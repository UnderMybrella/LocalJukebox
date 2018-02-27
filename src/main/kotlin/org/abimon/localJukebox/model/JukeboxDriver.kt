package org.abimon.localJukebox.model

import com.sedmelluq.discord.lavaplayer.format.AudioDataFormat
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager
import com.sedmelluq.discord.lavaplayer.player.FunctionalResultHandler
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers
import com.sedmelluq.discord.lavaplayer.track.AudioTrackState
import com.sedmelluq.discord.lavaplayer.track.playback.AudioFrame
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.yield
import org.abimon.localJukebox.EternalJukeboxAPI
import java.io.File
import java.io.FileOutputStream
import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger
import kotlin.collections.HashMap
import kotlin.properties.Delegates

object JukeboxDriver : IModel {
    override val manager: AudioPlayerManager
    override var currentTrack: InfiniteJukeboxTrack? by Delegates.observable<InfiniteJukeboxTrack?>(null) { property, old, new ->
        if (new == null)
            views.forEach { view -> view.trackStoppedPlaying(old) }
        else {
            loadTrack(new)
            views.forEach { view -> view.newTrackPlaying(new) }
        }
    }


    override var beatMap: Map<InfiniteJukeboxBeat, Array<AudioFrame>> = emptyMap()
    override var audioFrames: Queue<AudioFrame> = ConcurrentLinkedQueue()

    override var continueLooping: Boolean
        get() = _continueLooping.get()
        set(value) {
            _continueLooping.set(value)
        }

    override var loopBranchPercentage: Int
        get() = _loopBranchPercentage.get()
        set(value) {
            _loopBranchPercentage.set(value)
        }

    override var isPaused: Boolean
        get() = _isPaused.get()
        set(value) {
            _isPaused.set(value)
        }

    override var isActive: Boolean
        get() = _isActive
        set(value) {
            if (!value) {
                loopJob?.cancel()
                loopJob = null
            }

            _isActive = value
        }

    override val cachedAnalysisFiles: Array<File>
        get() = storage.listFiles { file -> !file.isHidden && file.extension == "json" && !file.startsWith(".") && !file.startsWith("__") }

    override val cachedAudioFiles: Array<File>
        get() = storage.listFiles { file -> !file.isHidden && file.extension == "m4a" && !file.startsWith(".") && !file.startsWith("__") }

    private val storage = File("storage").apply { mkdir() }
    private val player: AudioPlayer
    private var loopJob: Job?
    private var _isActive: Boolean = true

    private val _continueLooping = AtomicBoolean(true)
    private val _loopBranchPercentage = AtomicInteger(75)
    private val _isPaused = AtomicBoolean(true)

    fun loadTrack(track: InfiniteJukeboxTrack) {
        loopJob?.cancel()
        loopJob = null

        println("Preprocessing...")

        preprocess(track)
        dynamicCalculateNearestNeighbors(track.analysis.beatsArray)

        val beats = track.analysis.beats
        val beatmap = HashMap<InfiniteJukeboxBeat, Array<AudioFrame>>()
        audioFrames.clear()

        val audioFile = File(storage, "${track.info.id}.m4a")

        if (!audioFile.exists()) {
            println("Downloading audio...")
            FileOutputStream(audioFile).use { out -> out.write(EternalJukeboxAPI.getAudio(track.info.id) ?: return System.err.println("Could not load audio: returned null")) }
        } else {
            println("Using cached audio")
        }

        val audio = ConcurrentLinkedQueue<AudioFrame>()
        val isFinished = AtomicBoolean(false)

        manager.loadItem(audioFile.absolutePath, FunctionalResultHandler({ lavaTrack ->
            launch {
                while (isActive && lavaTrack.state != AudioTrackState.FINISHED) {
                    yield()
                    audioFrames.add(player.provide() ?: continue)
                }

                isFinished.set(true)
            }

            player.startTrack(lavaTrack, false)
        }, null, null, null))

        while (!isFinished.get()) Thread.sleep(100)

        println("Breaking down into beats")

        val start: Array<AudioFrame> = beats[0].let { beat ->
            val range = 0 until (beat.start * 1000).toLong()
            return@let audio.filter { frame -> frame.timecode in range }.toTypedArray()
        }

        for (beat in beats) {
            val range = (beat.start * 1000).toLong() until ((beat.start * 1000) + (beat.duration * 1000)).toLong()
            beatmap[beat] = audio.filter { frame -> frame.timecode in range }.toTypedArray()
        }

        isPaused = true

        beatMap = beatmap

        var beatIndex = 0
        val rng = Random()
        val frames = ConcurrentLinkedQueue<AudioFrame>()
        val finalBeat = beats.last { beat -> beat.neighbors?.isNotEmpty() == true }

        audioFrames.addAll(start)

        loopJob = launch {
            while (isActive) {
                while (frames.size > 4 || isPaused) delay((beats[beatIndex].duration * 1000).toInt())

                val beat = beats[beatIndex]

                val neighbors = beat.neighbors
                if (neighbors?.isNotEmpty() == true && (beat.uuid == finalBeat.uuid || rng.nextBoolean()) && continueLooping) {
                    val branchingTo = neighbors[rng.nextInt(neighbors.size)].dest as? InfiniteJukeboxBeat
                    beatIndex = (branchingTo?.let { branchingBeat -> beats.indexOf(branchingBeat) } ?: beatIndex) + 1
                    if(beatIndex >= beats.size)
                        println()
                } else {
                    beatIndex++
                }

                if(beatIndex >= beats.size) {
                    System.err.println("We have to reset from the start ($beatIndex > ${beats.size}). Please report this to UnderMybrella over at https://github.com/UnderMybrella/LocalJukebox/issues")
                    beatIndex = 0
                }

                frames.addAll(beatMap[beats[beatIndex]]!!)
            }
        }
    }

    init {
        manager = DefaultAudioPlayerManager()
        manager.setUseSeekGhosting(false)
        manager.configuration.outputFormat = AudioDataFormat(2, 44100, 960, AudioDataFormat.Codec.PCM_S16_BE)

        AudioSourceManagers.registerLocalSource(manager)

        player = manager.createPlayer()

        loopJob = null
    }
}