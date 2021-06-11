package com.acem.outreach

import org.testcontainers.containers.GenericContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@Testcontainers
class TestContainerUtils {
    companion object {
        @Container
        private lateinit var mongoContainer: KGenericContainer

        fun startTestContainers() {
            startMongoContainer()
        }

        fun stopTestContainers() {
            mongoContainer.stop()
        }

        private fun startMongoContainer() {
            mongoContainer = KGenericContainer("mongo:4.2")
                .withExposedPorts(27017)
                .withEnv(
                    mapOf(
                        "MONGO_INITDB_ROOT_USERNAME" to "local",
                        "MONGO_INITDB_ROOT_PASSWORD" to "localPass"
                    )
                )
            mongoContainer.start()

            val mongoConnectionString =
                "mongodb://${mongoContainer.containerIpAddress}:${mongoContainer.getMappedPort(27017)}/outreach-weather-service?ssl=false&uuidRepresentation=javaLegacy"

            UtilityHelper.updatePropertiesFile(
                mapOf("spring.data.mongodb.uri" to mongoConnectionString)
            )
        }
    }

    private class KGenericContainer(imageName: String) : GenericContainer<KGenericContainer>(imageName)
}
