package com.example.quantumboots.network;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import com.example.quantumboots.quantumboots;

@EventBusSubscriber(modid = quantumboots.MODID)
public class QuantumBootsNetworking {

    @SubscribeEvent
    public static void register(RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar("1");

        registrar.playToClient(
            SeasonSyncPayload.TYPE,
            SeasonSyncPayload.STREAM_CODEC,
            ClientSeasonPayloadHandler::handle
        );
    }
}