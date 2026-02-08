plugins {
  id("org.springframework.boot")
  id("io.spring.dependency-management")
  kotlin("jvm")
  kotlin("plugin.spring")
}

dependencies {
  implementation(project(":libs:shared"))

  implementation("org.springframework.boot:spring-boot-starter")
  implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

  implementation(platform("software.amazon.awssdk:bom:2.25.64"))
  implementation("software.amazon.awssdk:ssm")
  implementation("software.amazon.awssdk:sqs")
  implementation("software.amazon.awssdk:dynamodb")
  implementation("software.amazon.awssdk:regions")

  testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.named<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") {
  enabled = false
}
tasks.named<Jar>("jar") {
  enabled = true
}
