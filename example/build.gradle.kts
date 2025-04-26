plugins {
    id("java")
    id("groovy")
}

group = "pl.sejdii"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    testImplementation("org.spockframework:spock-core:2.3-groovy-3.0")
    testImplementation("org.codehaus.groovy:groovy-all:3.0.24")
}

tasks.test {
    useJUnitPlatform()
}