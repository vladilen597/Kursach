package com.vladien.kursovaya.kursovaya.repository;

import com.vladien.kursovaya.kursovaya.entity.ChatRoom;
import com.vladien.kursovaya.kursovaya.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    List<ChatRoom> findAllByParticipantsContains(User user);
}
