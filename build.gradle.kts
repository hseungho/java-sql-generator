plugins {
    id("java")
    `maven-publish`
}

group = "com.hseungho.util"
version = "0.0.1"

repositories {
    mavenCentral()
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "com.hseungho.util"
            artifactId = "java-sql-generator"
            version = "0.0.1"
            from(components["java"])
        }
    }
}
