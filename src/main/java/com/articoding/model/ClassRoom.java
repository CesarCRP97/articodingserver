package com.articoding.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import java.util.List;

@Entity
@Table(name = "class")
public class ClassRoom {

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "class_teacher",
            joinColumns = @JoinColumn(
                    name = "class_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(
                    name = "teacher_id", referencedColumnName = "id")
    )
    List<User> teachers;
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "class_student",
            joinColumns = @JoinColumn(
                    name = "class_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(
                    name = "student_id", referencedColumnName = "id")
    )
    List<User> students;
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "class_levels",
            joinColumns = @JoinColumn(
                    name = "class_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(
                    name = "level_id", referencedColumnName = "id")
    )
    List<Level> levels;
    String description;
    String name;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;
    @Column
    private boolean enabled = true;

    public ClassRoom() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<User> getTeachers() {
        return teachers;
    }

    public void setTeachers(List<User> teachers) {
        this.teachers = teachers;
    }

    public List<User> getStudents() {
        return students;
    }

    public void setStudents(List<User> students) {
        this.students = students;
    }

    public List<Level> getLevels() {
        return levels;
    }

    public void setLevels(List<Level> levels) {
        this.levels = levels;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}

