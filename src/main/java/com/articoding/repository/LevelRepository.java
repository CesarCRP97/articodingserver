package com.articoding.repository;

import com.articoding.model.ClassRoom;
import com.articoding.model.Level;
import com.articoding.model.User;
import com.articoding.model.in.ILevel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface LevelRepository extends JpaRepository<Level, Long> {
    <T> T findById(Long id, Class<T> type);


    <T> List<T> findBy(Class<T> type);

    <T> Streamable<T> findByTitleContains(String title, Class<T> type);

    <T> List<T> findByOwnerAndActiveTrue(User owner, Class<T> type);

    <T> List<T> findByOwnerAndActiveTrueAndTitleContains(User owner, String title, Class<T> type);

    <T> Streamable<T> findByPublicLevelTrue(Class<T> type);

    <T> List<T> findByPublicLevelTrueAndTitleContains(Class<T> type, String title);

    <T> List<T> findByClassRoomsAndActiveTrue(ClassRoom classRoom, Class<T> type);

    <T> List<T> findByClassRoomsAndActiveTrueAndTitleContains(ClassRoom classRoom, String title, Class<T> type);


    <T> Streamable<T> findByIdInAndPublicLevelTrue(Set<Long> likedIds, Class<T> type);
}
