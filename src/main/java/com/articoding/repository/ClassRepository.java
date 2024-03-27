package com.articoding.repository;

import com.articoding.model.ClassRoom;
import com.articoding.model.in.IClassRoom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClassRepository extends JpaRepository<ClassRoom, Long> {

    List<IClassRoom> findByStudentsId(Long id);

    <T> Page<T> findByStudentsId(Long id, Pageable pageable, Class<T> projection);

    <T> Page<T> findByStudentsIdAndEnabledTrue(Long id, Pageable pageable, Class<T> projection);

    <T> Page<T> findByStudentsIdAndNameContains(Long id, String name, Pageable pageable, Class<T> projection);

    <T> Page<T> findByStudentsIdAndNameContainsAndEnabledTrue(Long id, String name, Pageable pageable, Class<T> projection);

    <T> Page<T> findByTeachersId(Long id, Pageable pageable, Class<T> projection);

    <T> Page<T> findByTeachersIdAndEnabledTrue(Long id, Pageable pageable, Class<T> projection);

    <T> Page<T> findByTeachersIdAndNameContains(Long id, String name, Pageable pageable, Class<T> projection);

    <T> Page<T> findByTeachersIdAndNameContainsAndEnabledTrue(Long id, String name, Pageable pageable, Class<T> projection);

    <T> Page<T> findByLevelsId(Long id, Pageable pageable, Class<T> projection);

    <T> Page<T> findByLevelsIdAndEnabledTrue(Long id, Pageable pageable, Class<T> projection);

    <T> Page<T> findByLevelsIdAndNameContains(Long id, String name, Pageable pageable, Class<T> projection);

    <T> Page<T> findByLevelsIdAndNameContainsAndEnabledTrue(Long id, String name, Pageable pageable, Class<T> projection);

    <T> Page<T> findBy(Pageable pageable, Class<T> projection);

    <T> Page<T> findByAndNameContains(Pageable pageable, String name, Class<T> projection);

    List<IClassRoom> findByTeachersId(Long idUser);

    <T> T findById(Long id, Class<T> type);

    <T> Optional<T> findByClassKey(String key);

}