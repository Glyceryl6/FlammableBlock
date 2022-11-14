package com.glyceryl6.flaming.json.objects;

import java.util.List;

public class RemoveConfig {

    private List<RemoveEntry> removeEntries;

    public RemoveConfig(List<RemoveEntry> compostConfig) {
        this.removeEntries = compostConfig;
    }

    public List<RemoveEntry> getRemoveConfig() {
        return removeEntries;
    }

    public void setRemoveConfig(List<RemoveEntry> removeEntries) {
        this.removeEntries = removeEntries;
    }

}