package com.example.quantumboots.season;

import net.minecraft.client.color.block.BlockTintSource;
import net.minecraft.core.BlockPos;
import net.minecraft.util.ARGB;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;

import com.example.quantumboots.quantumboots;
import com.example.quantumboots.network.ClientSeasonPayloadHandler;
import com.example.quantumboots.season.SeasonManager.Season;


@EventBusSubscriber(modid = quantumboots.MODID, value = net.neoforged.api.distmarker.Dist.CLIENT)
public class SeasonTintHandler {

    @SubscribeEvent
    public static void registerBlockTints(RegisterColorHandlersEvent.BlockTintSources event) {
        event.register(
            java.util.List.of(new BlockTintSource() {
                @Override
                public int color(BlockState state) {
                    // Fallback when no world context is available (e.g. inventory rendering)
                    return 0xFF8CBD57; // vanilla default grass-ish green
                }

                @Override
                public int colorInWorld(BlockState state, BlockAndTintGetter level, BlockPos pos) {
                    int vanillaColor = getVanillaGrassColor(state, level, pos);
                    Season season = ClientSeasonPayloadHandler.getClientSeason();
                    return blendWithSeason(vanillaColor, season);
                }
            }),
            Blocks.GRASS_BLOCK
        );
    }

    private static int getVanillaGrassColor(BlockState state, BlockAndTintGetter level, BlockPos pos) {
        return BiomeColors.getAverageGrassColor(level, pos);
    }

    private static int blendWithSeason(int baseColor, Season season) {
        int seasonTint = switch (season) {
            case SPRING -> 0xFF8CBD57; // fresh green
            case SUMMER -> 0xFF6BA83A; // deeper green
            case AUTUMN -> 0xFFC98A3B; // orange/brown
            case WINTER -> 0xFFB8C9C9; // pale, desaturated
        };
        return ARGB.linearLerp(0.5f, baseColor, seasonTint);
    }
}