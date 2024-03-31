package com.articoding.model.in;

import java.util.Comparator;

public class PlaylistComparator implements Comparator<IPlaylist> {
    private final boolean orderByLikes;

    public PlaylistComparator(boolean orderByLikes) {
        this.orderByLikes = orderByLikes;
    }

    @Override
    public int compare(IPlaylist o1, IPlaylist o2) {
        return orderByLikes ? -(Integer.compare(o1.getLikes(), o2.getLikes())) : -(Integer.compare(o1.getTimesPlayed(), o2.getTimesPlayed()));
    }
}