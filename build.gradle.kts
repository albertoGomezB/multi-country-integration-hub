import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.api.plugins.JavaPluginExtension

plugins {
  kotlin("jvm") version "2.0.10" apply false
  kotlin("plugin.spring") version "2.0.10" apply false

  id("org.springframework.boot") version "3.4.1" apply false
  id("io.spring.dependency-management") version "1.1.6" apply false
}

allprojects {
  group = "com.agb.integrationhub"
  version = "0.1.0"

  repositories {
    mavenCentral()
  }
}

subprojects {
  plugins.withId("org.jetbrains.kotlin.jvm") {
    extensions.configure<KotlinJvmProjectExtension> {
      jvmToolchain(21)
    }
  }

  plugins.withId("java") {
    extensions.configure<JavaPluginExtension> {
      toolchain.languageVersion.set(JavaLanguageVersion.of(21))
    }
  }
}
