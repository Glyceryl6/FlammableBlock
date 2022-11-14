package com.glyceryl6.flaming.json;

import com.glyceryl6.flaming.FlammableBlock;
import com.glyceryl6.flaming.json.objects.BlockListConfig;
import com.glyceryl6.flaming.json.objects.BlockListEntry;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FireBlock;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.registries.ForgeRegistries;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class JsonHandler {

    private static final Gson gson = new Gson();
    private static final List<BlockListEntry> blockListEntries = new ArrayList<>();
    public static BlockListConfig blockListConfig = new BlockListConfig(blockListEntries);
    public static final File JSON_FILE = new File(FMLPaths.CONFIGDIR.get() + "/flammable_block/whitelist.json");

    public static void setup() {
        createDirectory();
        if (!JSON_FILE.exists()) {
            writeJson(JSON_FILE);
        }
        readJson(JSON_FILE);
    }

    public static void reload() {
        writeJson(JSON_FILE);
        readJson(JSON_FILE);
    }

    public static boolean containsEntry(BlockListEntry entry) {
        for (BlockListEntry blockListEntry : blockListConfig.getBlockListConfig()) {
            if (blockListEntry.getBlock().equals(entry.getBlock())) {
                return true;
            }
        }
        return false;
    }

    public static boolean addEntry(BlockListEntry entry) {
        ResourceLocation resource = new ResourceLocation(entry.getBlock());
        Block block = ForgeRegistries.BLOCKS.getValue(resource);
        FireBlock fireblock = (FireBlock) Blocks.FIRE;
        boolean flag1 = fireblock.flameOdds.containsKey(block);
        boolean flag2 = fireblock.burnOdds.containsKey(block);
        if (!containsEntry(entry) && block != null && !flag1 && !flag2) {
            blockListConfig.getBlockListConfig().add(entry);
            fireblock.setFlammable(block, entry.getFireSpreadSpeed(), entry.getFlammability());
            reload();
            return true;
        }
        return false;
    }

    public static boolean removeEntry(String entryName) {
        if (containsEntry(new BlockListEntry(entryName, 0, 0))) {
            blockListConfig.getBlockListConfig().removeIf(entry -> entry.getBlock().equals(entryName));
            reload();
            return true;
        }
        return false;
    }

    public static void writeJson(File jsonFile) {
        try (Writer writer = new FileWriter(jsonFile)) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(blockListConfig, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void readJson(File jsonFile) {
        try (Reader reader = new FileReader(jsonFile)) {
            blockListConfig = gson.fromJson(reader, BlockListConfig.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void createDirectory() {
        Path configPath = FMLPaths.CONFIGDIR.get().toAbsolutePath();
        Path compostConfigPath = Paths.get(configPath.toString(), FlammableBlock.MOD_ID);
        try {Files.createDirectory(compostConfigPath);
        } catch (IOException ignored) {}
    }

    public static void addToList() {
        FireBlock fireblock = (FireBlock) Blocks.FIRE;
        for (BlockListEntry entry : blockListConfig.getBlockListConfig()) {
            ResourceLocation resource = new ResourceLocation(entry.getBlock());
            Block block = ForgeRegistries.BLOCKS.getValue(resource);
            int fireSpreadSpeed = entry.getFireSpreadSpeed();
            int flammability = entry.getFlammability();
            boolean flag1 = fireblock.flameOdds.containsKey(block);
            boolean flag2 = fireblock.burnOdds.containsKey(block);
            boolean flag3 = fireSpreadSpeed <= 0 || flammability <= 0;
            if (block != null && (block.defaultBlockState().isAir() || block instanceof BaseFireBlock)) {
                removeEntry(entry.getBlock());
            }
            if (block != null && !flag1 && !flag2) {
                fireblock.setFlammable(block, fireSpreadSpeed, flammability);
            }
            if (block == null || flag3) {
                removeEntry(entry.getBlock());
            }
        }
    }

}