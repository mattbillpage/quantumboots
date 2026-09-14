package com.example.quantumboots.season;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import com.example.quantumboots.quantumboots;

@EventBusSubscriber(modid = quantumboots.MODID)
public class SeasonEvents {

    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Post event) {
        for (ServerLevel level : event.getServer().getAllLevels()) {
            if (level.dimension() == Level.OVERWORLD) {
                SeasonManager manager = SeasonManager.get(level);
                manager.tick(level.getGameTime());
            }
        }
    }
}