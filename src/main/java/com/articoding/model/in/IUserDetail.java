package com.articoding.model.in;

import java.util.List;

public interface IUserDetail {

    Integer getId();

    String getUsername();

    boolean isEnabled();

    IRole getRole();

    List<ILevel> getCreatedLevels();

    List<IClassRoom> getClassRooms();

    List<IClassRoom> getOwnerClassRooms();

}
