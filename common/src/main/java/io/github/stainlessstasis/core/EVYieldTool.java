package io.github.stainlessstasis.core;

import com.cobblemon.mod.common.pokemon.EVs;
import com.google.gson.*;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

// This is just a dev tool to create a file containing all the default EV yields
public class EVYieldTool {
    public static void loadEVYields() throws IOException, JsonSyntaxException {
        final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

        System.out.println("LOADING ALL EV YIELDS");
        ResourceManager resourceManager = Minecraft.getInstance().getSingleplayerServer().getResourceManager();
        System.out.println("RESOURCE MANAGER: "+resourceManager);

        Map<String, EVYield> loadedEVYields = new HashMap<>();
        Map<ResourceLocation, Resource> speciesJsonLocations = resourceManager.listResources("species", location ->
                location.getNamespace().equals("cobblemon") && location.getPath().endsWith(".json")
        );

        System.out.println("JSON FILES: "+speciesJsonLocations);

        for (ResourceLocation location : speciesJsonLocations.keySet()) {
            System.out.println("RESOURCE LOCATION: "+location);
            Resource resource = resourceManager.getResource(location).get();
            BufferedReader reader = new BufferedReader(new InputStreamReader(resource.open(), StandardCharsets.UTF_8));
            JsonElement jsonElement = JsonParser.parseReader(reader);
            JsonObject rootObject = jsonElement.getAsJsonObject();

            if (rootObject.has("evYield")) {
                JsonObject evYieldObject = rootObject.getAsJsonObject("evYield");
                String name = rootObject.get("name").getAsString();
                EVYield evYield = GSON.fromJson(evYieldObject, EVYield.class);
                loadedEVYields.put(name, evYield);
                System.out.println("Successfully parsed EVs for " + location.getPath() + ": " + evYield);
            }
            reader.close();
        }

        System.out.println(loadedEVYields);

        String output = GSON.toJson(loadedEVYields);
        Path outputFile = Paths.get("evYields.json");
        FileWriter writer = new FileWriter(outputFile.toFile());
        writer.write(output);
        writer.close();
    }

    private record EVYield(int hp, int attack, int defence, int special_attack, int special_defence, int speed) {}
}
