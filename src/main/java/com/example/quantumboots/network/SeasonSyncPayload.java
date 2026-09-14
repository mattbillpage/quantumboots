package com.example.quantumboots.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import com.example.quantumboots.quantumboots;

public record SeasonSyncPayload(int currentDay) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<SeasonSyncPayload> TYPE =
        new CustomPacketPayload.Type<>(
            Identifier.fromNamespaceAndPath(quantumboots.MODID, "season_sync")
        );

    public static final StreamCodec<ByteBuf, SeasonSyncPayload> STREAM_CODEC =
        StreamCodec.composite(
            ByteBufCodecs.VAR_INT,
            SeasonSyncPayload::currentDay,
            SeasonSyncPayload::new
        );

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}