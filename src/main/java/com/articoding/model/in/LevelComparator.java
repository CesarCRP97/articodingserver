package com.articoding.model.in;

import java.util.Comparator;

public class LevelComparator implements Comparator<ILevel> {
    private final boolean orderByLikes;

    public LevelComparator(boolean orderByLikes) {
        this.orderByLikes = orderByLikes;
    }

    @Override
    public int compare(ILevel o1, ILevel o2) {
        return orderByLikes ? -(Integer.compare(o1.getLikes(), o2.getLikes())) : -(Integer.compare(o1.getTimesPlayed(), o2.getTimesPlayed()));
    }
}
