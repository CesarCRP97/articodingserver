package com.articoding.model.in;

import com.articoding.model.ClassRoom;

import java.util.List;
import java.util.stream.Collectors;

public class ClassRoomDetailResponse {
    public Long id;
    public String name;
    public String description;
    public List<UserResponse> teachers;
    public List<UserResponse> students;
    public List<LevelResponse> levels;
    public String classKey;
    public boolean enabled;
    public List<LevelCompletedResponse> levelsCompletedByUsers;
    public static class LevelCompletedResponse {
        public Long levelId;
        public Long userId;
        public LevelCompletedResponse(Long levelId, Long userId) {
            this.levelId = levelId;
            this.userId = userId;
        }
    }

    public ClassRoomDetailResponse(ClassRoom classRoom) {
        this.id = classRoom.getId();
        this.name = classRoom.getName();
        this.description = classRoom.getDescription();
        this.teachers = classRoom.getTeachers()
                .stream()
                .map(UserResponse::new)
                .collect(Collectors.toList());
        this.students = classRoom.getStudents()
                .stream()
                .map(UserResponse::new)
                .collect(Collectors.toList());
        this.levels = classRoom.getLevels().stream()
                .map(LevelResponse::new)
                .collect(Collectors.toList());
        this.classKey = classRoom.getClassKey();
        this.enabled = classRoom.isEnabled();
        this.levelsCompletedByUsers = classRoom.getLevelsCompletedByUsers()
                .stream()
                .map(levelCompleted -> new LevelCompletedResponse(levelCompleted.getLevel().getId(), levelCompleted.getUser().getId()))
                .collect(Collectors.toList());
    }
}
