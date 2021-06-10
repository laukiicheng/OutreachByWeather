import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version Versions.springBoot
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    id("jacoco")
    kotlin("jvm") version Versions.kotlin
    kotlin("plugin.spring") version Versions.kotlin
    id("org.jlleitschuh.gradle.ktlint") version "9.4.1"
    id("org.owasp.dependencycheck") version "5.2.4"
}

tasks.withType<org.springframework.boot.gradle.tasks.bundling.BootJar> {
    enabled = false
}

java.sourceCompatibility = JavaVersion.VERSION_11
allprojects {
    group = "com.acme"
    version = "1.0-SNAPSHOT"

    apply(plugin = "kotlin")
    apply(plugin = "org.jetbrains.kotlin.plugin.spring")
    apply(plugin = "io.spring.dependency-management")
    apply(plugin = "org.owasp.dependencycheck")
    apply(plugin = "org.jlleitschuh.gradle.ktlint")

    repositories {
        mavenCentral()
    }

    dependencyManagement {
        imports {
//            mavenBom("org.springframework.cloud:spring-cloud-dependencies:${Versions.springCloud}")
            mavenBom(org.springframework.boot.gradle.plugin.SpringBootPlugin.BOM_COORDINATES)
        }
    }
    ktlint {
        version.set("0.40.0")
        debug.set(false)
        verbose.set(true)
        android.set(false)
        outputToConsole.set(true)
        outputColorName.set("RED")
        ignoreFailures.set(false)
        enableExperimentalRules.set(true)
        additionalEditorconfigFile.set(file(".editorconfig"))
        disabledRules.set(
            setOf(
                "import-ordering",
                "experimental:indent",
                "experimental:spacing-between-declarations-with-comments"
            )
        )
        reporters {
            reporter(org.jlleitschuh.gradle.ktlint.reporter.ReporterType.PLAIN)
            reporter(org.jlleitschuh.gradle.ktlint.reporter.ReporterType.CHECKSTYLE)
        }
        kotlinScriptAdditionalPaths {
            include(fileTree("scripts/"))
        }
        filter {
            exclude("**/generated/**")
            include("**/kotlin/**")
        }
    }
}

subprojects {
    repositories {
        mavenCentral()
    }

    apply(plugin = "org.jlleitschuh.gradle.ktlint") // Version should be inherited from parent
    dependencies {

        // logging extension library to allow for kotlin idiomatic logging conventions
        implementation("io.github.microutils:kotlin-logging:2.0.4")

        // using Mockk as our mocking framework: https://github.com/mockk/mockk
        testImplementation("io.mockk:mockk:1.10.6")

        testImplementation("org.springframework:spring-test")

        // comprehensive kotlin test framework, more info at https://github.com/kotlintest/kotlintest
        testImplementation("io.kotest:kotest-runner-junit5-jvm:${Versions.kotest}")
        testImplementation("io.kotest:kotest-assertions-core-jvm:${Versions.kotest}")
    }

    tasks.withType<KotlinCompile> {
        println("Configuring $name in project ${project.name}...")
        kotlinOptions {
            jvmTarget = "11"
            languageVersion = "1.4"
        }
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }
}

val unitTestResultsLocation = "reports/test/unit-tests" // usage is generally $build/unitTestResultsLocation
val testExecutionReportPaths = mutableListOf<String>()

// coverage report output locations - usage is generally $buildDir/coverage*
val coverageXmlReport = "jacoco/coverage.xml"
val coverageHtmlReport = "jacoco/html"

/**
 * projectsRequiringCoverageReports & coverageExclude* work in concert to support
 * excluding projects and packages from coverage reporting.
 *
 * - projectsRequiringCoverageReports: projects/submodules not in this list won't have coverage reported
 * - coverageExcludePaths/Packages: omits specific packages/paths within projects
 *   in projectsRequiringCoverageReports from coverage reporting
 */
val projectsRequiringCoverageReports =
    listOf("service")

// TODO: What do we actually need to exlude for packages and paths
val coverageExcludePackages = listOf(
    "com.acme.outreach.ServiceApplication*",
    "com.acme.outreach.config.*",
    "com.acme.outreach.migration.*",
    "com.acme.outreach.tests.*"
)
val coverageExcludePaths = listOf(
    "**/com/acme/outreach/ServiceApplication*",
    "**/com/acme/outreach/config/**/*",
    "**/com/acme/outreach/migration/**/*",
    "**/com/acme/tests/**/*",
    "service/src/integrationTest/kotlin/com/acme/outreach/**"
)

// configures test and coverage tasks for specified projects submodules
configure(subprojects.filter { projectsRequiringCoverageReports.contains(it.name) }) {

    pluginManager.apply("jacoco")

    tasks.named<Test>("test") {
        useJUnitPlatform()
        testLogging {
            showStandardStreams = false
            setExceptionFormat("full")
            events = setOf(
                org.gradle.api.tasks.testing.logging.TestLogEvent.FAILED,
                org.gradle.api.tasks.testing.logging.TestLogEvent.STANDARD_ERROR
            )
        }
        reports.html.destination = file("$buildDir/$unitTestResultsLocation")
        reports.junitXml.destination = file("$buildDir/$unitTestResultsLocation")
        testExecutionReportPaths += "$buildDir/$unitTestResultsLocation"
        finalizedBy("jacocoTestReport")
    }

    tasks.named<JacocoReport>("jacocoTestReport") {
        dependsOn("test")

        additionalSourceDirs.from(sourceSets["main"].allSource.srcDirs)
        sourceDirectories.from(sourceSets["main"].allSource.srcDirs)
        classDirectories.from(sourceSets["main"].output)

        reports {
            xml.isEnabled = true
            xml.destination = file("$buildDir/$coverageXmlReport")
            html.isEnabled = true
            html.destination = file("$buildDir/$coverageHtmlReport")
            csv.isEnabled = false
        }
    }

    tasks.named<JacocoCoverageVerification>("jacocoTestCoverageVerification") {
        violationRules {
            rule {
                element = "CLASS"
                excludes = coverageExcludePackages
            }
        }
    }
}

// This task aggregates all the submodule coverage reports into a single report
tasks.register<JacocoReport>("jacocoRootReport") {
    val jacocoReportTasks = subprojects.filter { projectsRequiringCoverageReports.contains(it.name) }
        .map { it.tasks["jacocoTestReport"] as JacocoReport }

    dependsOn(jacocoReportTasks)

    jacocoReportTasks.forEach { jacocoReportTask ->
        println("Aggregating jacoco coverage data from ${jacocoReportTask.project.name}")

        val execFiles = jacocoReportTask.executionData.filter { it.exists() && it.name.endsWith(".exec") }
        executionData(execFiles)
        additionalSourceDirs.from(jacocoReportTask.additionalSourceDirs ?: files())
        sourceDirectories.from(jacocoReportTask.sourceDirectories ?: files())

        // This is required to omit unwanted coverage from the aggregate reports.
        if (jacocoReportTask.classDirectories != null) {
            classDirectories.from(
                jacocoReportTask.classDirectories.files.map {
                    fileTree(it) { exclude(coverageExcludePaths) }
                }
            )
        }
    }

    reports {
        xml.isEnabled = true
        xml.destination = file("$buildDir/$coverageXmlReport")
        html.isEnabled = true
        html.destination = file("$buildDir/$coverageHtmlReport")
        csv.isEnabled = false
    }
}

dependencyCheck {
    format = org.owasp.dependencycheck.reporting.ReportGenerator.Format.ALL
}
