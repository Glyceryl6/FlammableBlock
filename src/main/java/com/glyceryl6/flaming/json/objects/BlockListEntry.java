package com.glyceryl6.flaming.json.objects;

@SuppressWarnings("unused")
public class BlockListEntry {

    private String block;
    private int fireSpreadSpeed;
    private int flammability;

    public BlockListEntry() {}

    public BlockListEntry(String block, int fireSpreadSpeed, int flammability) {
        this.block = block;
        this.fireSpreadSpeed = fireSpreadSpeed;
        this.flammability = flammability;
    }

    public String getBlock() {
        return this.block;
    }

    public int getFireSpreadSpeed() {
        return this.fireSpreadSpeed;
    }

    public int getFlammability() {
        return this.flammability;
    }

}