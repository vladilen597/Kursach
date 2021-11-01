package com.vladien.kursovaya.kursovaya.repository;

import com.vladien.kursovaya.kursovaya.entity.CoreSkill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CoreSkillsRepository extends JpaRepository<CoreSkill, Long> {
    CoreSkill findByName(String name);
}
