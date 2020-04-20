import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val ktor_version: String by project
val kotlin_version: String by project
val h2_version: String by project
val exposed_version: String by project
val HikariCP_version: String by project
val logback_version: String by project
val koin_version: String by project

plugins {
    java
    kotlin("jvm") version "1.3.71"
    application
    id("org.flywaydb.flyway") version "5.2.4"
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

sourceSets {
    test {
        resources {
            srcDir("src/test/resources")
        }
    }
}

repositories {
    jcenter()
    mavenCentral()
    maven { url = uri("https://kotlin.bintray.com/ktor") }
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlin_version")

    implementation("io.ktor:ktor-server-netty:$ktor_version")
    implementation("io.ktor:ktor-jackson:$ktor_version")
    implementation("io.ktor:ktor-auth-jwt:$ktor_version")
    implementation("io.ktor:ktor-websockets:$ktor_version")

    implementation("com.h2database:h2:$h2_version")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposed_version")
    implementation("org.jetbrains.exposed:exposed-core:$exposed_version")
    implementation("org.jetbrains.exposed:exposed-java-time:$exposed_version")
    implementation("com.zaxxer:HikariCP:$HikariCP_version")
    implementation("org.postgresql:postgresql:42.2.2")
    implementation("org.flywaydb:flyway-core:5.2.4")

    implementation("ch.qos.logback:logback-classic:$logback_version")
    implementation("org.koin:koin-ktor:$koin_version")
    implementation("org.koin:koin-logger-slf4j:$koin_version")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.10.3")

    implementation("org.reflections:reflections:0.9.12")

    implementation("com.google.firebase:firebase-admin:6.12.2")

    testImplementation("org.koin:koin-test:$koin_version")
    testImplementation("org.assertj:assertj-core:3.15.0")
    testImplementation("io.rest-assured:rest-assured:4.2.0")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.5.2")
    testImplementation("io.ktor:ktor-server-test-host:$ktor_version")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.5.2")

}

flyway {
    url = System.getenv("DB_URL")
    user = System.getenv("DB_USER")
    password = System.getenv("DB_PASSWORD")
    baselineOnMigrate=true
    locations = arrayOf("filesystem:resources/db/migrations")
}
