package com.articoding.service;

import com.articoding.RoleHelper;
import com.articoding.error.ErrorNotFound;
import com.articoding.error.NotAuthorization;
import com.articoding.model.ClassRoom;
import com.articoding.model.Level;
import com.articoding.model.User;
import com.articoding.model.in.ILevel;
import com.articoding.model.in.LevelForm;
import com.articoding.model.in.UpdateLevelForm;
import com.articoding.repository.ClassRepository;
import com.articoding.repository.LevelRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class LevelService {

    @Autowired
    LevelRepository levelRepository;

    @Autowired
    ClassRepository classRepository;
    @Autowired
    UserService userService;
    @Autowired
    RoleHelper roleHelper;

    public Long createLevel(User actualUser, LevelForm levelForm) throws ErrorNotFound, NotAuthorization, JsonProcessingException {

        Level level = new Level();
        /** If the level needs to be added to Classrooms, we need to verify actualUser is teacher in every classroom */
        if (!levelForm.getClasses().isEmpty()) {
            List<ClassRoom> classRoomList = new ArrayList<>();
            /** Verifies if the user is teacher or more*/
            if (!roleHelper.can(actualUser.getRole(), "ROLE_TEACHER")) {
                throw new NotAuthorization(actualUser.getRole(),
                        "crear un nivel en una clase");
            }

            /** Verifies the validity of the classes */
            for (Long idClass : levelForm.getClasses()) {
                /** Class exists */
                ClassRoom classRoom = classRepository.findById(idClass)
                        .orElseThrow(() -> new ErrorNotFound("Clase", idClass));

                /** actualUser is Teacher of the class */
                if (!classRoom.getTeachers().contains(actualUser)) {
                    throw new NotAuthorization(" crear un nivel en la clase " + idClass);
                }
                classRoomList.add(classRoom);
                classRoom.getLevels().add(level);
            }
            level.setClassRooms(classRoomList);
        }

        level.setActive(true);
        level.setTitle(levelForm.getTitle());
        level.setPublicLevel(levelForm.isPublicLevel());

        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        level.setSerializaArticodingLevel(ow.writeValueAsString(levelForm.getArticodingLevel()));

        level.setOwner(actualUser);
        Level newLevel = levelRepository.save(level);

        return newLevel.getId();
    }

    public ILevel getLevel(User actualUser, Long levelId) {
        /** Verifies if the classroom exists */
        Level level = levelRepository.findById(levelId, Level.class);
        if (level == null) {
            throw new ErrorNotFound("nivel", levelId);
        }

        if (!level.isActive() && !roleHelper.isAdmin(actualUser) && !(roleHelper.isTeacher(actualUser) && level.getOwner().getId() == actualUser.getId())) {
            throw new NotAuthorization("nivel desactivado");
        }
        boolean canShow = false;
        /** Check permission */
        if (level.isPublicLevel()) {
            if (level.getOwner().getId() != actualUser.getId() && !roleHelper.isAdmin(actualUser)) {
                for (ClassRoom classRoom : level.getClassRooms()) {
                    if (!classRoom.getTeachers().stream().anyMatch(teacher -> teacher.getId() == actualUser.getId())) {
                        if (classRoom.getStudents().stream().anyMatch(studend -> studend.getId() == actualUser.getId())) {
                            /** Is student of a classroom that includes the level */
                            canShow = true;
                        }
                    } else {/** Is teacher in any of the classrooms that includes the level */
                        canShow = true;
                    }
                }
            } else {/** Is the owner of the level or ROOT*/
                canShow = true;
            }
        } else {/** Is public */
            canShow = true;
        }
        if (!canShow) {
            throw new NotAuthorization(String.format("ver la clase %s", level.getId()));
        }
        return levelRepository.findById(levelId, ILevel.class);

    }

    //Todo- refactor, too many lanes, this code has to be in private methods.
    public Page<ILevel> getLevels(PageRequest pageRequest, Optional<Long> classId,
                                  Optional<Long> userId, Optional<Boolean> publicLevels, Optional<String> title) {
        User actualUser = userService.getActualUser();
        /** If there is a classId then returns levels from the classroom */
        if (classId.isPresent()) {
            return getLevelsFromClass(pageRequest, actualUser, classId, title);
        }
        else if (userId.isPresent()) {
            return getLevelsFromUserId(pageRequest,actualUser,userId);
        } else {
            if (publicLevels.isPresent()) {
                /** If publicLevels is true, returns all public levels. */
                return getPublicLevels(pageRequest, title);
            } else {
                /** If it's ADMIN then it returns every level */
                return getOwnedLevels(pageRequest, title, actualUser);
            }

        }
    }

    public long updateLevel(UpdateLevelForm newLevel, Long levelId) {
        /** Checks if the level exists */
        Level levelOld = levelRepository.findById(levelId)
                .orElseThrow(() -> new ErrorNotFound("level", levelId));

        User actualUser = userService.getActualUser();
        /** Checks if actualUser is the owner or ADMIN */
        if (!actualUser.getCreatedLevels().stream().anyMatch(x -> x.getId() == levelId) &&
                !roleHelper.isAdmin(actualUser)) {
            throw new NotAuthorization("modificar el nivel " + levelId);
        } else {
            /** It's editable */
            if (newLevel.getTitle() != null) {
                levelOld.setTitle(newLevel.getTitle());
            }
            if (newLevel.isPublicLevel() != null) {
                levelOld.setPublicLevel(newLevel.isPublicLevel());
            }
            if (newLevel.isActive() != null) {
                levelOld.setActive(newLevel.isActive());
            }

            return levelRepository.save(levelOld).getId();

        }
    }


    public long likeLevel(LevelForm levelForm, long levelId){
        Level level = levelRepository.findById(levelId)
                .orElseThrow(() -> new ErrorNotFound("level", levelId));
        level.incrLikes();
        return levelId;
    }

    public long dislikeLevel(LevelForm levelForm, Long levelId) {
        Level level = levelRepository.findById(levelId)
                .orElseThrow(() -> new ErrorNotFound("level", levelId));
        level.decrLikes();
        return levelId;
    }

    public long playLevel(LevelForm levelForm, Long levelId) {
        Level level = levelRepository.findById(levelId)
                .orElseThrow(() -> new ErrorNotFound("level", levelId));
        level.increaseTimesPlayed();
        return levelId;
    }

    private Page<ILevel> getLevelsFromClass(PageRequest pageRequest, User actualUser, Optional<Long> classId, Optional<String> title){
        /** Checks if the classroom exists */
        ClassRoom classRoom = classRepository.findById(classId.get())
                .orElseThrow(() -> new ErrorNotFound("clase", classId.get()));

        /** Checks if it is student/teacher or admin */
        if (!roleHelper.isAdmin(actualUser)) {
            if (!classRoom.getStudents().stream().anyMatch(s -> s.getId() == actualUser.getId()) &&
                    !classRoom.getTeachers().stream().anyMatch(s -> s.getId() == actualUser.getId())) {
                throw new NotAuthorization("ver niveles de la clase " + classId.get());
            }
        }
        if (title.isPresent()) {
            return levelRepository.findByClassRoomsAndActiveTrueAndTitleContains(classRoom, title.get(), pageRequest, ILevel.class);
        } else {
            return levelRepository.findByClassRoomsAndActiveTrue(classRoom, pageRequest, ILevel.class);

        }

    }

    private Page<ILevel> getLevelsFromUserId(PageRequest pageRequest, User actualUser, Optional<Long> userId){
        /** If there is a userId it checks if the user is ADMIN or . */
        if (!roleHelper.isAdmin(actualUser)) {
            throw new NotAuthorization("ver niveles del usuario " + userId.get());
        } else {
            return levelRepository.findByOwnerAndActiveTrue(actualUser, pageRequest, ILevel.class);
        }
    }


    private Page<ILevel> getPublicLevels(PageRequest pageRequest, Optional<String> title){
        if (title.isPresent()) {
            return levelRepository.findByPublicLevelTrueAndTitleContains(pageRequest, ILevel.class, title.get());
        } else {
            return levelRepository.findByPublicLevelTrue(pageRequest, ILevel.class);
        }
    }

    private Page<ILevel> getOwnedLevels(PageRequest pageRequest, Optional<String> title, User actualUser){
        /** If it's ADMIN then it returns every level */
        if (roleHelper.isAdmin(actualUser)) {
            if (title.isPresent()) {
                return levelRepository.findByTitleContains(pageRequest, title.get(), ILevel.class);
            } else {
                return levelRepository.findBy(pageRequest, ILevel.class);
            }
        } else {
            /** Otherwise, returns only the levels created by the user */
            if (title.isPresent()) {
                return levelRepository.findByOwnerAndActiveTrueAndTitleContains(actualUser, title.get(), pageRequest, ILevel.class);
            } else {
                return levelRepository.findByOwnerAndActiveTrue(actualUser, pageRequest, ILevel.class);
            }
        }
    }

}
