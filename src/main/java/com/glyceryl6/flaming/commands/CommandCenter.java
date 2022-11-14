package com.glyceryl6.flaming.commands;

import com.glyceryl6.flaming.commands.server.*;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;

public class CommandCenter {

    public CommandCenter(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(LiteralArgumentBuilder
                .<CommandSourceStack> literal("flaming")
                .then(CommandAddEntry.register())
                .then(CommandReloadJSON.register())
                .then(CommandRemoveEntry.register())
                .then(CommandCreateBlockList.register())
                .executes(ctx -> 0));
    }

}