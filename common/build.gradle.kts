plugins {
    id("dev.architectury.loom")
    id("architectury-plugin")
}

architectury {
    common("neoforge", "fabric")
}

loom {
    silentMojangMappingsLicense()
}

repositories {
    maven("https://maven.blamejared.com") // Journeymap API
    maven("https://maven.isxander.dev/releases") // YACL
}

dependencies {
    minecraft("com.mojang:minecraft:${property("minecraft_version")}")
    mappings(loom.officialMojangMappings())
    modImplementation("com.cobblemon:mod:${property("cobblemon_version")}") { isTransitive = false }

    // Depend on fabric loader to make mixins work
    modImplementation("net.fabricmc:fabric-loader:${property("fabric_loader_version")}")

    compileOnly("info.journeymap", "journeymap-api-common", property("journeymap_api_version") as String?)

    // Depend on fabric version here since no common version of YACL is available
    // (recommended to do this for an archloom setup on their docs)
    modImplementation("dev.isxander:yet-another-config-lib:${property("yacl_version")}-fabric")

    implementation("com.n1netails:n1netails-discord-webhook-client:0.3.0")

    testImplementation("org.junit.jupiter:junit-jupiter-api:${property("junit_version")}")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:${property("junit_version")}")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}