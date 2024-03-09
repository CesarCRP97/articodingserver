package com.articoding.service;

import com.articoding.RoleHelper;
import com.articoding.error.ErrorNotFound;
import com.articoding.error.NotAuthorization;
import com.articoding.model.ClassRoom;
import com.articoding.model.Level;
import com.articoding.model.User;
import com.articoding.model.in.ILevel;
import com.articoding.model.in.LevelForm;
import com.articoding.model.in.LevelWithImageDTO;
import com.articoding.model.in.UpdateLevelForm;
import com.articoding.repository.ClassRepository;
import com.articoding.repository.LevelRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class LevelService {

    public static Path uploadDir = Paths.get("levelimages");
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

        if (levelForm.getImage() != null) {
            System.out.println("getImage != null");
            byte[] image = levelForm.getImage();
            String imagePath = this.saveImageForLevel(level, image);
        }

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

    public Page<LevelWithImageDTO> getLevels(PageRequest pageRequest, Optional<Long> classId,
                                             Optional<Long> userId, Optional<Boolean> publicLevels, Optional<Boolean> liked, Optional<String> title, Optional<String> owner, Optional<Long> levelId) {

        Page<ILevel> page;
        User actualUser = userService.getActualUser();
        /** If there is a classId then returns levels from the classroom */
        if (classId.isPresent()) {
            page = getLevelsFromClass(pageRequest, actualUser, classId, title);
        } else if (userId.isPresent()) {
            page = getLevelsFromUserId(pageRequest, actualUser, userId);
        } else {
            if (publicLevels.isPresent()) {
                /** If publicLevels is true, returns all public levels. */
                page = getPublicLevels(pageRequest, liked, title, owner, levelId);
            } else {
                /** If it's ADMIN then it returns every level */
                page = getOwnedLevels(pageRequest, title, actualUser);
            }
        }
        return toLevelWithImageDTO(page);
    }

    private Page<LevelWithImageDTO> toLevelWithImageDTO(Page<ILevel> oldPage) {

        return oldPage.map(level -> {
            LevelWithImageDTO levelWithImageDTO = new LevelWithImageDTO();
            levelWithImageDTO.setLevel(level);
            try {
                String imageName = level.getImagePath();
                if (imageName != null)
                    levelWithImageDTO.setImage(this.getImageByImagePath(imageName));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return levelWithImageDTO;
        });
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


    public long likeLevel(LevelForm levelForm, long levelId) {
        Level level = levelRepository.findById(levelId)
                .orElseThrow(() -> new ErrorNotFound("level", levelId));
        level.incrLikes();
        User u = userService.getActualUser();
        u.addLikedLevel(levelId);

        //userService.updateActualUser(u);
        levelRepository.save(level);
        return levelId;
    }

    public long dislikeLevel(LevelForm levelForm, Long levelId) {
        Level level = levelRepository.findById(levelId)
                .orElseThrow(() -> new ErrorNotFound("level", levelId));
        level.decrLikes();
        User u = userService.getActualUser();
        u.deleteLikedLevel(levelId);

        //userService.updateActualUser(u);
        levelRepository.save(level);
        return levelId;
    }

    public long playLevel(LevelForm levelForm, Long levelId) {
        System.out.println("like level id: " + levelId);
        Level level = levelRepository.findById(levelId)
                .orElseThrow(() -> new ErrorNotFound("level", levelId));
        level.increaseTimesPlayed();
        levelRepository.save(level);
        return levelId;
    }

    private Page<ILevel> getLevelsFromClass(PageRequest pageRequest, User actualUser, Optional<Long> classId, Optional<String> title) {
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

    private Page<ILevel> getLevelsFromUserId(PageRequest pageRequest, User actualUser, Optional<Long> userId) {
        /** If there is a userId it checks if the user is ADMIN or . */
        if (!roleHelper.isAdmin(actualUser)) {
            throw new NotAuthorization("ver niveles del usuario " + userId.get());
        } else {
            return levelRepository.findByOwnerAndActiveTrue(actualUser, pageRequest, ILevel.class);
        }
    }

    //Todo - refactorizar filtros chapuceros
    private Page<ILevel> getPublicLevels(PageRequest pageRequest, Optional<Boolean> liked, Optional<String> title, Optional<String> owner, Optional<Long> levelId) {
        Page<ILevel> page;
        if (title.isPresent()) {
            page = levelRepository.findByPublicLevelTrueAndTitleContains(pageRequest, ILevel.class, title.get());
        } else {
            page = levelRepository.findByPublicLevelTrue(pageRequest, ILevel.class);
        }

        if (liked.isPresent()) {
            Set<Long> likedId = userService.getActualUser().getLikedLevels();
            List<ILevel> filteredLiked = page.filter(level -> likedId.contains(level.getId().longValue()))
                    .stream()
                    .collect(Collectors.toList());

            page = filteredLevelsToPage(pageRequest, filteredLiked);
        }
        if (owner.isPresent()) {
            List<ILevel> filteredLiked = page.filter(level -> Objects.equals(level.getOwner().getUsername(), owner.get()))
                    .stream()
                    .collect(Collectors.toList());

            page = filteredLevelsToPage(pageRequest, filteredLiked);
        }

        if (levelId.isPresent()) {
            List<ILevel> filteredLiked = page.filter(level -> level.getId().longValue() == levelId.get())
                    .stream()
                    .collect(Collectors.toList());

            page = filteredLevelsToPage(pageRequest, filteredLiked);
        }


        return page;
    }

    private Page<ILevel> getOwnedLevels(PageRequest pageRequest, Optional<String> title, User actualUser) {
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

    //Returns the path of the image
    private String saveImageForLevel(Level level, byte[] image) {
        String fileName = UUID.randomUUID() + "_" + level.getTitle() + ".png";
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(image)) {
            Files.createDirectories(uploadDir);
            Path filePath = uploadDir.resolve(fileName);
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
            level.setImagePath(fileName);
            return fileName;
        } catch (IOException ex) {
            System.out.println("Error al guardar la imagen " + ex.toString());
        }
        return null;
    }

    public byte[] getImageByImagePath(String imageName) throws IOException {
        Path filePath = uploadDir.resolve(imageName);
        System.out.println(filePath);
        if (Files.exists(filePath)) {
            return Files.readAllBytes(filePath);
        } else {
            return null;
        }
    }

    private Page<ILevel> filteredLevelsToPage(PageRequest pageRequest, List<ILevel> filteredLiked) {

        int start = (int) pageRequest.getOffset();
        int end = Math.min((start + pageRequest.getPageSize()), filteredLiked.size());

        List<ILevel> pageContent = filteredLiked.subList(start, end);

        System.out.println("Get Liked Levels");

        return new PageImpl<>(pageContent, pageRequest, filteredLiked.size());


    }

}
