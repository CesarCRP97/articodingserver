package com.articoding.repository;

import com.articoding.model.ClassRoom;
import com.articoding.model.Level;
import com.articoding.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LevelRepository extends JpaRepository<Level, Long> {
    <T> T findById(Long id, Class<T> type);

    <T> Page<T> findBy(Pageable pageable, Class<T> type);

    <T> Page<T> findByTitleContains(Pageable pageable, String title, Class<T> type);

    <T> Page<T> findByOwnerAndActiveTrue(User owner, Pageable pageable, Class<T> type);

    <T> Page<T> findByOwnerAndActiveTrueAndTitleContains(User owner, String title, Pageable pageable, Class<T> type);

    <T> Page<T> findByPublicLevelTrue(Pageable pageable, Class<T> type);

    <T> Page<T> findByPublicLevelTrueAndTitleContains(Pageable pageable, Class<T> type, String title);

    <T> Page<T> findByPublicLevelTrueLikesTrue(User actualUser, Pageable pageable, Class<T> type);

    <T> Page<T> findByPublicLevelTrueLikesTrueAndTitleContains(User actualUser,Pageable pageable, Class<T> type, String title);

    <T> Page<T> findByClassRoomsAndActiveTrue(ClassRoom classRoom, Pageable pageable, Class<T> type);

    <T> Page<T> findByClassRoomsAndActiveTrueAndTitleContains(ClassRoom classRoom, String title, Pageable pageable, Class<T> type);
}
