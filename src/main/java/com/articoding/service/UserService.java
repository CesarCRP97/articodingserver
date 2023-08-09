package com.articoding.service;

import com.articoding.RoleHelper;
import com.articoding.error.ErrorNotFound;
import com.articoding.error.NotAuthorization;
import com.articoding.error.RestError;
import com.articoding.model.*;
import com.articoding.model.in.IUser;
import com.articoding.model.in.IUserDetail;
import com.articoding.model.in.UpdateUserForm;
import com.articoding.repository.ClassRepository;
import com.articoding.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    RoleHelper roleHelper;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ClassRepository classRepository;

    @Autowired
    private PasswordEncoder bcryptEncoder;

    public User getActualUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return userRepository.findByUsername(authentication.getName());
    }

    public Long save(UserForm user) {
        /** Comprobamos que sea, mínimo profesor */
        User actualUser = this.getActualUser();
        if(!roleHelper.can(actualUser.getRole(),"ROLE_TEACHER")) {
            throw new  NotAuthorization("crear usuarios");
        }

        /** Comprobamos que no exista un usuario con ese username */
        if (userRepository.findByUsername(user.getUsername()) != null) {
            throw new RestError("El usuario con nombre " + user.getUsername() + " ya existe.");
        }

        User newUser = prepareUser(user, actualUser);

        User createdUser = userRepository.save(newUser);

        return createdUser.getId();
    }

    private User prepareUser(UserForm user, User actualUser) {
        User newUser = new User();
        newUser.setUsername(user.getUsername());
        newUser.setPassword(bcryptEncoder.encode(user.getPassword()));
        newUser.setEnabled(true);

        /** Añadimos los roles, comprobando que el usuario tiene permisos para asignarlos según su nivel */
            Role newRole = null;
            switch (user.getRole()) {
                case "ROLE_ADMIN": {
                    if (roleHelper.isAdmin(actualUser)) {
                        newRole = roleHelper.getAdmin();
                    } else {
                        throw new NotAuthorization("crear usuarios ADMIN");
                    }
                }
                break;

                case "ROLE_TEACHER": {
                    if (roleHelper.isAdmin(actualUser)) {
                        newRole = roleHelper.getTeacher();
                    } else {
                        throw new NotAuthorization("crear usuarios PROFESOR");
                    }
                }
                break;

                case "ROLE_USER": {
                    newRole = roleHelper.getUser();
                }
                break;

                default:
                    throw new ErrorNotFound("role", -1L);
            }
        newUser.setRole(newRole);

        /** Añadimos las clases, comprobando que el usuario es profesor de ellas*/
        List<ClassRoom> classRoomList = new ArrayList<>();
        for (Long idClass: user.getClasses()) {
            ClassRoom classRoom = classRepository.findById(idClass)
                    .orElseThrow(() -> new ErrorNotFound("clase", idClass));
            if (!classRoom.getTeachers().stream().anyMatch(t -> t.getId() == actualUser.getId())) {
                throw new NotAuthorization("crear alumnos en la clase " + idClass);
            }
            classRoom.getStudents().add(newUser);
            classRoomList.add(classRoom);
        }
        newUser.setClasses(classRoomList);
        return newUser;
    }

    public void saveAll(List<UserForm> userFormList) {
        /** Comprobamos que sea, mínimo profesor */
        User actualUser = this.getActualUser();
        if(!roleHelper.can(actualUser.getRole(),"ROLE_TEACHER")) {
            throw new  NotAuthorization("crear usuarios");
        }
        List<User> usersList = new ArrayList<>();

        userFormList.forEach(u -> usersList.add(prepareUser(u, actualUser)));

        userRepository.saveAll(usersList);
    }
    public Page<IUser> geAllUser(PageRequest pageable, Optional<Long> clase, boolean teacher) {
        User actualUser = this.getActualUser();
        /** Comprobamos que sea, mínimo profesor */
        if(!roleHelper.can(actualUser.getRole(),"ROLE_TEACHER")) {
            throw new  NotAuthorization("obtener usuarios");
        }

        /** Si quiere todos los usuarios de una clase*/
        if(clase.isPresent()) {
            /** Comprobamos que exista la clase*/
            ClassRoom classRoom = classRepository.findById(clase.get())
                    .orElseThrow(() -> new ErrorNotFound("clase", clase.get()));

            /** Comprobamos que sea ADMIN o profesor de la clase en cuestion */
            if (!roleHelper.isAdmin(actualUser) && !classRoom.getTeachers().stream().anyMatch(t -> t.getId() == actualUser.getId())) {
                throw new NotAuthorization("obtener alumnos de la clase " + clase.get());
            }
            if(teacher) {
                return userRepository.findByOwnerClassRoomsIn(pageable, Arrays.asList(classRoom) , IUser.class);
            } else {
                return userRepository.findByClassRoomsIn(pageable, Arrays.asList(classRoom) , IUser.class);
            }

        } else {
            /** Si es admin, mostramos todos */
            if(roleHelper.isAdmin(actualUser)) {
                return userRepository.findBy(pageable, IUser.class);
            } else {
                /** Si es profesor mostramos solo USER */
                return userRepository.findByRole(pageable, roleHelper.getUser());
            }
        }
    }

    public IUserDetail getUser(Long userId) {
        User actualUser = this.getActualUser();
        /** Comprobamos que sea, mínimo profesor */
        if(!roleHelper.can(actualUser.getRole(),"ROLE_TEACHER")) {
            throw new  NotAuthorization("obtener usuario");
        }

        /** Comprobamos que existe */
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ErrorNotFound("clase", userId));

        /** Si es solo profesor, nos aseguramos de que el usuario es alumno */
        if(!roleHelper.isAdmin(actualUser)) {
            if (!roleHelper.isUser(user)) {
                throw new NotAuthorization("obtener usuario");
            }
        }

        return userRepository.findById(userId, IUserDetail.class);
    }

    public Long update(Long userId, UpdateUserForm updateUserForm) {

        /** Comprobamos que existe el usuario */
        User userOld = userRepository.findById(userId)
                .orElseThrow(() -> new ErrorNotFound("usuario", userId));

        User actualUser = getActualUser();
        /** Comprobamos que sea el propio usuario o usuario ADMIN */
        if (!(actualUser.getId() == userId) &&
                !roleHelper.isAdmin(actualUser)) {
            throw new NotAuthorization("modificar el usuario " + userId);
        } else {
            /** Podemos modificar */
            if (updateUserForm.getPassword() != null ) {
                userOld.setPassword(bcryptEncoder.encode(updateUserForm.getPassword()));
            }
            if (updateUserForm.isEnabled() != null ) {
                userOld.setEnabled(updateUserForm.isEnabled());
            }

            return userRepository.save(userOld).getId();

        }
    }
}
