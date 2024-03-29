package com.articoding.model.in;

import java.util.List;

public class CompletedLevelsDTO {
    private final List<Long> levelIds;

    public CompletedLevelsDTO(List<Long> levelIds) {
        this.levelIds = levelIds;
    }

    public List<Long> getLevelIds() {
        return levelIds;
    }
}
