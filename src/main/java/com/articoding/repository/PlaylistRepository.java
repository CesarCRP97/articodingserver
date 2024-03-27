package com.articoding.repository;

import com.articoding.model.Playlist;
import com.articoding.model.User;
import com.articoding.model.in.IPlaylist;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface PlaylistRepository extends JpaRepository<Playlist, Long> {

    <T> T findById(Long id, Class<T> type);

    <T> Streamable<T> findBy(Class<T> type);


    <T> Streamable<T> findByOwnerAndEnabledTrue(User owner, Class<T> type);


    <T> Streamable<T> findByTitleContains(String s, Class<T> type);

    <T> Streamable<T> findByOwnerAndEnabledTrueAndTitleContains(User actualUser, String s, Class<T> type);

    <T> Streamable<T> findByPublicLevelTrue(Class<T> type);

    <T> Streamable<T> findByIdInAndPublicLevelTrue(Set<Long> likedIds, Class<T> type);
}
