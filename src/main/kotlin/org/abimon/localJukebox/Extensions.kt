package org.abimon.localJukebox

import com.fasterxml.jackson.core.JsonParseException
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.JsonMappingException
import com.fasterxml.jackson.databind.ObjectMapper
import java.io.File
import java.io.InputStream
import kotlin.reflect.KClass

fun <T : Any> ObjectMapper.tryReadValue(src: ByteArray, klass: KClass<T>): T? {
    try {
        return this.readValue(src, klass.java)
    } catch (jsonProcessing: JsonProcessingException) {
        return null
    } catch (jsonMapping: JsonMappingException) {
        return null
    } catch (jsonParsing: JsonParseException) {
        return null
    }
}

fun <T : Any> ObjectMapper.tryReadValue(src: InputStream, klass: KClass<T>): T? {
    try {
        return this.readValue(src, klass.java)
    } catch (jsonProcessing: JsonProcessingException) {
        return null
    } catch (jsonMapping: JsonMappingException) {
        return null
    } catch (jsonParsing: JsonParseException) {
        return null
    }
}

fun <T : Any> ObjectMapper.tryReadValue(src: File, klass: KClass<T>): T? {
    try {
        return this.readValue(src, klass.java)
    } catch (jsonProcessing: JsonProcessingException) {
        return null
    } catch (jsonMapping: JsonMappingException) {
        return null
    } catch (jsonParsing: JsonParseException) {
        return null
    }
}