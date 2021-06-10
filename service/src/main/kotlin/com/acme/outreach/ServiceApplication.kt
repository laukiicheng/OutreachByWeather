package com.acme.outreach

import com.github.cloudyrock.spring.v5.EnableMongock
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.boot.runApplication
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer

@EnableMongock
@SpringBootApplication
// @ComponentScan("com.acme")
class ServiceApplication : SpringBootServletInitializer() {
    override fun configure(application: SpringApplicationBuilder): SpringApplicationBuilder {
        return application.sources(ServiceApplication::class.java)
    }
}

fun main(args: Array<String>) {
    @Suppress("SpreadOperator")
    try {
        runApplication<ServiceApplication>(*args)
    } catch (throwable: Throwable) {
        println("ERROR Starting Application")
        throwable.printStackTrace()
    }
}
