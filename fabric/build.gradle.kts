plugins {
    id("dev.architectury.loom")
    id("architectury-plugin")
    id("com.gradleup.shadow") version "9.3.2"
}

architectury {
    platformSetupLoomIde()
    fabric()
}

loom {
    runs {
        named("client") {
            runDir = "runs/client"
            client()
        }
        named("server") {
            runDir = "runs/server"
            server()
        }
    }
}

val shadowCommon = configurations.create("shadowCommon")

repositories {
    maven("https://maven.blamejared.com") // Journeymap API
    maven("https://www.cursemaven.com")
}

dependencies {
    minecraft("com.mojang:minecraft:${property("minecraft_version")}")
    mappings(loom.officialMojangMappings())
    modImplementation("net.fabricmc:fabric-loader:${property("fabric_loader_version")}")

    modApi("net.fabricmc.fabric-api:fabric-api:${property("fabric_api_version")}")
    modImplementation(fabricApi.module("fabric-command-api-v2", property("fabric_api_version").toString()))

    modImplementation("net.fabricmc:fabric-language-kotlin:${property("fabric_kotlin")}")
    modImplementation("com.cobblemon:fabric:${property("cobblemon_version")}") { isTransitive = false }

    modImplementation("net.tysontheember.emberstextapi:emberstextapi-fabric-1.21.1:2.5.0")

    modImplementation("info.journeymap", "journeymap-api-fabric", property("journeymap_api_version") as String?)
    modRuntimeOnly("curse.maven:journeymap-${property("journeymap_project_id")}:${property("journeymap_fabric_file_id")}")
    modRuntimeOnly("mysticdrew:common-networking-fabric:${property("common_networking_version")}")

    implementation("com.n1netails:n1netails-discord-webhook-client:0.3.0")
    shadowCommon("com.n1netails:n1netails-discord-webhook-client:0.3.0") {
        isTransitive = true
    }

    implementation(project(":common", configuration = "namedElements"))
    "developmentFabric"(project(":common", configuration = "namedElements"))
    shadowCommon(project(":common", configuration = "transformProductionFabric"))

    testImplementation("org.junit.jupiter:junit-jupiter-api:${property("junit_version")}")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:${property("junit_version")}")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}

tasks.processResources {
    inputs.property("version", rootProject.version)

    filesMatching("fabric.mod.json") {
        expand(rootProject.properties)
    }
}

tasks {

    jar {
        archiveBaseName.set("${rootProject.property("archives_base_name")}-${project.name}")
        archiveClassifier.set("dev-slim")
    }

    shadowJar {
        archiveClassifier.set("dev-shadow")
        archiveBaseName.set("${rootProject.property("archives_base_name")}-${project.name}")

        configurations = listOf(shadowCommon)
    }

    remapJar {
        dependsOn(shadowJar)
        inputFile.set(shadowJar.flatMap { it.archiveFile })
        archiveBaseName.set("${rootProject.property("archives_base_name")}-${project.name}")
        archiveVersion.set("${rootProject.version}")
    }
}