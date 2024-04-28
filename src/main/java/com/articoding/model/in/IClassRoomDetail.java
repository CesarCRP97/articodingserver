package com.articoding.model.in;

import com.articoding.model.ClassRoomLevelCompleted;

import java.util.List;

public interface IClassRoomDetail {

    Long getId();

    String getName();

    String getDescription();

    List<IUser> getTeachers();

    List<IUser> getStudents();

    List<ILevel> getLevels();

    String getClassKey();

    List<ClassRoomLevelCompleted> getLevelsCompletedByUsers();


    boolean isEnabled();
}
