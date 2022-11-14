package com.glyceryl6.flaming.commands.server;

import com.glyceryl6.flaming.json.JsonHandler;
import com.glyceryl6.flaming.json.JsonRemoval;
import com.glyceryl6.flaming.json.objects.BlockListEntry;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.blocks.BlockInput;
import net.minecraft.commands.arguments.blocks.BlockStateArgument;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.Block;

import java.util.Objects;

public class CommandAddEntry {

    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return Commands.literal("add").requires((commandSource) -> commandSource.hasPermission(2))
                .then(Commands.argument("block", BlockStateArgument.block())
                .then(Commands.argument("fireSpreadSpeed", IntegerArgumentType.integer(1))
                .then(Commands.argument("flammability", IntegerArgumentType.integer(1))
                .executes((commandSource) -> addEntry(commandSource.getSource(),
                BlockStateArgument.getBlock(commandSource, "block"),
                IntegerArgumentType.getInteger(commandSource, "fireSpreadSpeed"),
                IntegerArgumentType.getInteger(commandSource, "flammability"))))));
    }

    private static int addEntry(CommandSourceStack source, BlockInput blockInput, int fireSpreadSpeed, int flammability) {
        Block block = blockInput.getState().getBlock();
        String blockName = block.getName().getString();
        String entryName = Objects.requireNonNull(block.getRegistryName()).toString();
        BlockListEntry blockListEntry = new BlockListEntry(entryName, fireSpreadSpeed, flammability);
        if (!block.defaultBlockState().isAir() && !(block instanceof BaseFireBlock)) {
            if (JsonHandler.addEntry(blockListEntry)) {
                JsonRemoval.removeEntry(entryName);
                source.sendSuccess(new TranslatableComponent("flaming.info.add.success", blockName), true);
            } else {
                source.sendSuccess(new TranslatableComponent("flaming.info.add.fail"), true);
            }
        } else {
            source.sendSuccess(new TranslatableComponent("flaming.info.add.error", blockName), true);
        }

        return 0;
    }

}