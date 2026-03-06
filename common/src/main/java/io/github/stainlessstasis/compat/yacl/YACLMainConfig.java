package io.github.stainlessstasis.compat.yacl;

import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder;
import io.github.stainlessstasis.config.client.MainConfig;
import io.github.stainlessstasis.core.CobblemonSpawnAlerts;
import io.github.stainlessstasis.core.CobblemonSpawnAlertsClient;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.lang.reflect.Field;
import java.lang.reflect.RecordComponent;

public class YACLMainConfig {
    public static Screen createScreen(Screen parent) {
        return new YACLMainConfig().createNewScreen(parent);
    }

    private YACLMainConfig() {}

    private MainConfig originalConfig;
    boolean debug;
    boolean multiplayerWarning;
    boolean versionChangeWarning;
    boolean enableAlerts;
    boolean enableDespawnAlerts;
    boolean enableSounds;
    boolean alertAllShinies;
    boolean alertAllHiddenAbility;
    boolean alertAllLegendaries;
    boolean alertAllMythicals;
    boolean alertAllUltraBeasts;
    boolean alertAllParadox;
    boolean alertAllStarter;
    boolean alertAllNotInDex;
    boolean alertAllUncaught;
    boolean alertEverything;

    public Screen createNewScreen(Screen parent) {
        var config = CobblemonSpawnAlertsClient.CLIENT_CONFIG_MANAGER.getMainConfig();
        if (config == null) config = MainConfig.createDefault();
        this.originalConfig = config;
        MainConfig defaults = MainConfig.createDefault();

        ConfigCategory.Builder categoryBuilder = ConfigCategory.createBuilder()
                .name(Component.literal("Main Config (client)"));

        OptionGroup.Builder groupBuilder = OptionGroup.createBuilder()
                .name(Component.literal("Options"));

        for (RecordComponent component : MainConfig.class.getRecordComponents()) {
            String name = component.getName();

            if (name.equals("configVersion") || name.equals("comment")) continue;

            try {
                Object value = component.getAccessor().invoke(config);
                Field field = this.getClass().getDeclaredField(name);
                field.setAccessible(true);
                field.set(this, value);

                Object defaultValue = component.getAccessor().invoke(defaults);

                if (component.getType() == boolean.class) {
                    groupBuilder.option(Option.<Boolean>createBuilder()
                            .name(Component.literal(formatName(name)))
                            .description(OptionDescription.of(Component.literal("Automatically generated option for '" +name+ "'. For more information, see the docs at: stainlessstasis.github.io/CSA-Docs/config")))
                            .binding(
                                    (Boolean) defaultValue,
                                    () -> {
                                        try {
                                            return field.getBoolean(this);
                                        } catch (Exception e) { return false; }
                                    },
                                    (newVal) -> { try
                                        {
                                            field.setBoolean(this, newVal);
                                        } catch (Exception ignored) {}
                                    }
                            )
                            .controller(TickBoxControllerBuilder::create)
                            .build());
                }

            } catch (NoSuchFieldException e) {
                CobblemonSpawnAlerts.LOGGER.info("Skipping UI option for: {} - Field not defined in UI class", name);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return YetAnotherConfigLib.createBuilder()
                .title(Component.literal("Main Config"))
                .category(categoryBuilder.group(groupBuilder.build()).build())
                .save(this::save)
                .build()
                .generateScreen(parent);
    }

    private String formatName(String name) {
        return Character.toUpperCase(name.charAt(0)) + name.substring(1).replaceAll("([A-Z])", " $1");
    }

    private void save() {
        var original = this.originalConfig;
        var config = new MainConfig
        (
             original.configVersion(), original.comment(),
             debug, multiplayerWarning, versionChangeWarning, enableAlerts, enableDespawnAlerts, enableSounds,
             alertAllShinies, alertAllHiddenAbility, alertAllLegendaries, alertAllMythicals, alertAllUltraBeasts, alertAllParadox,
             alertAllStarter, original.bucketsToAlert(), alertAllNotInDex, alertAllUncaught, alertEverything,
             original.ivHunting(), original.evHunting(), original.levelFilter(), original.distanceFilter()
        );
        CobblemonSpawnAlertsClient.CLIENT_CONFIG_MANAGER.reloadMainConfig(config);
    }


//    public Screen createNewScreen(Screen parent) {
//        var config = CobblemonSpawnAlertsClient.CLIENT_CONFIG_MANAGER.getMainConfig();
//        if (config == null) {
//            config = MainConfig.createDefault();
//        }
//
//        debug = config.debug();
//        multiplayerWarning = config.multiplayerWarning();
//        versionChangeWarning = config.versionChangeWarning();
//        enableAlerts = config.enableAlerts();
//        enableDespawnAlerts = config.enableDespawnAlerts();
//        enableSounds = config.enableSounds();
//        alertAllShinies = config.alertAllShinies();
//        alertAllHA = config.alertAllHA();
//        alertAllLegendaries = config.alertAllLegendaries();
//        alertAllMythicals = config.alertAllMythicals();
//        alertAllUltraBeasts = config.alertAllUltraBeasts();
//        alertAllParadox = config.alertAllParadox();
//        alertAllStarter = config.alertAllStarter();
//        alertAllNotInDex = config.alertAllNotInDex();
//        alertAllUncaught = config.alertAllUncaught();
//        alertEverything = config.alertEverything();
//
//        return YetAnotherConfigLib.createBuilder()
//                .title(Component.literal("Main Config (client)"))
//                .category(ConfigCategory.createBuilder()
//                        .name(Component.literal("Main Config (client)"))
//                        .tooltip(Component.literal("General config options for what groups of Pokemon to alert."))
//                        .group(OptionGroup.createBuilder()
//                                .name(Component.literal("General"))
//                                .description(OptionDescription.of(Component.literal("General config options.")))
//                                .option(Option.<Boolean>createBuilder()
//                                        .name(Component.literal("Debug"))
//                                        .description(OptionDescription.of(Component.literal("Enables debug messages in chat when an alert attempts to trigger.")))
//                                        .binding(true, () -> this.debug, b -> this.debug = b)
//                                        .controller(TickBoxControllerBuilder::create)
//                                        .build())
//                                .option(Option.<Boolean>createBuilder()
//                                        .name(Component.literal("Multiplayer Warning"))
//                                        .description(OptionDescription.of(Component.literal("Shows a warning message in chat when joining a server.")))
//                                        .binding(true, () -> this.multiplayerWarning, b -> this.multiplayerWarning = b)
//                                        .controller(TickBoxControllerBuilder::create)
//                                        .build())
//                                .option(Option.<Boolean>createBuilder()
//                                        .name(Component.literal("Version Change Warning"))
//                                        .description(OptionDescription.of(Component.literal("Shows a warning message in chat when the mod's version changes. Used to notify of important/breaking changes. Recommended to leave enabled.")))
//                                        .binding(true, () -> this.versionChangeWarning, b -> this.versionChangeWarning = b)
//                                        .controller(TickBoxControllerBuilder::create)
//                                        .build())
//                                .option(Option.<Boolean>createBuilder()
//                                        .name(Component.literal("Enable Alerts"))
//                                        .description(OptionDescription.of(Component.literal("Enables all alerts, excluding despawns.")))
//                                        .binding(true, () -> this.enableAlerts, b -> this.enableAlerts = b)
//                                        .controller(TickBoxControllerBuilder::create)
//                                        .build())
//                                .option(Option.<Boolean>createBuilder()
//                                        .name(Component.literal("Enable Despawn Alerts"))
//                                        .description(OptionDescription.of(Component.literal("Enables alert messages for when an alerted Pokemon despawns. Limited to Pokemon which have been globally alerted, thus requiring the mod to be on the server.")))
//                                        .binding(true, () -> this.enableDespawnAlerts, b -> this.enableDespawnAlerts = b)
//                                        .controller(TickBoxControllerBuilder::create)
//                                        .build())
//                                .option(Option.<Boolean>createBuilder()
//                                        .name(Component.literal("Enable Alert Sounds"))
//                                        .description(OptionDescription.of(Component.literal("Enables all custom sounds defined for the alerts.")))
//                                        .binding(true, () -> this.enableSounds, b -> this.enableSounds = b)
//                                        .controller(TickBoxControllerBuilder::create)
//                                        .build())
//                                .build()
//                            )
//                            .group(OptionGroup.createBuilder()
//                                    .name(Component.literal("Alert Groups"))
//                                    .description(OptionDescription.of(Component.literal("General groups of Pokemon to alert.")))
//                                    .option(Option.<Boolean>createBuilder()
//                                            .name(Component.literal("Alert All Shinies"))
//                                            .description(OptionDescription.of(Component.literal("Alerts when any shiny spawns, unless alertShiny is disabled in its config.")))
//                                            .binding(true, () -> this.alertAllShinies, b -> this.alertAllShinies = b)
//                                            .controller(TickBoxControllerBuilder::create)
//                                            .build())
//                                    .build()
//                            )
//                        .build()
//                    )
//                .save(this::save)
//                .build()
//            .generateScreen(parent);
//    }
}
