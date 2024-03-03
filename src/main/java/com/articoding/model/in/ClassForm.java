package com.articoding.model.in;

import java.util.List;

public class ClassForm {

    List<Long> studentsId;

    List<Long> teachersId;

    String name;

    String description;

    boolean enabled = true;

    public ClassForm() {
    }

    public List<Long> getStudentsId() {
        return studentsId;
    }

    public void setStudentsId(List<Long> studentsId) {
        this.studentsId = studentsId;
    }

    public List<Long> getTeachersId() {
        return teachersId;
    }

    public void setTeachersId(List<Long> teachersId) {
        this.teachersId = teachersId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
