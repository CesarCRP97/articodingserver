package com.articoding.model.in;

import java.util.List;


//Intended for the return value of the /completed_levels endpoint
public class CompletedLevelsDTO {
    private final List<Long> levelIds;

    public CompletedLevelsDTO(List<Long> levelIds) {
        this.levelIds = levelIds;
    }

    public List<Long> getLevelIds() {
        return levelIds;
    }
}
