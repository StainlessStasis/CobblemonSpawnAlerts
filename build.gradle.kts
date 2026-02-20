plugins {
    id("java")
    id("java-library")
    kotlin("jvm") version("2.2.20")

    id("dev.architectury.loom") version ("1.13.467") apply false
    id("architectury-plugin") version("3.4.162") apply false
}

allprojects {
    apply(plugin = "java")
    apply(plugin = "org.jetbrains.kotlin.jvm")

    version = project.properties["mod_version"]!!
    group = project.properties["maven_group"]!!

    repositories {
        mavenCentral()
        maven("https://dl.cloudsmith.io/public/geckolib3/geckolib/maven/")
        maven("https://maven.impactdev.net/repository/development/")
        maven("https://maven.neoforged.net/releases")
        maven("https://thedarkcolour.github.io/KotlinForForge/")
    }

    tasks.getByName<Test>("test") {
        useJUnitPlatform()
    }

    java {
        withSourcesJar()
    }

    tasks.processResources {
        filesMatching(listOf("**/*.mods.toml", "pack.mcmeta", "fabric.mod.json", "*.mixins.json")) {
            expand(project.properties)
        }
    }
}

