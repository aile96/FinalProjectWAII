import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "2.7.0"
	id("io.spring.dependency-management") version "1.0.11.RELEASE"
	kotlin("jvm") version "1.6.21"
	kotlin("plugin.spring") version "1.6.21"
	kotlin("plugin.jpa") version "1.6.21"
}

group = "it.polito.s280048"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

repositories {
	mavenCentral()
	google()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-data-r2dbc:2.7.0")
	implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-client:3.1.3")
	implementation("org.springframework.boot:spring-boot-starter-web:2.7.0")
	implementation("org.springframework.boot:spring-boot-starter-webflux:2.7.0")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.13.3")
	implementation("io.projectreactor.kotlin:reactor-kotlin-extensions:1.1.6")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:1.6.2")
	implementation("org.springframework.data:spring-data-r2dbc:1.5.0")
	implementation("org.postgresql:r2dbc-postgresql:0.9.1.RELEASE")
	implementation("org.springframework.kafka:spring-kafka")
	implementation("io.jsonwebtoken:jjwt:0.9.1")
    implementation("org.springframework.boot:spring-boot-starter-security")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
	annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
	runtimeOnly("io.r2dbc:r2dbc-postgresql:0.8.12.RELEASE")
	runtimeOnly("org.postgresql:postgresql:42.3.6")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("io.projectreactor:reactor-test")
	testImplementation("org.springframework.kafka:spring-kafka-test")
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget = "17"
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}