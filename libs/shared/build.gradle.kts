plugins {
  kotlin("jvm")
}

repositories {
  mavenCentral()
}

dependencies {
  implementation(platform("software.amazon.awssdk:bom:2.25.64"))
  implementation("software.amazon.awssdk:dynamodb")
  implementation("software.amazon.awssdk:ssm")
  implementation(libs.jackson.module.kotlin)
}
