package com.micewine.emu.core;

public class WinetricksItem {
    private final String name;
    private final String description;
    private final String category;
    private boolean isSelected = false;

    public WinetricksItem(String name, String description, String category) {
        this.name = name;
        this.description = description;
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getCategory() {
        return category;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
