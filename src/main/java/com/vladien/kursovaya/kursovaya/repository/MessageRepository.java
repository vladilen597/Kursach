package com.vladien.kursovaya.kursovaya.repository;

import com.vladien.kursovaya.kursovaya.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
}
