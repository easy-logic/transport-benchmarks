plugins {
	java
	id("org.springframework.boot") version  "2.2.5.RELEASE"
	id("io.spring.dependency-management") version "1.0.9.RELEASE"
}

group = "com.luxoft"
version = "0.0.1-SNAPSHOT"

java {
	sourceCompatibility = JavaVersion.VERSION_1_8
}

repositories {
	mavenCentral()	
}

tasks.test {
	useJUnitPlatform()
}

dependencies {


	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("net.openhft:jlbh:1.0.1")
	implementation("com.konghq:unirest-java:3.4.01")

	implementation(group = "com.rabbitmq", name = "amqp-client", version = "5.8.0")
	implementation(group = "com.google.guava", name = "guava", version = "28.2-jre")

}
