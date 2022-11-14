package com.glyceryl6.flaming.json;

import com.glyceryl6.flaming.FlammableBlock;
import com.glyceryl6.flaming.json.objects.RemoveConfig;
import com.glyceryl6.flaming.json.objects.RemoveEntry;
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

public class JsonRemoval {

    private static final Gson gson = new Gson();
    private static final List<RemoveEntry> removeEntries = new ArrayList<>();
    public static RemoveConfig removeConfig = new RemoveConfig(removeEntries);
    public static final File JSON_FILE = new File(FMLPaths.CONFIGDIR.get() + "/flammable_block/blacklist.json");

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

    public static boolean containsEntry(RemoveEntry entry) {
        for (RemoveEntry removeEntry : removeConfig.getRemoveConfig()) {
            if (removeEntry.getBlock().equals(entry.getBlock())) {
                return true;
            }
        }
        return false;
    }

    public static void addEntry(RemoveEntry entry) {
        if (!containsEntry(entry)) {
            removeConfig.getRemoveConfig().add(entry);
            reload();
        }
    }

    public static void removeEntry(String entryName) {
        if (containsEntry(new RemoveEntry(entryName))) {
            removeConfig.getRemoveConfig().removeIf(entry -> entry.getBlock().equals(entryName));
            reload();
        }
    }

    public static void writeJson(File jsonFile) {
        try (Writer writer = new FileWriter(jsonFile)) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(removeConfig, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void readJson(File jsonFile) {
        try (Reader reader = new FileReader(jsonFile)) {
            removeConfig = gson.fromJson(reader, RemoveConfig.class);
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

    public static void removeFromList() {
        FireBlock fireblock = (FireBlock) Blocks.FIRE;
        for (RemoveEntry entry : removeConfig.getRemoveConfig()) {
            ResourceLocation resource = new ResourceLocation(entry.getBlock());
            Block block = ForgeRegistries.BLOCKS.getValue(resource);
            boolean flag1 = fireblock.flameOdds.containsKey(block);
            boolean flag2 = fireblock.burnOdds.containsKey(block);
            if (flag1 && flag2) {
                fireblock.flameOdds.keySet().remove(block);
                fireblock.burnOdds.keySet().remove(block);
            }
            if (block != null && (block.defaultBlockState().isAir() || block instanceof BaseFireBlock)) {
                removeEntry(entry.getBlock());
            }
            if (block == null) {
                removeEntry(entry.getBlock());
            }
        }
    }

}