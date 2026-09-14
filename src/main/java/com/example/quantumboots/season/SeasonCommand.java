package com.example.quantumboots.season;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import com.example.quantumboots.quantumboots;

@EventBusSubscriber(modid = quantumboots.MODID)
public class SeasonCommand {

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        dispatcher.register(Commands.literal("season")
            .executes(ctx -> {
                CommandSourceStack source = ctx.getSource();
                SeasonManager manager = SeasonManager.get(source.getLevel());
                source.sendSuccess(() -> Component.literal("Current season: " + manager.getSeason()), false);
                return 1;
            }));
    }
}

// testing to see if seasons are tracking properly!