package com.glyceryl6.flaming.commands.server;

import com.glyceryl6.flaming.json.JsonHandler;
import com.glyceryl6.flaming.json.JsonRemoval;
import com.glyceryl6.flaming.json.objects.RemoveEntry;
import com.mojang.brigadier.builder.ArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.blocks.BlockInput;
import net.minecraft.commands.arguments.blocks.BlockStateArgument;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.block.Block;

import java.util.Objects;

public class CommandRemoveEntry {

    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return Commands.literal("remove").requires((commandSource) -> commandSource.hasPermission(2))
                .then(Commands.argument("block", BlockStateArgument.block())
                .executes((commandSource) -> removeEntry(commandSource.getSource(),
                BlockStateArgument.getBlock(commandSource, "block"))));
    }

    private static int removeEntry(CommandSourceStack source, BlockInput blockInput) {
        Block block = blockInput.getState().getBlock();
        String blockName = block.getName().getString();
        String entryName = Objects.requireNonNull(block.getRegistryName()).toString();
        if (JsonHandler.removeEntry(entryName)) {
            JsonRemoval.addEntry(new RemoveEntry(entryName));
            JsonHandler.addToList();
            JsonRemoval.removeFromList();
            source.sendSuccess(new TranslatableComponent("flaming.info.remove.success", blockName), true);
        } else {
            source.sendSuccess(new TranslatableComponent("flaming.info.remove.fail"), true);
        }

        return 0;
    }

}