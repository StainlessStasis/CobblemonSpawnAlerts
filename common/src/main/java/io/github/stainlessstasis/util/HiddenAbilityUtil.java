package io.github.stainlessstasis.util;

import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.api.abilities.Abilities;
import com.cobblemon.mod.common.api.abilities.AbilityTemplate;
import com.cobblemon.mod.common.api.abilities.PotentialAbility;
import com.cobblemon.mod.common.api.pokemon.PokemonSpecies;
import com.cobblemon.mod.common.pokemon.Species;
import com.cobblemon.mod.common.pokemon.abilities.HiddenAbilityType;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class HiddenAbilityUtil {
    public static boolean hasHiddenAbility(Species species, String abilityID) {
        final int[] numAbilities = {0};
        Set<String> abilities = new HashSet<>();
        species.getAbilities().iterator().forEachRemaining(potentialAbility -> {
            String abilityName = potentialAbility.getTemplate().getName();
            if (abilities.contains(abilityName)) {
                return;
            }
            abilities.add(abilityName);
            numAbilities[0]++;
        });

        // If it only has one ability it will ALWAYS be HA, so ignore it
        if (numAbilities[0] < 2) {
            return false;
        }

        Set<String> hiddenAbilityNames =
                species.getAbilities().getMapping().values().stream()
                        .flatMap(List::stream)
                        .filter(ability -> ability.getType() == HiddenAbilityType.INSTANCE)
                        .map(PotentialAbility::getTemplate)
                        .map(AbilityTemplate::getName)
                        .collect(Collectors.toSet());

        AbilityTemplate ability = Abilities.INSTANCE.get(abilityID);
        if (ability == null) {
            return false;
        }

        String abilityName = ability.getName();
        return hiddenAbilityNames.contains(abilityName);
    }

    public static boolean hasHiddenAbility(int dexID, String abilityID) {
        Species species = PokemonSpecies.INSTANCE.getByPokedexNumber(dexID, Cobblemon.MODID);
        if (species == null) {
            return false;
        }

        return hasHiddenAbility(species, abilityID);
    }
}
