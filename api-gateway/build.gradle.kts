plugins {
	java
	id("org.springframework.boot") version "3.4.3"
	id("io.spring.dependency-management") version "1.1.7"
}

group = "fr.matteofierquin.apigateway"
version = "0.0.1-SNAPSHOT"
description = "API Gateway for SpringSuite"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

repositories {
	mavenCentral()
}

dependencies {
	// Spring Cloud Gateway
	implementation("org.springframework.cloud:spring-cloud-starter-gateway")

	// Eureka Client
	implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-client")

	// Spring Security
	implementation("org.springframework.boot:spring-boot-starter-security")

	// JWT Validation
	implementation("io.jsonwebtoken:jjwt-api:0.12.6")
	runtimeOnly("io.jsonwebtoken:jjwt-impl:0.12.6")
	runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.12.6")

	// Actuator
	implementation("org.springframework.boot:spring-boot-starter-actuator")

	// Lombok
	compileOnly("org.projectlombok:lombok")
	annotationProcessor("org.projectlombok:lombok")

	// Test
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.security:spring-security-test")
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
