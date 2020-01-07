import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {

	// https://mvnrepository.com/artifact/org.jetbrains.kotlin/kotlin-gradle-plugin
	rootProject.extra["kotlinVersion"] = "1.3.61"

	// https://plugins.gradle.org/plugin/com.github.ben-manes.versions
	rootProject.extra["ben-manes.versions"] = "0.27.0"

	// https://mvnrepository.com/artifact/org.springframework.boot/spring-boot
	rootProject.extra["spring_boot2Version"] = "2.2.2.RELEASE"

	// https://mvnrepository.com/artifact/org.springframework.boot/spring-boot
    rootProject.extra["spring_bootVersion"] = "2.2.2.RELEASE"

	// https://mvnrepository.com/artifact/javax.xml.bind/jaxb-api
    rootProject.extra["jaxbapiVersion"] = "2.3.1"
	
	// https://mvnrepository.com/artifact/com.fasterxml.jackson.module/jackson-module-kotlin
    rootProject.extra["jacksonModule_kotlinVersion"] = "2.10.1"
	
	// https://mvnrepository.com/artifact/ch.qos.logback/logback-classic
    rootProject.extra["logbackVersion"] = "1.2.3"
	
	// https://mvnrepository.com/artifact/org.junit.jupiter/junit-jupiter-api
    rootProject.extra["junit5Version"] = "5.5.2"
	
	// https://mvnrepository.com/artifact/org.amshove.kluent/kluent
    rootProject.extra["kluent_Version"] = "1.53"
	
	// https://mvnrepository.com/artifact/org.mockito/mockito-core
	rootProject.extra["mockito_Version"] = "3.2.4"

	// https://mvnrepository.com/artifact/com.nhaarman.mockitokotlin2/mockito-kotlin
	rootProject.extra["mockitoKotlin_Version"] = "2.2.0"
	
	// https://mvnrepository.com/artifact/javax.cache/cache-api
	rootProject.extra["cacheApi_Version"] = "1.1.1"
	
	// https://mvnrepository.com/artifact/org.ehcache/ehcache
	rootProject.extra["ehcache_Version"] = "3.8.1"
	
    System.setProperty("ben-manes.versions", rootProject.extra["ben-manes.versions"] as String)
    System.setProperty("kotlinVersion", rootProject.extra["kotlinVersion"] as String)
    System.setProperty("spring_boot2Version", rootProject.extra["spring_boot2Version"] as String)
	
}

plugins {

	id("com.github.ben-manes.versions") version System.getProperty("ben-manes.versions")

    kotlin("jvm") version System.getProperty("kotlinVersion")

    id("org.springframework.boot") version System.getProperty("spring_boot2Version")
	
	java
	application
	
}

repositories {

	jcenter()
	mavenLocal()
	mavenCentral()

	maven {
		url = uri("https://plugins.gradle.org/m2/")
	}
	maven {
		url = uri("https://kotlin.bintray.com/kotlin-js-wrappers")
	}

}

dependencies {

	compile(kotlin("stdlib-jdk8"))
	implementation(kotlin("stdlib-jdk7"))
	implementation(kotlin("reflect"))

	compile("com.fasterxml.jackson.module:jackson-module-kotlin:${rootProject.extra["jacksonModule_kotlinVersion"]}")

	testImplementation(kotlin("test"))
	testImplementation(kotlin("test-junit"))

	implementation("javax.xml.bind:jaxb-api:${rootProject.extra["jaxbapiVersion"]}")

	compile("ch.qos.logback:logback-classic:${rootProject.extra["logbackVersion"]}")

	testImplementation("org.junit.jupiter:junit-jupiter-api:${rootProject.extra["junit5Version"]}")

	testImplementation("org.amshove.kluent:kluent:${rootProject.extra["kluent_Version"]}")

	// mockito
	testImplementation("org.mockito:mockito-core:${rootProject.extra["mockito_Version"]}")
	// 39. Mocking final types, enums and final methods (Since 2.1.0) https://javadoc.io/static/org.mockito/mockito-core/3.2.0/org/mockito/Mockito.html#39
	// https://mvnrepository.com/artifact/org.mockito/mockito-inline
	testImplementation("org.mockito:mockito-inline:${rootProject.extra["mockito_Version"]}")
	
	// mockito-kotlin
	testImplementation("com.nhaarman.mockitokotlin2:mockito-kotlin:${rootProject.extra["mockitoKotlin_Version"]}")
	
	// Sharing dependency versions between projects https://docs.gradle.org/current/userguide/platforms.html
	// Example 2. Depending on a BOM to import its dependency constraints build.gradle.ktsdependencies { 
	// import a BOM 
	implementation(platform("org.springframework.boot:spring-boot-dependencies:${rootProject.extra["spring_bootVersion"]}"))  // Spring
	// define dependencies without versions 
	
    // implementation: spring core specific features, depend on spring core framework
    implementation("org.springframework.boot:spring-boot-starter") // Spring
	testImplementation("org.springframework.boot:spring-boot-starter-test") // Spring
	
	// https://www.baeldung.com/spring-boot-ehcache#dependencies
    implementation("org.springframework.boot:spring-boot-starter-cache") // Spring
	implementation("org.ehcache:ehcache:${rootProject.extra["ehcache_Version"]}")
	implementation("javax.cache:cache-api:${rootProject.extra["cacheApi_Version"]}")
	
}

