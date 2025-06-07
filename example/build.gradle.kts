plugins {
    id("java")
    id("groovy")

    id("com.diffplug.spotless").version("7.0.3")
}

group = "pl.sejdii"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

val junitVersion = "5.10.0"
val spockVersion = "2.3-groovy-3.0"
val groovyVersion = "3.0.24"
val byteBuddyVersion = "1.17.5"
val objenesisVersion = "3.4"
val mockitoVersion = "5.18.0"

dependencies {
    testImplementation(platform("org.junit:junit-bom:$junitVersion"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    testImplementation("org.spockframework:spock-core:$spockVersion")
    testImplementation("org.codehaus.groovy:groovy-all:$groovyVersion")
    testImplementation("org.mockito:mockito-core:$mockitoVersion")
    testImplementation("org.mockito:mockito-junit-jupiter:$mockitoVersion")

    testRuntimeOnly("net.bytebuddy:byte-buddy:$byteBuddyVersion")
    testRuntimeOnly("org.objenesis:objenesis:$objenesisVersion")
}

tasks.test {
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
