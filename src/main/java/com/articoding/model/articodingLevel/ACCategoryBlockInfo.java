package com.articoding.model.articodingLevel;

import java.util.List;

public class ACCategoryBlockInfo {

    private boolean activate;
    private List<ACBlockInfo> activeBlocks;

    public ACCategoryBlockInfo() {
    }

    public boolean isActivate() {
        return activate;
    }

    public void setActivate(boolean activate) {
        this.activate = activate;
    }

    public List<ACBlockInfo> getActiveBlocks() {
        return activeBlocks;
    }

    public void setActiveBlocks(List<ACBlockInfo> activeBlocks) {
        this.activeBlocks = activeBlocks;
    }

}