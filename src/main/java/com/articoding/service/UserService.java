package com.articoding.service;

import com.articoding.RoleHelper;
import com.articoding.error.ErrorNotFound;
import com.articoding.error.NotAuthorization;
import com.articoding.error.RestError;
import com.articoding.model.ClassRoom;
import com.articoding.model.Role;
import com.articoding.model.User;
import com.articoding.model.UserForm;
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
import java.util.Collections;
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
        /** Verifies user is at least ROLE_TEACHER */
        User actualUser = this.getActualUser();
        if (!roleHelper.can(actualUser.getRole(), "ROLE_TEACHER")) {
            throw new NotAuthorization("crear usuarios");
        }

        User newUser = prepareUser(user, actualUser);

        User createdUser = userRepository.save(newUser);

        return createdUser.getId();
    }

    public Long registerStudent(UserForm userForm) {
        User newUser = prepareUser(userForm);

        User createdUser = userRepository.save(newUser);

        return createdUser.getId();
    }

    private User prepareUser(UserForm userForm) {
        User newUser = new User();
        newUser.setUsername(userForm.getUsername());
        newUser.setRole(roleHelper.getUser());
        newUser.setPassword(bcryptEncoder.encode(userForm.getPassword()));
        newUser.setEnabled(true);

        if (!userForm.getUsername().matches("^[a-zA-Z0-9-_]+$")) {
            throw new RestError("Username can only contain alphanumerics and '_'.");
        }

        if (userRepository.findByUsername(userForm.getUsername()) != null) {
            throw new RestError("Username '" + userForm.getUsername() + "' already exists.");
        }

        return newUser;
    }

    private User prepareUser(UserForm user, User actualUser) {
        User newUser = new User();
        newUser.setUsername(user.getUsername());
        newUser.setPassword(bcryptEncoder.encode(user.getPassword()));
        newUser.setEnabled(true);

        /** Comprobamos que no exista un usuario con ese username */
        if (userRepository.findByUsername(user.getUsername()) != null) {
            throw new RestError("El usuario con nombre " + user.getUsername() + " ya existe.");
        }

        /** Comprobamos que el nombre sea correcto*/
        if (!user.getUsername().matches("^[a-zA-Z0-9-_]+$")) {
            throw new RestError("El nombre solo puede contener carácteres alfanumericos y '_' .");
        }

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
                throw new RestError("Los posibles roles son: ROLE_USER,ROLE_TEACHER,ROLE_ADMIN");
        }
        newUser.setRole(newRole);

        /** Añadimos las clases, comprobando que el usuario es profesor de ellas*/
        List<ClassRoom> classRoomList = new ArrayList<>();

        /** Si hay clases, comprobamos que sea alumno*/
        if (!user.getClasses().isEmpty() && !roleHelper.isUser(newUser)) {
            throw new RestError("No se puede matricular en clases al alumno " + user.getUsername() + " con role " + user.getRole());
        }

        for (Long idClass : user.getClasses()) {
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
        if (!roleHelper.can(actualUser.getRole(), "ROLE_TEACHER")) {
            throw new NotAuthorization("crear usuarios");
        }
        List<User> usersList = new ArrayList<>();

        userFormList.forEach(u -> usersList.add(prepareUser(u, actualUser)));

        userRepository.saveAll(usersList);
    }

    public Page<IUser> geAllUser(PageRequest pageable, Optional<Long> clase, boolean teacher, Optional<String> title) {
        User actualUser = this.getActualUser();
        /** Comprobamos que sea, mínimo profesor */
        if (!roleHelper.can(actualUser.getRole(), "ROLE_TEACHER")) {
            throw new NotAuthorization("obtener usuarios");
        }

        /** Si quiere todos los usuarios de una clase*/
        if (clase.isPresent()) {
            /** Comprobamos que exista la clase*/
            ClassRoom classRoom = classRepository.findById(clase.get())
                    .orElseThrow(() -> new ErrorNotFound("clase", clase.get()));

            /** Comprobamos que sea ADMIN o profesor de la clase en cuestion */
            if (!roleHelper.isAdmin(actualUser) && !classRoom.getTeachers().stream().anyMatch(t -> t.getId() == actualUser.getId())) {
                throw new NotAuthorization("obtener alumnos de la clase " + clase.get());
            }
            if (teacher) {
                if (title.isPresent()) {
                    return userRepository.findByOwnerClassRoomsInAndUsernameContains(pageable, Collections.singletonList(classRoom), title.get(), IUser.class);
                } else {
                    return userRepository.findByOwnerClassRoomsIn(pageable, Collections.singletonList(classRoom), IUser.class);
                }
            } else {
                if (title.isPresent()) {
                    return userRepository.findByClassRoomsInAndUsernameContains(pageable, Collections.singletonList(classRoom), title.get(), IUser.class);
                } else {
                    return userRepository.findByClassRoomsIn(pageable, Collections.singletonList(classRoom), IUser.class);
                }
            }

        } else {
            /** Si es admin, mostramos todos */
            if (roleHelper.isAdmin(actualUser)) {
                if (title.isPresent()) {
                    return userRepository.findByUsernameContains(pageable, title.get(), IUser.class);
                } else {
                    return userRepository.findBy(pageable, IUser.class);
                }
            } else {
                /** Si es profesor mostramos solo USER */
                if (title.isPresent()) {
                    return userRepository.findByRoleAndUsernameContains(pageable, roleHelper.getUser(), title.get());
                } else {
                    return userRepository.findByRole(pageable, roleHelper.getUser());
                }
            }
        }
    }

    public IUserDetail getUser(Long userId) {
        User actualUser = this.getActualUser();
        /** Comprobamos que sea, mínimo profesor */
        if (!roleHelper.can(actualUser.getRole(), "ROLE_TEACHER")) {
            throw new NotAuthorization("obtener usuario");
        }

        /** Comprobamos que existe */
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ErrorNotFound("clase", userId));

        /** Si es solo profesor, nos aseguramos de que el usuario es alumno */
        if (!roleHelper.isAdmin(actualUser)) {
            if (!roleHelper.isUser(user)) {
                throw new NotAuthorization("obtener usuario");
            }
        }

        return userRepository.findById(userId, IUserDetail.class);
    }

    //Todo - Si es el profesor que lo haya creado.
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
            if (updateUserForm.getPassword() != null) {
                userOld.setPassword(bcryptEncoder.encode(updateUserForm.getPassword()));
            }
            if (updateUserForm.isEnabled() != null) {
                userOld.setEnabled(updateUserForm.isEnabled());
            }

            return userRepository.save(userOld).getId();

        }
    }
    public void updateActualUser(User u){
        userRepository.save(u);
    }

}
