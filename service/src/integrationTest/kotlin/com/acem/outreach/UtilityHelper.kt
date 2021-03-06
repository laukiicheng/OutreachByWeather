package com.acem.outreach

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import java.io.File
import java.util.Properties
import java.util.Random
import kotlin.streams.asSequence
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder
import org.springframework.util.ResourceUtils

class UtilityHelper {

    companion object {
        const val testPropertiesFilePath = "application-integration-test.properties"
        private val charPool = ('a'..'z') + ('A'..'Z') + ('0'..'9')
        private val objectMapper =
            Jackson2ObjectMapperBuilder.json()
                .featuresToEnable(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, JsonParser.Feature.STRICT_DUPLICATE_DETECTION)
                .featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .build<ObjectMapper>()
                .registerKotlinModule()
                .registerModules(JavaTimeModule())

        fun createRandomAlphaNumeric(length: Int = 5) =
            Random()
                .ints(length.toLong(), 0, charPool.size)
                .asSequence()
                .map(charPool::get)
                .joinToString("")

        fun updatePropertiesFile(properties: Map<String, String>) =
            File(UtilityHelper::class.java.classLoader.getResource(".")!!.file + testPropertiesFilePath)
                .apply {
                    createNewFile()
                    inputStream().use { inputStream ->
                        val props = Properties()
                        props.load(inputStream)
                        for ((key, value) in properties) {
                            props.setProperty(key, value)
                        }

                        outputStream().use { outputStream ->
                            props.store(
                                outputStream,
                                "Updating properties file in classpath $testPropertiesFilePath"
                            )
                        }
                    }
                }

        fun clearPropertiesFile() {
            with(ResourceUtils.getFile("${ResourceUtils.CLASSPATH_URL_PREFIX}$testPropertiesFilePath")) {
                if (exists()) delete()
            }
        }

        fun <T> createHttpEntity(
            requestContent: T,
            mediaType: MediaType = MediaType.APPLICATION_JSON
        ): HttpEntity<String> {
            val jsonString = objectMapper.writeValueAsString(requestContent)
            val headers = createHttpHeadersMediaType(mediaType)

            return HttpEntity(jsonString, headers)
        }

        private fun createHttpHeadersMediaType(mediaType: MediaType): HttpHeaders {
            val httpHeaders = HttpHeaders()
            httpHeaders.contentType = mediaType
            httpHeaders.accept = listOf(mediaType)

            return httpHeaders
        }
    }
}
