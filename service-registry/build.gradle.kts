plugins {
	java
	id("org.springframework.boot") version "3.4.3"
	id("io.spring.dependency-management") version "1.1.7"
}

group = "fr.matteofierquin.serviceregistry"
version = "0.0.1-SNAPSHOT"
description = "Eureka Service Registry for SpringSuite"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

repositories {
	mavenCentral()
}

dependencies {
	// Eureka Server
	implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-server")
	
	// Actuator for monitoring
	implementation("org.springframework.boot:spring-boot-starter-actuator")
	
	// Test
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

dependencyManagement {
	imports {
		mavenBom("org.springframework.cloud:spring-cloud-dependencies:2024.0.0")
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}
