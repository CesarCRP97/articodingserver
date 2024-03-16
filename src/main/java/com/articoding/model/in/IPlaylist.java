package com.articoding.model.in;

import java.util.List;

public interface IPlaylist {

    Long getId();

    String getName();

    IUser getOwner();

    List<ILevel> getLevels();

    boolean isPublicLevel();

    boolean isActive();

    //Todo - añadir me gustas cuando funcionen

}
