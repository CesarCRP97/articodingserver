package com.articoding.service;

import com.articoding.RoleHelper;
import com.articoding.error.ErrorNotFound;
import com.articoding.error.NotAuthorization;
import com.articoding.error.RestError;
import com.articoding.model.ClassRoom;
import com.articoding.model.ClassRoomLevelCompleted;
import com.articoding.model.Level;
import com.articoding.model.User;
import com.articoding.model.in.ClassForm;
import com.articoding.model.in.CompletedLevelsDTO;
import com.articoding.model.in.IClassRoom;
import com.articoding.model.in.IClassRoomDetail;
import com.articoding.model.in.IUid;
import com.articoding.model.in.UpdateClassRoomForm;
import com.articoding.repository.ClassRepository;
import com.articoding.repository.LevelRepository;
import com.articoding.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ClassService {

    @Autowired
    UserService userService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ClassRepository classRepository;

    @Autowired
    LevelRepository levelRepository;
    @Autowired
    RoleHelper roleHelper;


    public Long createClass(ClassForm classForm) {
        User actualUser = userService.getActualUser();

        /** Checks if is teacher or admin */
        if (!roleHelper.isTeacher(actualUser) && !roleHelper.isAdmin(actualUser)) {
            throw new NotAuthorization("You can't create classes if you don't have teacher role");
        }

        ClassRoom newClassRoom = new ClassRoom();
        newClassRoom.setDescription(classForm.getDescription());
        newClassRoom.setName(classForm.getName());

        UUID uuid = UUID.randomUUID();
        String formatKey = uuid.toString().replace("-", "");
        newClassRoom.setKey(formatKey.substring(0, 7));

        List<User> students = new ArrayList<>();
        classForm.getStudentsId().forEach(studentId -> {
            Optional<User> userOptional = userRepository.findById(studentId);
            if (userOptional.isPresent()) {
                students.add(userOptional.get());
            } else {
                throw new ErrorNotFound("user", studentId);
            }
        });
        newClassRoom.setStudents(students);

        List<User> teachers = new ArrayList<>();
        classForm.getTeachersId().forEach(studentId -> {
            Optional<User> userOptional = userRepository.findById(studentId);
            if (userOptional.isPresent()) {
                if (roleHelper.isTeacher(userOptional.get())) {
                    teachers.add(userOptional.get());
                } else {
                    throw new NotAuthorization("One of the teacher from the class is not authorized");
                }
            } else {
                throw new ErrorNotFound("user", studentId);
            }
        });
        teachers.add(actualUser);
        newClassRoom.setTeachers(teachers);

        newClassRoom.setEnabled(classForm.isEnabled());

        return classRepository.save(newClassRoom).getId();

    }

    public IClassRoomDetail getById(Long classId) {

        User actualUser = userService.getActualUser();
        /** Checks if class exists */
        ClassRoom classRoom = classRepository.findById(classId)
                .orElseThrow(() -> new ErrorNotFound("class", classId));
        /** Checks if it's ADMIN, student or teacher from the class */
        if (!roleHelper.isAdmin(actualUser) && !classRoom.getStudents().stream().anyMatch(s -> s.getId() == actualUser.getId()) &&
                !classRoom.getTeachers().stream().anyMatch(t -> t.getId() == actualUser.getId())) {
            throw new NotAuthorization("access to the class with id " + classId);
        }
        return classRepository.findById(classId, IClassRoomDetail.class);
    }

    public Page<IClassRoom> getClasses(PageRequest pageRequest, Optional<Long> userId, Optional<Long> teachId, Optional<Long> levelId, Optional<String> title) {
        /** To know the classes of a level or user, it needs to at least be teacher */
        User actualUser = userService.getActualUser();
        if (userId.isPresent() || teachId.isPresent() || levelId.isPresent()) {
            if (!roleHelper.can(actualUser.getRole(), "ROLE_TEACHER")) {
                throw new NotAuthorization("get class of user ");
            } else {/** Returns the classes where the user or teacher is */
                if (userId.isPresent()) {
                    if (title.isPresent()) {
                        if (roleHelper.isAdmin(actualUser)) {
                            return classRepository.findByStudentsIdAndNameContains(userId.get(), title.get(), pageRequest, IClassRoom.class);
                        } else {
                            return classRepository.findByStudentsIdAndNameContainsAndEnabledTrue(userId.get(), title.get(), pageRequest, IClassRoom.class);
                        }
                    } else {
                        if (roleHelper.isAdmin(actualUser)) {
                            return classRepository.findByStudentsId(userId.get(), pageRequest, IClassRoom.class);
                        } else {
                            return classRepository.findByStudentsIdAndEnabledTrue(userId.get(), pageRequest, IClassRoom.class);
                        }
                    }
                } else if (teachId.isPresent()) {
                    if (title.isPresent()) {
                        if (roleHelper.isAdmin(actualUser)) {
                            return classRepository.findByTeachersIdAndNameContains(teachId.get(), title.get(), pageRequest, IClassRoom.class);
                        } else {
                            return classRepository.findByTeachersIdAndNameContainsAndEnabledTrue(teachId.get(), title.get(), pageRequest, IClassRoom.class);
                        }

                    } else {
                        if (roleHelper.isAdmin(actualUser)) {
                            return classRepository.findByTeachersIdAndEnabledTrue(teachId.get(), pageRequest, IClassRoom.class);
                        } else {
                            return classRepository.findByTeachersId(teachId.get(), pageRequest, IClassRoom.class);
                        }
                    }
                } else {
                    if (title.isPresent()) {
                        if (roleHelper.isAdmin(actualUser)) {
                            return classRepository.findByLevelsIdAndNameContains(levelId.get(), title.get(), pageRequest, IClassRoom.class);
                        } else {
                            return classRepository.findByLevelsIdAndNameContainsAndEnabledTrue(levelId.get(), title.get(), pageRequest, IClassRoom.class);
                        }
                    } else {
                        if (roleHelper.isAdmin(actualUser)) {
                            return classRepository.findByLevelsId(levelId.get(), pageRequest, IClassRoom.class);
                        } else {
                            return classRepository.findByLevelsIdAndEnabledTrue(levelId.get(), pageRequest, IClassRoom.class);
                        }
                    }
                }
            }
        } else {
            if (roleHelper.isAdmin(actualUser)) {
                /** If ADMIN, returns every class*/
                if (title.isPresent()) {
                    return classRepository.findByAndNameContains(pageRequest, title.get(), IClassRoom.class);
                } else {
                    return classRepository.findBy(pageRequest, IClassRoom.class);
                }
            } else if (roleHelper.isTeacher(actualUser)) {
                /** If TEACHER, returns every class the teacher has created or is in */
                if (title.isPresent()) {
                    return classRepository.findByTeachersIdAndNameContainsAndEnabledTrue(actualUser.getId(), title.get(), pageRequest, IClassRoom.class);
                } else {
                    return classRepository.findByTeachersIdAndEnabledTrue(actualUser.getId(), pageRequest, IClassRoom.class);
                }
            } else {
                /** If STUDENT, returns every class the user is enrolled in */
                if (title.isPresent()) {
                    return classRepository.findByStudentsIdAndNameContainsAndEnabledTrue(actualUser.getId(), title.get(), pageRequest, IClassRoom.class);
                } else {
                    return classRepository.findByStudentsIdAndEnabledTrue(actualUser.getId(), pageRequest, IClassRoom.class);
                }
            }
        }
    }

    public Long updateClassRoom(Long classId, UpdateClassRoomForm updateClassRoomForm) {

        ClassRoom classRoom = canEdit(classId);

        /** Modifies the classroom's parameters included in the UpdateClassRoomForm */
        if (updateClassRoomForm.getName() != null) {
            classRoom.setName(updateClassRoomForm.getName());
        }

        if (updateClassRoomForm.getDescription() != null) {
            classRoom.setDescription(updateClassRoomForm.getDescription());
        }

        if (updateClassRoomForm.isEnabled() != null) {
            classRoom.setEnabled(updateClassRoomForm.isEnabled());
        }

        classRepository.save(classRoom);
        return classRoom.getId();
    }

    public Long addLevel(Long classId, List<IUid> levelsId) {
        ClassRoom classRoom = canEdit(classId);

        for (IUid levelId : levelsId) {
            Level level = levelRepository.findById(levelId.getId()).
                    orElseThrow(() -> new ErrorNotFound("Nivel", levelId.getId()));
            /** If a level is already included does nothing */
            if (classRoom.getLevels().stream().anyMatch(level1 -> level1.getId() == level.getId())) {
                return classId;
            }

            level.getClassRooms().add(classRoom);
            classRoom.getLevels().add(level);
        }

        classRepository.save(classRoom);

        return classRoom.getId();
    }

    public Long deleteLevel(Long classId, Long levelId) {
        ClassRoom classRoom = canEdit(classId);

        Level level = levelRepository.findById(levelId).
                orElseThrow(() -> new ErrorNotFound("Nivel", levelId));
        /** If a level isn't included does anything */
        if (!classRoom.getLevels().stream().anyMatch(level1 -> level1.getId() == level.getId())) {
            return classId;
        }

        List<Level> actualLevels = classRoom.getLevels().stream().filter(level1 -> level1.getId() != level.getId()).collect(Collectors.toList());
        classRoom.setLevels(actualLevels);
        classRepository.save(classRoom);

        return classRoom.getId();
    }

    public Long addStudents(Long classId, List<String> usersId) {
        ClassRoom classroom = canEdit(classId);

        for (String username : usersId) {
            User student = userRepository.findByUsername(username);
            if (student == null) {
                throw new RestError("Doesn't exist a user with name " + username);
            }
            /** Verifies user's role */
            if (!roleHelper.isUser(student)) {
                throw new RestError("User " + username + " is not student");
            }
            /** If it's already part of the class does nothing */
            if (classroom.getStudents().stream().anyMatch(level1 -> level1.getId() == student.getId())) {
                return classId;
            }

            student.getClasses().add(classroom);
            classroom.getStudents().add(student);
        }

        classRepository.save(classroom);

        return classroom.getId();
    }

    //Add a student it was not part of the classroom previously
    public Long addStudent(ClassRoom classroom, User user) {
        if (classroom.getStudents().stream().anyMatch(cUser -> user.getId() == cUser.getId())) {
            return classroom.getId();
        }
        user.getClasses().add(classroom);
        classroom.getStudents().add(user);

        classRepository.save(classroom);
        return classroom.getId();
    }

    public Long deleteStudent(Long classId, Long userId) {
        ClassRoom classRoom = canEdit(classId);

        User student = userRepository.findById(userId).
                orElseThrow(() -> new ErrorNotFound("Estudiante", userId));
        /** If it isn't part of the class does nothing */
        if (!classRoom.getStudents().stream().anyMatch(level1 -> level1.getId() == student.getId())) {
            return classId;
        }

        List<User> actualStudents = classRoom.getStudents().stream().filter(user -> user.getId() != student.getId()).collect(Collectors.toList());
        classRoom.setStudents(actualStudents);
        classRepository.save(classRoom);

        return classRoom.getId();
    }

    public Long addTeachers(Long classId, List<String> usersId) {
        ClassRoom classRoom = canEdit(classId);

        for (String username : usersId) {
            User teacher = userRepository.findByUsername(username);
            if (teacher == null) {
                throw new RestError("User " + username + " does not exist");
            }

            /** Verify its role*/
            if (!roleHelper.isTeacher(teacher)) {
                throw new RestError("The user " + username + " is not a teacher");
            }

            /** If it is already part of the class does nothing */
            if (classRoom.getTeachers().stream().anyMatch(level1 -> level1.getId() == teacher.getId())) {
                return classId;
            }

            teacher.getClasses().add(classRoom);
            classRoom.getTeachers().add(teacher);
        }

        classRepository.save(classRoom);

        return classRoom.getId();
    }

    public Long deleteTeacher(Long classId, Long userId) {
        ClassRoom classRoom = canEdit(classId);

        User teacher = userRepository.findById(userId).
                orElseThrow(() -> new ErrorNotFound("Profesor", userId));

        /** If it is not part of the class does nothing */
        if (!classRoom.getTeachers().stream().anyMatch(level1 -> level1.getId() == teacher.getId())) {
            return classId;
        }

        List<User> actualTeacher = classRoom.getTeachers().stream().filter(user -> user.getId() != teacher.getId()).collect(Collectors.toList());
        if (actualTeacher.isEmpty()) {
            throw new RestError("A class has to have at least a teacher");
        }
        classRoom.setTeachers(actualTeacher);
        classRepository.save(classRoom);

        return classRoom.getId();
    }

    private ClassRoom canEdit(Long classId) {

        User actualUser = userService.getActualUser();

        /** Gets the original class*/
        ClassRoom classRoom = classRepository.findById(classId).
                orElseThrow(() -> new ErrorNotFound("clase", classId));

        /** Verifies if the actualUser is ROLE_ADMIN or ROLE_TEACHERS */
        if (!roleHelper.isAdmin(actualUser) && !classRoom.getTeachers().stream().anyMatch(t -> t.getId() == actualUser.getId())) {
            throw new NotAuthorization("Modificar la clase " + classId);
        }

        return classRoom;
    }


    public Long enterClass(String classKey) {
        User actualUser = userService.getActualUser();

        ClassRoom classRoom = (ClassRoom) classRepository.findByClassKey(classKey).
                orElseThrow(() -> new ErrorNotFound("key", actualUser.getId()));

        return addStudent(classRoom, actualUser);

    }

    public void completeLevel(Long classId, Long levelId) {
        User actualUser = userService.getActualUser();
        ClassRoom classRoom = classRepository.findById(classId, ClassRoom.class);
        Level level = levelRepository.findById(levelId).get();

        ClassRoomLevelCompleted newCompleted = new ClassRoomLevelCompleted(actualUser, classRoom, level);

        classRoom.getLevelsCompletedByUsers().add(newCompleted);
        
        classRepository.save(classRoom);
    }

    public CompletedLevelsDTO getCompletedLevels(Long classId) {
        User actualUser = userService.getActualUser();

        return new CompletedLevelsDTO(classRepository.findById(classId, ClassRoom.class)
                .getLevelsCompletedByUsers().stream()
                .filter(object -> object.getUser().getId() == actualUser.getId())
                .map(ClassRoomLevelCompleted::getLevel)
                .map(Level::getId)
                .collect(Collectors.toList()));
    }
}
