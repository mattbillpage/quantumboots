package com.example.quantumboots.network;

import com.example.quantumboots.season.SeasonManager;
import com.example.quantumboots.season.SeasonManager.Season;

public class ClientSeasonPayloadHandler {

    private static volatile int clientCurrentDay = 0;

    public static void handle(final SeasonSyncPayload payload, final net.neoforged.neoforge.network.handling.IPayloadContext context) {
        context.enqueueWork(() -> {
            Season oldSeason = SeasonManager.seasonFromDay(clientCurrentDay);
            clientCurrentDay = payload.currentDay();
            Season newSeason = SeasonManager.seasonFromDay(clientCurrentDay);

            if (oldSeason != newSeason) {
                forceChunkRerender();
            }
        });
    }

    private static void forceChunkRerender() {
        net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getInstance();
        if (mc.level != null) {
            mc.levelRenderer.invalidateCompiledGeometry(
                mc.level,
                mc.options,
                mc.gameRenderer.mainCamera(),
                mc.getBlockColors()
            );
        }
    }

    public static int getClientCurrentDay() {
        return clientCurrentDay;
    }

    public static SeasonManager.Season getClientSeason() {
        return SeasonManager.seasonFromDay(clientCurrentDay);
    }
}