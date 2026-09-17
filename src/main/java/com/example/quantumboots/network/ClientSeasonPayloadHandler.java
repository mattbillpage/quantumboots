package com.example.quantumboots.network;

import com.example.quantumboots.season.SeasonManager;

public class ClientSeasonPayloadHandler {

    // Simple in-memory mirror of the season state on the client.
    // Not persisted — it's just re-synced whenever the server sends an update.
    private static volatile int clientCurrentDay = 0;

    public static void handle(final SeasonSyncPayload payload, final net.neoforged.neoforge.network.handling.IPayloadContext context) {
        context.enqueueWork(() -> {
            clientCurrentDay = payload.currentDay();
            System.out.println("[QuantumBoots] Client received season sync, day=" + clientCurrentDay);
        });
    }

    public static int getClientCurrentDay() {
        return clientCurrentDay;
    }

    public static SeasonManager.Season getClientSeason() {
        return SeasonManager.seasonFromDay(clientCurrentDay);
    }
}