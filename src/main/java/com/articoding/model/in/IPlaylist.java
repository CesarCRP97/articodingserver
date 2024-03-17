package com.articoding.model.in;

import java.util.List;

public interface IPlaylist {

    Long getId();

    String getTitle();

    IUser getOwner();

    List<ILevel> getLevels();

    //Todo - añadir me gustas cuando funcionen

}
