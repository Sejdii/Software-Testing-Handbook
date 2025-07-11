plugins {
    java
    groovy
    id("org.springframework.boot") version "3.5.0"
    id("io.spring.dependency-management") version "1.1.7"

    id("com.diffplug.spotless").version("7.0.3")
}

group = "pl.sejdii"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(24)
    }
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

val spockVersion = "2.3-groovy-3.0"
val groovyVersion = "3.0.24"
val mockitoVersion = "5.18.0"
val byteBuddyVersion = "1.17.5"
val objenesisVersion = "3.4"
val mapstructVersion = "1.6.3"

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jdbc")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.flywaydb:flyway-core")
    implementation("org.flywaydb:flyway-database-postgresql")
    implementation("org.mapstruct:mapstruct:$mapstructVersion")
    implementation("org.springframework.kafka:spring-kafka")
    implementation("org.jetbrains:annotations:24.0.0")

    compileOnly("org.projectlombok:lombok")
    compileOnly("org.springframework.kafka:spring-kafka")

    runtimeOnly("org.postgresql:postgresql")

    annotationProcessor("org.projectlombok:lombok")
    annotationProcessor("org.mapstruct:mapstruct-processor:$mapstructVersion")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.boot:spring-boot-testcontainers")
    testImplementation("org.springframework.kafka:spring-kafka-test")
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:postgresql")
    testImplementation("org.spockframework:spock-core:$spockVersion")
    testImplementation("org.codehaus.groovy:groovy-all:$groovyVersion")
    testImplementation("org.mockito:mockito-core:$mockitoVersion")
    testImplementation("org.mockito:mockito-junit-jupiter:$mockitoVersion")

    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testRuntimeOnly("net.bytebuddy:byte-buddy:$byteBuddyVersion")
    testRuntimeOnly("org.objenesis:objenesis:$objenesisVersion")

    testCompileOnly("org.projectlombok:lombok")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

spotless {
    java {
        googleJavaFormat()

        formatAnnotations()
    }
    kotlin {
        ktlint()
    }
    groovy {
        greclipse()
        excludeJava()
    }
    kotlinGradle {
        ktlint()
    }
}
