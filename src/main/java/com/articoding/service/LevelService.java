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
import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
            String imagePath = this.saveImage(level, image);
        }

        Level newLevel = levelRepository.save(level);

        return newLevel.getId();
    }

    //Todo - Que devuelva LevelWithImageDTO y la lógica crear el DTO se quede en LevelService
    //       en vez de hacerse en el controller
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

    public Page<LevelWithImageDTO> getLevels(PageRequest pageRequest, Comparator<ILevel> comparator, Optional<Long> classId,
                                             Optional<Long> userId, Optional<Boolean> publicLevels, Optional<Boolean> liked,
                                             Optional<String> title, Optional<String> owner, Optional<Long> levelId) {
        List<ILevel> levels;
        User actualUser = userService.getActualUser();
        /** If there is a classId then returns levels from the classroom */
        if (classId.isPresent()) {
            levels = getLevelsFromClass(pageRequest, actualUser, classId, title);
        } else if (userId.isPresent()) {
            levels = getLevelsFromUserId(actualUser, userId);
        } else {
            if (publicLevels.isPresent()) {
                /** If publicLevels is true, returns all public levels. */
                levels = getPublicLevels(liked, title, owner, levelId);
            } else {
                /** If it's ADMIN then it returns every level */
                levels = getOwnedLevels(title, actualUser);
            }
        }

        //Converts the list of levels to a Page given PageRequest
        Page<ILevel> page = filteredLevelsToPage(pageRequest, comparator, levels);
        return toLevelWithImageDTO(page);
    }


    private Page<LevelWithImageDTO> toLevelWithImageDTO(Page<ILevel> oldPage) {
        return oldPage.map(this::toLevelWithImageDTO);
    }

    public List<LevelWithImageDTO> toLevelWithImageDTO(List<ILevel> levelList) {
        List<LevelWithImageDTO> newList = new ArrayList<>();
        for (ILevel level : levelList) {
            newList.add(toLevelWithImageDTO(level));
        }
        return newList;
    }

    public LevelWithImageDTO toLevelWithImageDTO(ILevel level) {
        LevelWithImageDTO levelWithImageDTO = new LevelWithImageDTO();
        levelWithImageDTO.setLevel(level);
        try {
            String imageName = level.getImagePath();
            if (imageName != null)
                levelWithImageDTO.setImage(this.getImage(imageName));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return levelWithImageDTO;
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

    private List<ILevel> getLevelsFromClass(PageRequest pageRequest, User actualUser, Optional<Long> classId, Optional<String> title) {
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
            return levelRepository.findByClassRoomsAndActiveTrueAndTitleContains(classRoom, title.get(), ILevel.class);
        } else {
            return levelRepository.findByClassRoomsAndActiveTrue(classRoom, ILevel.class);
        }
    }

    private List<ILevel> getLevelsFromUserId(User actualUser, Optional<Long> userId) {
        /** If there is a userId it checks if the user is ADMIN or . */
        if (!roleHelper.isAdmin(actualUser)) {
            throw new NotAuthorization("ver niveles del usuario " + userId.get());
        } else {
            return levelRepository.findByOwnerAndActiveTrue(actualUser, ILevel.class);
        }
    }

    private List<ILevel> getPublicLevels(Optional<Boolean> liked, Optional<String> title, Optional<String> owner, Optional<Long> levelId) {
        Streamable<ILevel> levels;

        //Big query, return a potentially giant list
        if (liked.isPresent() && liked.get()) {
            Set<Long> likedIds = userService.getActualUser().getLikedLevels();
            levels = levelRepository.findByIdInAndPublicLevelTrue(likedIds, ILevel.class);
        } else levels = levelRepository.findByPublicLevelTrue(ILevel.class);


        if (title.isPresent()) {
            levels = filterStreamable(levels, levelRepository.findByTitleContains(title.get(), ILevel.class));
        }
        if (levelId.isPresent()) {
            levels = levels.filter(level -> level.getId().longValue() == levelId.get());
        }
        //It uses a filter because we need direct access to the name of the owner
        if (owner.isPresent()) {
            levels = levels.filter(level -> Objects.equals(level.getOwner().getUsername(), owner.get()));
        }

        return levels.stream().collect(Collectors.toList());
    }


    private List<ILevel> getOwnedLevels(Optional<String> title, User actualUser) {
        /** If it's ADMIN then it returns every level */
        if (roleHelper.isAdmin(actualUser)) {
            if (title.isPresent()) {
                return levelRepository.findByTitleContains(title.get(), ILevel.class).stream().collect(Collectors.toList());
            } else {
                return levelRepository.findBy(ILevel.class);
            }
        } else {
            /** Otherwise, returns only the levels created by the user */
            if (title.isPresent()) {
                return levelRepository.findByOwnerAndActiveTrueAndTitleContains(actualUser, title.get(), ILevel.class);
            } else {
                return levelRepository.findByOwnerAndActiveTrue(actualUser, ILevel.class);
            }
        }
    }

    //Returns the path of the image
    private String saveImage(Level level, byte[] image) {
        String fileName = UUID.randomUUID() + "_" + level.getTitle() + ".png";
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(image)) {
            Files.createDirectories(uploadDir);
            Path filePath = uploadDir.resolve(fileName);
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
            level.setImagePath(fileName);
            return fileName;
        } catch (IOException ex) {
            System.out.println("Error al guardar la imagen " + ex);
        }
        return null;
    }

    public byte[] getImage(String imageName) throws IOException {
        Path filePath = uploadDir.resolve(imageName);
        System.out.println(filePath);
        if (Files.exists(filePath)) {
            return Files.readAllBytes(filePath);
        } else {
            return null;
        }
    }

     private Streamable<ILevel> filterStreamable(Streamable<ILevel> s1, Streamable<ILevel> s2){
        Set<ILevel> set2 = new HashSet<>();
        s2.forEach(set2::add);
        return s1.filter(set2::contains);
     }

    private Page<ILevel> filteredLevelsToPage(PageRequest pageRequest, Comparator<ILevel> comparator, List<ILevel> filteredLiked) {

        int start = (int) pageRequest.getOffset();
        int end = Math.min((start + pageRequest.getPageSize()), filteredLiked.size());

        filteredLiked.sort(comparator);

        List<ILevel> pageContent = filteredLiked.subList(start, end);


        return new PageImpl<>(pageContent, pageRequest, filteredLiked.size());
    }

}
