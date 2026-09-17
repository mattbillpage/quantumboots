package com.example.quantumboots.season;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import com.example.quantumboots.quantumboots;
import com.example.quantumboots.network.SeasonSyncPayload;

@EventBusSubscriber(modid = quantumboots.MODID)
public class SeasonEvents {

    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Post event) {
        for (ServerLevel level : event.getServer().getAllLevels()) {
            if (level.dimension() == Level.OVERWORLD) {
                SeasonManager manager = SeasonManager.get(level);
                int before = manager.getCurrentDay();
                manager.tick(level.getGameTime());
                if (manager.getCurrentDay() != before) {
                    PacketDistributor.sendToAllPlayers(new SeasonSyncPayload(manager.getCurrentDay()));
                }
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            ServerLevel overworld = player.level().getServer().overworld();
            SeasonManager manager = SeasonManager.get(overworld);
            PacketDistributor.sendToPlayer(player, new SeasonSyncPayload(manager.getCurrentDay()));
    }
}
}