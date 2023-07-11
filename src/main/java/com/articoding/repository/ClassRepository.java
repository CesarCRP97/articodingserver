package com.articoding.repository;

import com.articoding.model.ClassRoom;
import com.articoding.model.in.IClassRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClassRepository extends JpaRepository<ClassRoom, Integer> {

  List<IClassRoom> findByStudentsId(Long id);

  List<IClassRoom> findByTeachersId(Long idUser);

}