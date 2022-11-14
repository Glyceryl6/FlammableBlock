package com.glyceryl6.flaming.commands.server;

import com.glyceryl6.flaming.FlammableBlock;
import com.glyceryl6.flaming.json.JsonHandler;
import com.glyceryl6.flaming.json.JsonRemoval;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.*;
import net.minecraft.world.level.block.*;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.registries.ForgeRegistries;

import java.io.*;
import java.nio.file.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CommandCreateBlockList {

    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss");

    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return Commands.literal("export").requires((commandSource) -> commandSource.hasPermission(2))
                .then(Commands.argument("namespace", StringArgumentType.word())
                .executes((commandSource) -> createList(commandSource.getSource(),
                StringArgumentType.getString(commandSource, "namespace"))));
    }

    @SuppressWarnings("deprecation")
    private static int createList(CommandSourceStack source, String namespace) {
        createDirectory();
        JsonHandler.reload();
        JsonHandler.addToList();
        JsonRemoval.removeFromList();
        String s = DATE_FORMAT.format(new Date());
        FireBlock fireblock = (FireBlock) Blocks.FIRE;
        File blockListFile = new File(FMLPaths.CONFIGDIR.get() + "/flammable_block/" + namespace + "_" + s + ".csv");
        try (FileWriter fileWriter = new FileWriter(blockListFile)) {
            blockListFile.createNewFile();
            fileWriter.write(new TranslatableComponent("flaming.info.file.block_name").getString() + ",");
            fileWriter.append(new TranslatableComponent("flaming.info.file.registry_name").getString()).append(",");
            fileWriter.append(new TranslatableComponent("flaming.info.file.can_burn").getString()).append("\n");
            for (Block block : ForgeRegistries.BLOCKS) {
                if (block != null && block.getRegistryName() != null) {
                    String blockName = block.getName().getString();
                    blockName = blockName.replaceAll("\\[", "").replaceAll("]", "");
                    boolean canCatchFire = fireblock.getFlameOdds(block.defaultBlockState()) > 0;
                    if (namespace.equals("all")) {
                        String registryName = block.getRegistryName().toString();
                        String content = blockName + "," + registryName + "," + canCatchFire + "\n";
                        fileWriter.write(content);
                        fileWriter.flush();
                    } else {
                        if (ModList.get().isLoaded(namespace)) {
                            String id = block.getRegistryName().getNamespace();
                            if (id.equals(namespace)) {
                                String path = block.getRegistryName().getPath();
                                String content = blockName + "," + id + ":" + path + "," + canCatchFire + "\n";
                                fileWriter.write(content);
                                fileWriter.flush();
                            }
                        } else {
                            source.sendSuccess(new TranslatableComponent("flaming.info.file.invalid"), true);
                            fileWriter.close();
                            blockListFile.delete();
                            break;
                        }
                    }
                }
            }
            if (blockListFile.exists()) {
                String fileName = blockListFile.getName();
                Component component = (new TextComponent(fileName))
                        .withStyle(ChatFormatting.UNDERLINE).withStyle(ChatFormatting.YELLOW).withStyle((style)
                        -> style.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, blockListFile.getAbsolutePath())));
                source.sendSuccess(new TranslatableComponent("flaming.info.file.output", component), true);
            }
        } catch (IOException ignored) {}
        return 0;
    }

    private static void createDirectory() {
        Path configPath = FMLPaths.CONFIGDIR.get().toAbsolutePath();
        Path compostConfigPath = Paths.get(configPath.toString(), FlammableBlock.MOD_ID);
        try {Files.createDirectory(compostConfigPath);
        } catch (IOException ignored) {}
    }

}
