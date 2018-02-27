package org.abimon.localJukebox.view

import club.minnced.discord.rpc.DiscordEventHandlers
import club.minnced.discord.rpc.DiscordRPC
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import org.abimon.localJukebox.model.InfiniteJukeboxTrack
import java.util.concurrent.TimeUnit

object DiscordRichPresenceView: IView {
    override var isActive: Boolean
        get() = true
        set(value) {
            if (value) {

            } else {
                discord.Discord_ClearPresence()
                updateJob?.cancel()
                updateJob = null
            }
        }
    private val CLIENT_ID = "282530881565097984"
    private val discord = DiscordRPC.INSTANCE

    private var updateJob: Job? = null

    var started: Long = System.currentTimeMillis()

    override fun newTrackPlaying(track: InfiniteJukeboxTrack) {
        started = System.currentTimeMillis() / 1000
        discord.Discord_ClearPresence()
        discord.updatePresence {
            startTimestamp = started
            state = "Listening to ${track.info.title} by ${track.info.artist}"

            largeImageKey = "default_song"
            largeImageText = "${track.info.title} by ${track.info.artist}"
            smallImageKey = "play"
            smallImageText = "Playing"
        }
    }

    override fun trackStoppedPlaying(track: InfiniteJukeboxTrack?) {
        discord.Discord_ClearPresence()
    }

    override fun trackPaused() {
        val track = model.currentTrack ?: return
        discord.Discord_ClearPresence()
        discord.updatePresence {
            startTimestamp = started
            state = "Listening to ${track.info.title} by ${track.info.artist}"

            largeImageKey = "default_song"
            largeImageText = "${track.info.title} by ${track.info.artist}"
            smallImageKey = "pause"
            smallImageText = "Playing"
        }
    }
    override fun trackUnpaused() {
        val track = model.currentTrack ?: return
        discord.Discord_ClearPresence()
        discord.updatePresence {
            startTimestamp = started
            state = "Listening to ${track.info.title} by ${track.info.artist}"

            largeImageKey = "default_song"
            largeImageText = "${track.info.title} by ${track.info.artist}"

            smallImageKey = "play"
            smallImageText = "Playing"
        }
    }
    override fun stopLooping() {}
    override fun startLooping() {}

    fun DiscordRPC.updatePresence(init: RichPresenceBuilder.() -> Unit) {
        val rp = RichPresenceBuilder()
        rp.init()

        this.Discord_UpdatePresence(rp.build())
    }

    init {
        discord.Discord_Initialize(CLIENT_ID, DiscordEventHandlers().apply { this.ready = DiscordEventHandlers.OnReady { discord.Discord_ClearPresence() } }, false, null)

        updateJob = launch {
            while (isActive) {
                discord.Discord_RunCallbacks()
                delay(2, TimeUnit.SECONDS)
            }
        }
    }
}