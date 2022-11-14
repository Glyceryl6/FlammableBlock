package com.glyceryl6.flaming;

import com.glyceryl6.flaming.commands.CommandCenter;
import com.glyceryl6.flaming.json.JsonHandler;
import com.glyceryl6.flaming.json.JsonRemoval;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.fml.common.Mod;

@Mod(FlammableBlock.MOD_ID)
public class FlammableBlock {

    public static final String MOD_ID = "flammable_block";

    public FlammableBlock() {
        JsonHandler.setup();
        JsonRemoval.setup();
        JsonHandler.addToList();
        JsonRemoval.removeFromList();
        MinecraftForge.EVENT_BUS.addListener(this::registerCommands);
    }

    public void registerCommands(RegisterCommandsEvent event) {
        new CommandCenter(event.getDispatcher());
    }

}