package vlfsoft.rd0003

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.ComponentScan

/**
 * A Guide To Caching in Spring | Baeldung https://www.baeldung.com/spring-cache-tutorial
 * 3. Enable Caching https://www.baeldung.com/spring-cache-tutorial#enable-caching
 * 3.1. Spring Boot https://www.baeldung.com/spring-cache-tutorial#1-spring-boot-1
 */
@EnableCaching
@ComponentScan("vlfsoft")
@SpringBootApplication
open class Application

fun main(@Suppress("UnusedMainParameter") args: Array<String>) {
    SpringApplicationBuilder(Application::class.java).run()
}
