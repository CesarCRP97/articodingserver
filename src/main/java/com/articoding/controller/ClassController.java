package com.articoding.controller;

import com.articoding.model.in.ClassForm;
import com.articoding.model.in.CompletedLevelsDTO;
import com.articoding.model.in.IClassRoom;
import com.articoding.model.in.IClassRoomDetail;
import com.articoding.model.in.IUid;
import com.articoding.model.in.UpdateClassRoomForm;
import com.articoding.model.rest.CreatedRef;
import com.articoding.service.ClassService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/classes")
public class ClassController {
    @Autowired
    ClassService classService;

    @PostMapping
    public ResponseEntity<CreatedRef> createClass(@RequestBody ClassForm classForm) {
        return ResponseEntity.ok(new CreatedRef("classes/" + classService.createClass(classForm)));
    }

    @GetMapping
    public ResponseEntity<Page<IClassRoom>> getAllClass(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "user", required = false) Optional<Long> userId,
            @RequestParam(name = "teacher", required = false) Optional<Long> teachId,
            @RequestParam(name = "level", required = false) Optional<Long> levelId,
            @RequestParam(name = "title", required = false) Optional<String> title) {
        return ResponseEntity.ok(classService.getClasses(PageRequest.of(page, size), userId, teachId, levelId, title));
    }

    @GetMapping("/{classId}")
    public ResponseEntity<IClassRoomDetail> getById(@PathVariable(value = "classId") Long classId) {
        return ResponseEntity.ok(classService.getById(classId));
    }

    @PutMapping("/{classId}")
    public ResponseEntity<CreatedRef> updateClassRoom(@PathVariable(value = "classId") Long classId,
                                                      @RequestBody UpdateClassRoomForm updateClassRoomForm) {
        return ResponseEntity.ok(new CreatedRef("classes/" + classService.updateClassRoom(classId, updateClassRoomForm)));
    }

    @PostMapping("/enterclass/{classKey}")
    public ResponseEntity<CreatedRef> enterClass(@PathVariable(value = "classKey") String classKey) {
        return ResponseEntity.ok(new CreatedRef("classes/" + classService.enterClass(classKey)));
    }


    @PostMapping("/{classId}/levels")
    public ResponseEntity<CreatedRef> addLevelToClass(@PathVariable(value = "classId") Long classId,
                                                      @RequestBody List<IUid> levelId) {
        return ResponseEntity.ok(new CreatedRef("classes/" + classService.addLevel(classId, levelId)));
    }

    @DeleteMapping("/{classId}/levels/{levelId}")
    public ResponseEntity<CreatedRef> deleteLevelOfClass(@PathVariable(value = "classId") Long classId,
                                                         @PathVariable(value = "levelId") Long levelId) {
        return ResponseEntity.ok(new CreatedRef("classes/" + classService.deleteLevel(classId, levelId)));
    }

    @PostMapping("/{classId}/students")
    public ResponseEntity<CreatedRef> addStudentsToClass(@PathVariable(value = "classId") Long classId,
                                                         @RequestBody List<String> usersId) {
        return ResponseEntity.ok(new CreatedRef("classes/" + classService.addStudents(classId, usersId)));
    }

    @DeleteMapping("/{classId}/students/{studentId}")
    public ResponseEntity<CreatedRef> deleteStudentOfClass(@PathVariable(value = "classId") Long classId,
                                                           @PathVariable(value = "studentId") Long studentId) {
        return ResponseEntity.ok(new CreatedRef("classes/" + classService.deleteStudent(classId, studentId)));
    }

    @PostMapping("/{classId}/teachers")
    public ResponseEntity<CreatedRef> addTeachersToClass(@PathVariable(value = "classId") Long classId,
                                                         @RequestBody List<String> usersId) {
        return ResponseEntity.ok(new CreatedRef("classes/" + classService.addTeachers(classId, usersId)));
    }

    @DeleteMapping("/{classId}/teachers/{teacherId}")
    public ResponseEntity<CreatedRef> deleteTeacherOfClass(@PathVariable(value = "classId") Long classId,
                                                           @PathVariable(value = "teacherId") Long teacherId) {
        return ResponseEntity.ok(new CreatedRef("classes/" + classService.deleteTeacher(classId, teacherId)));
    }

    @PostMapping("/{classId}/levels_completed/{levelId}")
    public ResponseEntity<Void> completeLevel(@PathVariable(value = "classId") Long classId,
                                              @PathVariable(value = "levelId") Long levelId) {
        classService.completeLevel(classId, levelId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{classId}/completed_levels")
    public ResponseEntity<CompletedLevelsDTO> getCompletedLevels(@PathVariable(value = "classId") Long classId) {
        return ResponseEntity.ok(classService.getCompletedLevels(classId));
    }
}
