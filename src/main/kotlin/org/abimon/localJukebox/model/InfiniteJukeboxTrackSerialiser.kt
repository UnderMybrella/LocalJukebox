package org.abimon.localJukebox.model

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider

class InfiniteJukeboxTrackSerialiser: JsonSerializer<InfiniteJukeboxTrack>() {
    override fun serialize(value: InfiniteJukeboxTrack, gen: JsonGenerator, serializers: SerializerProvider) {
        val analysis = value.analysis
        val analysisMap = mapOf(
                "beats" to analysis.beats.map { beat -> mapOf("start" to beat.start, "duration" to beat.duration, "confidence" to beat.confidence) },
                "segments" to analysis.segments.map { segment -> mapOf("start" to segment.start, "duration" to segment.duration, "confidence" to segment.confidence, "lodness_max" to segment.loudness_max, "loudness_start" to segment.loudness_start, "loudness_max_time" to segment.loudness_max_time, "pitches" to segment.pitches, "timbre" to segment.timbre) },
                "tatums" to analysis.tatums.map { tatum -> mapOf("start" to tatum.start, "duration" to tatum.duration, "confidence" to tatum.confidence) },
                "bars" to analysis.bars.map { bar -> mapOf("start" to bar.start, "duration" to bar.duration, "confidence" to bar.confidence) },
                "sections" to analysis.sections.map { section -> mapOf("start" to section.start, "duration" to section.duration, "confidence" to section.confidence, "key" to section.key, "key_confidence" to section.key_confidence, "loudness" to section.loudness, "mode" to section.mode, "mode_confidence" to section.mode_confidence, "tempo" to section.tempo, "tempo_confidence" to section.tempo_confidence, "time_signature" to section.time_signature, "time_signature_confidence" to section.time_signature_confidence) }
        )

        val map = mapOf(
                "analysis" to analysisMap,
                "info" to value.info,
                "summary" to value.summary
        )

        gen.writeObject(map)
    }

    override fun handledType(): Class<InfiniteJukeboxTrack> = InfiniteJukeboxTrack::class.java
}