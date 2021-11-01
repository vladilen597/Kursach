package com.vladien.kursovaya.kursovaya.repository;

import com.vladien.kursovaya.kursovaya.entity.TrainingCourse;
import com.vladien.kursovaya.kursovaya.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TrainingCourseRepository extends JpaRepository<TrainingCourse, Long> {
    List<TrainingCourse> findAllByOwner(User owner);
    Optional<TrainingCourse> findByOwnerAndCourseName(User owner, String courseName);
}
