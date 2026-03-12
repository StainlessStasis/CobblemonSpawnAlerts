package io.github.stainlessstasis.cobblemon_spawn_alerts.util;

import com.cobblemon.mod.common.api.pokemon.stats.Stat;
import com.cobblemon.mod.common.api.pokemon.stats.Stats;
import com.cobblemon.mod.common.pokemon.EVs;
import com.cobblemon.mod.common.pokemon.stat.CobblemonStatProvider;
import com.google.gson.*;
import io.github.stainlessstasis.cobblemon_spawn_alerts.core.CobblemonSpawnAlerts;
import io.github.stainlessstasis.cobblemon_spawn_alerts.platform.Services;
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
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class EvsUtil {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Map<Integer, EVs> cache = new HashMap<>();

    public static EVs getEVsFromYield(Map<Stat, Integer> evYield) {
        EVs finalEvYield = CobblemonStatProvider.INSTANCE.createEmptyEVs();
        for (Stat stat : evYield.keySet()) {
            finalEvYield.add(stat, evYield.get(stat));
        }
        return finalEvYield;
    }

    public static EVs getYield(int dexNumber) {
        if (cache.containsKey(dexNumber)) {
            return cache.get(dexNumber);
        }

        Optional<Resource> resource = Minecraft.getInstance().getResourceManager().getResource(ResourceLocation.fromNamespaceAndPath(CobblemonSpawnAlerts.MOD_ID, "evyields.json"));
        if (resource.isEmpty()) {
            return EVs.createEmpty();
        }

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(resource.get().open(), StandardCharsets.UTF_8));
            JsonElement jsonElement = JsonParser.parseReader(reader);
            JsonObject rootObject = jsonElement.getAsJsonObject();
            if (rootObject.has(String.valueOf(dexNumber))) {
                JsonObject evYieldObject = rootObject.getAsJsonObject(String.valueOf(dexNumber));
                EVYield evYield = GSON.fromJson(evYieldObject, EVYield.class);
                EVs evs = EVs.createEmpty();
                evs.set(Stats.HP, evYield.hp);
                evs.set(Stats.ATTACK, evYield.attack);
                evs.set(Stats.DEFENCE, evYield.defence);
                evs.set(Stats.SPECIAL_ATTACK, evYield.special_attack);
                evs.set(Stats.SPECIAL_DEFENCE, evYield.special_defence);
                evs.set(Stats.SPEED, evYield.speed);
                cache.put(dexNumber, evs);
                return evs;
            }
        } catch (IOException e) {
            CobblemonSpawnAlerts.LOGGER.error("Could not create reader for file `evyields.json`: "+e);
        }

        return EVs.createEmpty();
    }

    public static void clearCache() {
        cache.clear();
    }

    /** This is just a dev tool to create a file containing all the default EV yields<br>
     * Do NOT use for any other reason
     * @throws IOException
     */
    public static void loadEVYieldsToFile() throws IOException {
        if (!Services.PLATFORM.isDevelopmentEnvironment()) {
            return;
        }

        System.out.println("LOADING ALL EV YIELDS");
        ResourceManager resourceManager = Minecraft.getInstance().getSingleplayerServer().getResourceManager();
        System.out.println("RESOURCE MANAGER: "+resourceManager);

        Map<Integer, EVYield> loadedEVYields = new HashMap<>();
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
                int dexNumber = rootObject.get("nationalPokedexNumber").getAsInt();
                EVYield evYield = GSON.fromJson(evYieldObject, EVYield.class);
                loadedEVYields.put(dexNumber, evYield);
                System.out.println("Successfully parsed EVs for " + location.getPath() + ": " + evYield);
            }
            reader.close();
        }

        System.out.println(loadedEVYields);

        String output = GSON.toJson(loadedEVYields);
        Path outputFile = Paths.get("evyields.json");
        FileWriter writer = new FileWriter(outputFile.toFile());
        writer.write(output);
        writer.close();
    }

    private record EVYield(int hp, int attack, int defence, int special_attack, int special_defence, int speed) {}

    public static void print(EVs evs) {
        evs.iterator().forEachRemaining(ev -> {
            CobblemonSpawnAlerts.LOGGER.info(ev.getKey().getDisplayName()+" | "+ev.getValue());
        });
    }
}
