package io.github.stainlessstasis.util;

import com.cobblemon.mod.common.api.pokemon.labels.CobblemonPokemonLabels;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.Pokemon;
import io.github.stainlessstasis.config.ServerConfig;
import io.github.stainlessstasis.core.CobblemonSpawnAlerts;

public class AlertUtil {
    public static boolean shouldGlobalAlert(PokemonEntity pokemonEntity) {
        Pokemon pokemon = pokemonEntity.getPokemon();
        ServerConfig config = CobblemonSpawnAlerts.COMMON_CONFIG_MANAGER.getServerConfig();

        boolean shouldAlertShiny = pokemon.getShiny() && config.alertShinies();
        boolean shouldAlertLegend = pokemon.isLegendary() && config.alertLegendaries();
        boolean shouldAlertMythical = pokemon.isMythical() && config.alertMythicals();
        boolean shouldAlertUltra = pokemon.isUltraBeast() && config.alertUltraBeasts();
        boolean shouldAlertParadox = pokemon.hasLabels(CobblemonPokemonLabels.PARADOX) && config.alertParadox();
        boolean shouldAlertStarter = RarityUtil.isStarter(pokemon.getSpecies().getNationalPokedexNumber()) && config.alertStarters();
        boolean shouldAlertHA = HiddenAbilityUtil.hasHiddenAbility(pokemon.getSpecies(), pokemon.getAbility().getName()) && config.alertHiddenAbility();
        return shouldAlertShiny || shouldAlertLegend || shouldAlertMythical || shouldAlertUltra || shouldAlertParadox || shouldAlertStarter || shouldAlertHA;
    }
}
