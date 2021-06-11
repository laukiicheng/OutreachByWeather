plugins {
    idea
}

apply(plugin = "org.springframework.boot")
apply<IdeaPlugin>()

sourceSets.create("integrationTest") {
    compileClasspath += sourceSets["main"].output + configurations.testCompile
    compileClasspath += sourceSets["test"].output + configurations.testCompile
    runtimeClasspath += output + compileClasspath + configurations.testRuntime
    resources.srcDir("src/integrationTest/resources")
}

configurations {
    this["integrationTestImplementation"].extendsFrom(this["testImplementation"])
    this["integrationTestRuntime"].extendsFrom(this["testRuntime"])
}
val integrationTestImplementation = configurations["integrationTestImplementation"]!!

dependencyManagement {
    imports {
        mavenBom("org.testcontainers:testcontainers-bom:1.15.2")
        mavenBom("com.github.cloudyrock.mongock:mongock-bom:4.3.8")
    }
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-reflect:${Versions.kotlin}")

    implementation("org.springframework.boot:spring-boot-starter-aop")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")
//    implementation("org.springframework.cloud:spring-cloud-starter-bootstrap")

    implementation("org.springframework.retry:spring-retry")
    implementation("org.springframework:spring-webmvc")

    implementation("javax.validation:validation-api:2.0.1.Final")
    // Json logging
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.9.4.1")
    implementation("ch.qos.logback.contrib:logback-json-classic:0.1.5")
    implementation("ch.qos.logback.contrib:logback-jackson:0.1.5")
    implementation("com.sumologic.plugins.logback:sumologic-logback-appender:1.5")

    implementation("com.github.cloudyrock.mongock:mongock-spring-v5")
    implementation("com.github.cloudyrock.mongock:mongodb-springdata-v3-driver")

    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(module = "junit")
    }

    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("com.ninja-squad:springmockk:2.0.1")

//    testImplementation("org.hamcrest:hamcrest-core:1.3")

    integrationTestImplementation("io.kotest:kotest-extensions-spring-jvm:4.4.3")
    integrationTestImplementation("org.springframework.boot:spring-boot-test-autoconfigure")
    integrationTestImplementation("org.springframework:spring-test")

    integrationTestImplementation("org.testcontainers:testcontainers")
    integrationTestImplementation("org.testcontainers:junit-jupiter")
    integrationTestImplementation("org.testcontainers:localstack")
}

val integrationTestResultsLocation = "reports/test/integration-tests"
tasks.register<Test>("integrationTest") {
    useJUnitPlatform()
    description = "Runs integration tests"
    group = "verification"
    testClassesDirs = sourceSets["integrationTest"].output.classesDirs
    classpath = sourceSets["integrationTest"].runtimeClasspath
    outputs.upToDateWhen { false }
    mustRunAfter("test")
    reports.html.destination = file("$buildDir/$integrationTestResultsLocation")
    reports.junitXml.destination = file("$buildDir/$integrationTestResultsLocation")

    testLogging {
        showStandardStreams = false
        setExceptionFormat("full")
        events = setOf(
            org.gradle.api.tasks.testing.logging.TestLogEvent.STANDARD_ERROR,
            org.gradle.api.tasks.testing.logging.TestLogEvent.FAILED
        )
    }

    val includeTagsKey = "kotlintest.tags.include"
    System.getProperty(includeTagsKey)?.run {
        println("Running integration tests with include tag $this")
        systemProperty(includeTagsKey, this)
    }

    val excludeTagsKey = "kotlintest.tags.exclude"
    System.getProperty(excludeTagsKey)?.run {
        println("Running integration tests with exclude tag $this")
        systemProperty(excludeTagsKey, this)
    }
}

tasks["check"].dependsOn("integrationTest")

idea.module {
    testSourceDirs = testSourceDirs.plus(project.sourceSets["integrationTest"].allSource.srcDirs)
}
