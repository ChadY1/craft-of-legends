package com.craftoflegends.champion;

import java.util.Collections;
import java.util.Map;

public class Champion {
    private final String name;
    private final Map<String, Ability> abilities;

    public Champion(String name, Map<String, Ability> abilities) {
        this.name = name;
        this.abilities = abilities;
    }

    public String getName() {
        return name;
    }

    public Map<String, Ability> getAbilities() {
        return Collections.unmodifiableMap(abilities);
    }

    public Ability getAbility(String abilityName) {
        return abilities.get(abilityName.toLowerCase());
    }
}
