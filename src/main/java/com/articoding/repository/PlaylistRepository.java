package com.articoding.repository;

import com.articoding.model.Playlist;
import com.articoding.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlaylistRepository extends JpaRepository<Playlist, Long> {

    <T> T findById(Long id, Class<T> type);

    <T> Page<T> findBy(Pageable pageable, Class<T> type);


    <T> Page<T> findByOwnerAndEnabledTrue(User owner, Pageable pageable, Class<T> type);


    <T> Page<T> findByTitleContains(Pageable pageRequest, String s, Class<T> iPlaylistClass);

    <T> Page<T> findByOwnerAndEnabledTrueAndTitleContains(User actualUser, String s, Pageable pageRequest, Class<T> iPlaylistClass);

}
