package org.abimon.localJukebox.controller

import org.abimon.localJukebox.EternalJukeboxAPI
import org.abimon.localJukebox.model.EternalJukeboxSearchItem
import org.abimon.localJukebox.model.JukeboxDriver
import java.text.NumberFormat

object ConsoleController : IController {
    override var isActive: Boolean
        get() = true
        set(value) {}

    val SPACES_REGEX = "\\s+".toRegex()
    val NUMBER_FORMATTER = NumberFormat.getInstance().apply {
        minimumIntegerDigits = 2
    }

    init {
        loop@ while (isActive) {
            print("> ")
            val line = readLine()?.split(SPACES_REGEX) ?: continue
            if (line.isEmpty())
                continue
            val params = line.subList(1, line.size)

            when (line[0].toLowerCase()) {
                "play" -> {
                    if (params.isEmpty()) {
                        println("No song provided, maybe you meant \"cached\"?")
                        continue@loop
                    }

                    val id = params[0]
                    var track = EternalJukeboxAPI.getAnalysis(id)

                    if (track == null) {
                        val searchQuery = params.joinToString(" ")
                        println("No song found for $id, performing a search query for $searchQuery")

                        val searchResults = EternalJukeboxAPI.searchFor(searchQuery)
                        if (searchResults == null || searchResults.isEmpty()) {
                            println("No songs found for $searchQuery")
                            continue@loop
                        }

                        println("Pick a number for the songs listed below, or \"cancel\" to return to the previous screen\n")
                        println(searchResults.mapIndexed { index, searchItem -> "${index + 1}) ${searchItem.name} by ${searchItem.artist} (${searchItem.duration.toInt().let { duration -> "${(duration / 1000) / 60}:${NUMBER_FORMATTER.format(duration / 1000 % 60)}" } })" }.joinToString("\n"))

                        while (true) {
                            print("Song Selection> ")
                            val song = readLine() ?: break
                            if(song.equals("cancel", true))
                                break

                            val num = song.toIntOrNull()

                            val result: EternalJukeboxSearchItem
                            if(num == null) {
                                val first = searchResults.firstOrNull { searchItem -> searchItem.title.equals(song, true) }
                                if(first == null) {
                                    println("\"$song\" isn't a number, and doesn't match the title of any songs")
                                    continue
                                }

                                result = first
                            } else {
                                result = searchResults[(num - 1) % searchResults.size]
                            }

                            track = EternalJukeboxAPI.getAnalysis(result.id)
                            break
                        }

                        if (track == null)
                            continue@loop
                    }

                    JukeboxDriver.currentTrack = track
                    println("Ready!")
                    JukeboxDriver.isPaused = false
                }
                "pause" -> {
                    if(model.currentTrack == null) {
                        println("No track is currently playing!")
                        continue@loop
                    }

                    if(model.isPaused) {
                        model.isPaused = false
                        println("Unpaused the currently playing track")
                    } else {
                        model.isPaused = true
                        println("Paused the currently playing track")
                    }
                }
                "quit" -> break@loop
            }
        }
    }
}