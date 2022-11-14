package com.glyceryl6.flaming.json.objects;

import java.util.List;

@SuppressWarnings("unused")
public class BlockListConfig {

    private List<BlockListEntry> blockListEntries;

    public BlockListConfig(List<BlockListEntry> blockListEntries) {
        this.blockListEntries = blockListEntries;
    }

    public List<BlockListEntry> getBlockListConfig() {
        return blockListEntries;
    }

    public void setBlockListConfig(List<BlockListEntry> blockListEntries) {
        this.blockListEntries = blockListEntries;
    }

}