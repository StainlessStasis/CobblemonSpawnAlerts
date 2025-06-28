plugins {
	id("java")
	id("dev.architectury.loom") version("1.7-SNAPSHOT")
	id("architectury-plugin") version("3.4-SNAPSHOT")
	kotlin("jvm") version "1.9.23"
}

group = "io.github.stainlessstasis"
version = "1.4"

architectury {
	platformSetupLoomIde()
	fabric()
}

loom {
	silentMojangMappingsLicense()

	mixin {
		defaultRefmapName.set("mixins.${project.name}.refmap.json")
	}
	accessWidenerPath = file("src/main/resources/cobblemon-spawn-alerts.accesswidener")
}

repositories {
	mavenCentral()
	maven(url = "https://dl.cloudsmith.io/public/geckolib3/geckolib/maven/")
	maven("https://maven.impactdev.net/repository/development/")
	maven("https://oss.sonatype.org/content/repositories/snapshots")
}

dependencies {
	minecraft("net.minecraft:minecraft:1.21.1")
	mappings(loom.officialMojangMappings())
	modImplementation("net.fabricmc:fabric-loader:0.16.5")

	modImplementation("net.fabricmc.fabric-api:fabric-api:0.116.3+1.21.1")
	modImplementation(fabricApi.module("fabric-command-api-v2", "0.104.0+1.21.1"))

	modImplementation("net.fabricmc:fabric-language-kotlin:1.12.3+kotlin.2.0.21")
	modImplementation("com.cobblemon:fabric:1.6.1+1.21.1")

	modImplementation(include("net.kyori:adventure-platform-fabric:5.14.1")!!)

	testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.0")
	testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.10.0")
}

tasks.getByName<Test>("test") {
	useJUnitPlatform()
}

tasks.processResources {
	inputs.property("version", project.version)

	filesMatching("fabric.mod.json") {
		expand(project.properties)
	}
}