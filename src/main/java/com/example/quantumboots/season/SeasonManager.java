package com.example.quantumboots.season;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

public class SeasonManager extends SavedData {
    private static final int DAYS_PER_SEASON = 7;

    // The new persistence descriptor: identifier + constructor + codec.
    public static final SavedDataType<SeasonManager> TYPE = new SavedDataType<>(
        Identifier.fromNamespaceAndPath("quantumboots", "seasons_manager"),
        SeasonManager::new, // no-arg constructor for a brand-new save
        RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("currentDay").forGetter(sd -> sd.currentDay)
        ).apply(instance, SeasonManager::new))
    );

    private int currentDay;

    public enum Season { SPRING, SUMMER, AUTUMN, WINTER }

    // No-arg constructor: used when no save file exists yet.
    public SeasonManager() {
        this.currentDay = 0;
    }

    // Data constructor: used by the codec when loading from disk.
    public SeasonManager(int currentDay) {
        this.currentDay = currentDay;
    }

    public static Season seasonFromDay(int day) {
        int seasonIndex = (day / DAYS_PER_SEASON) % 4;
        return Season.values()[seasonIndex];
    }

    public Season getSeason() {
        return seasonFromDay(currentDay);
    }

    public int getCurrentDay() {
        return currentDay;
    }

    public void tick(long worldTimeDay) {
        int newDay = (int) worldTimeDay;
        if (newDay != currentDay) {
            currentDay = newDay;
            setDirty();
        }
    }

    public static SeasonManager get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(TYPE);
    }
}