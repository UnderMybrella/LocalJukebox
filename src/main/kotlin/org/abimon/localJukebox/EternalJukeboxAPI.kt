package org.abimon.localJukebox

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.Request
import org.abimon.localJukebox.model.EternalJukeboxSearchItem
import org.abimon.localJukebox.model.InfiniteJukeboxTrack

object EternalJukeboxAPI {
    val BASE_URL = "https://eternal.abimon.org/api"

    val ANALYSIS = "$BASE_URL/analysis"
    val ANALYSE = "$ANALYSIS/analyse"
    val SEARCH = "$ANALYSIS/search"

    val AUDIO = "$BASE_URL/audio"
    val JUKEBOX_AUDIO = "$AUDIO/jukebox"

    val USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.13; rv:57.0) Gecko/20100101 Firefox/57.0"

    val MAPPER: ObjectMapper = ObjectMapper()
            .registerKotlinModule()
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
            .setSerializationInclusion(JsonInclude.Include.NON_ABSENT)

    fun searchFor(query: String): Array<EternalJukeboxSearchItem>? {
        val (_, response) = Fuel.get(SEARCH, listOf("query" to query, "results" to 5)).userAgent().response()

        if(response.statusCode != 200)
            return null

        return MAPPER.tryReadValue(response.data, Array<EternalJukeboxSearchItem>::class)
    }

    fun getAnalysis(id: String): InfiniteJukeboxTrack? {
        val (_, response) = Fuel.get("$ANALYSE/$id").userAgent().response()

        if(response.statusCode != 200)
            return null

        return MAPPER.tryReadValue(response.data, InfiniteJukeboxTrack::class)
    }

    fun getAudio(id: String): ByteArray? {
        val (_, response) = Fuel.get("$JUKEBOX_AUDIO/$id").userAgent().response()

        if(response.statusCode != 200)
            return null

        return response.data
    }

    fun Request.userAgent(): Request = header("User-Agent" to USER_AGENT)
}