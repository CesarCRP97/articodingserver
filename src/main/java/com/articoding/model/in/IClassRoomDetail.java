package com.articoding.model.in;

import java.util.List;

public interface IClassRoomDetail {

    Long getId();

    String getName();

    String getDescription();

    List<IUser> getTeachers();

    List<IUser> getStudents();

    List<ILevel> getLevels();

    String getKey();

    boolean isEnabled();
}
