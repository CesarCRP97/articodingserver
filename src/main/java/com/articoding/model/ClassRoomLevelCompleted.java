package com.articoding.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.io.Serializable;

@Entity
@IdClass(ClassRoomLevelCompleted.class)
public class ClassRoomLevelCompleted implements Serializable {

    @Id
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Id
    @ManyToOne
    @JoinColumn(name = "classroom_id")
    private ClassRoom classRoom;

    @Id
    @ManyToOne
    @JoinColumn(name = "level_id")
    private Level level;


    public ClassRoomLevelCompleted() {
    }

    public ClassRoomLevelCompleted(User user, ClassRoom classRoom, Level level) {
        this.user = user;
        this.classRoom = classRoom;
        this.level = level;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public ClassRoom getClassRoom() {
        return classRoom;
    }

    public void setClassRoom(ClassRoom classRoom) {
        this.classRoom = classRoom;
    }

    public Level getLevel() {
        return level;
    }

    public void setLevel(Level level) {
        this.level = level;
    }
}