java {
	sourceCompatibility = JavaVersion.VERSION_12
	targetCompatibility = JavaVersion.VERSION_12
}

tasks.withType<KotlinCompile>().all {
	kotlinOptions {
		jvmTarget = "12"
		// freeCompilerArgs = listOf("-Xjsr305=strict")
	}
}

group = "vlfsoft"
version = 1.0 // -> vlfsoft.module-1.0.jar

val moduleName = "rd0003"

application {
    mainClassName = "vlfsoft.$moduleName.ApplicationKt"
}

val jar by tasks.getting(Jar::class) {
    manifest {
        attributes["Main-Class"] = application.mainClassName
        attributes["Automatic-Module-Name"] = "vlfsoft.$moduleName.app"
    }
}

tasks {

	// https://stackoverflow.com/questions/31405818/want-to-specify-jar-name-and-version-both-in-build-gradle
	// https://github.com/barlog-m/spring-boot-2-example-app/blob/master/build.gradle.kts
	// https://stackoverflow.com/questions/55575264/creating-a-fat-jar-in-gradle-with-kotlindsl
    bootJar {
        // defaults to project.name
        archiveBaseName.set("vlfsoft.$moduleName.app")

        // defaults to all, so removing this overrides the normal, non-fat jar
        // archiveClassifier.set("")
    }
}

// PRB: Gradle doesn't execute tests https://stackoverflow.com/questions/56960467/gradle-doesnt-execute-tests
// WO: You have to enable JUnit support in your test configuration. For JUnit 5, add the following to your gradle build file:
// https://github.com/bmuschko/gradle-junit5-kotlin-dsl/blob/master/build.gradle.kts
tasks.withType<Test> {

    useJUnitPlatform()

    // Using Gradle Kotlin DSL with junit 5 https://technology.lastminute.com/junit5-kotlin-and-gradle-dsl/
    // https://github.com/tieskedh/KotlinPoetDSL/blob/master/build.gradle.kts
    // Gradle: How to Display Test Results in the Console in Real Time? https://stackoverflow.com/questions/3963708/gradle-how-to-display-test-results-in-the-console-in-real-time
    addTestListener(object : TestListener {
        override fun beforeTest(p0: TestDescriptor?) = Unit
        override fun beforeSuite(p0: TestDescriptor?) = Unit
        override fun afterTest(desc: TestDescriptor, result: TestResult) = Unit

        private var isTotalResultsPrinted = false

        /**
         * [afterSuite] is invoked several times: after each class with tests + two more invocations
         */
        override fun afterSuite(desc: TestDescriptor, result: TestResult) {
            val totalClassName = "Total"
            val testReportIndexHtml = "build/reports/tests/test/index.html"
            val testReportIndexHtmlFile = File(projectDir, testReportIndexHtml)

            val testName = desc.className ?: totalClassName

            if (testName != totalClassName || !isTotalResultsPrinted) {
                println("Results: $testName, ${result.failedTestCount} failed / ${result.successfulTestCount} passed")

                if (testName == totalClassName) {
                    isTotalResultsPrinted = true
                    println("Html report: file:///${testReportIndexHtmlFile.absolutePath.replace('\\', '/')}")

                }

            }

            /**
             * [afterSuite] is invoked several times: after each class with tests + two more invocations
             *
             * if (testReportIndexHtmlFile.exists()) Desktop.getDesktop().browse(URI(testReportIndexHtmlFile.absolutePath.replace('\\', '/')))
             * PRB: testReportIndexHtml is generated AFTER [afterSuite] is invoked
             * WO: open and refresh testReportIndexHtml in browser manually. see above
             * Solution: ?
             */
        }
    })

    // Is there any way to turn off parallel execution for the tests? #32 https://github.com/scoverage/gradle-scoverage/issues/32
    // maxParallelForks = 1
}

