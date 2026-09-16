package com.example.quantumboots.network;

import com.example.quantumboots.season.SeasonManager;
import com.example.quantumboots.season.SeasonManager.Season;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;

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
        Minecraft mc = Minecraft.getInstance();

        if (mc.level == null || mc.player == null) {
            return;
        }

        BlockPos pos = mc.player.blockPosition();

        int sectionX = SectionPos.blockToSectionCoord(pos.getX());
        int sectionY = SectionPos.blockToSectionCoord(pos.getY());
        int sectionZ = SectionPos.blockToSectionCoord(pos.getZ());

        int radius = mc.options.renderDistance().get();

        mc.levelExtractor.setSectionRangeDirty(
                sectionX - radius,
                mc.level.getMinSectionY(),
                sectionZ - radius,
                sectionX + radius,
                mc.level.getMaxSectionY(),
                sectionZ + radius
        );
    }

    public static int getClientCurrentDay() {
        return clientCurrentDay;
    }

    public static SeasonManager.Season getClientSeason() {
        return SeasonManager.seasonFromDay(clientCurrentDay);
    }
}