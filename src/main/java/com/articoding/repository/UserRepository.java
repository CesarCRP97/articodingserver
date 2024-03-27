package com.articoding.repository;

import com.articoding.model.ClassRoom;
import com.articoding.model.Role;
import com.articoding.model.User;
import com.articoding.model.in.IUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

// Todo - Refactorizar
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    User findByUsername(String username);

    Page<IUser> findByRole(Pageable pageable, Role role);

    Page<IUser> findByRoleAndUsernameContains(Pageable pageable, Role role, String username);

    <T> Page<T> findByClassRoomsIn(Pageable pageable, List<ClassRoom> classRooms, Class<T> type);

    <T> Page<T> findByClassRoomsInAndUsernameContains(Pageable pageable, List<ClassRoom> classRooms, String username, Class<T> type);

    <T> Page<T> findByOwnerClassRoomsIn(Pageable pageable, List<ClassRoom> classRooms, Class<T> type);

    <T> Page<T> findByOwnerClassRoomsInAndUsernameContains(Pageable pageable, List<ClassRoom> classRooms, String username, Class<T> type);


    <T> Page<T> findBy(Pageable pageable, Class<T> type);

    <T> Page<T> findByUsernameContains(Pageable pageable, String username, Class<T> type);

    <T> T findById(Long id, Class<T> type);
}