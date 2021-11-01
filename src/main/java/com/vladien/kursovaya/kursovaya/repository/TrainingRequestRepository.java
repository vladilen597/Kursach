package com.vladien.kursovaya.kursovaya.repository;

import com.vladien.kursovaya.kursovaya.entity.TrainingRequest;
import com.vladien.kursovaya.kursovaya.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TrainingRequestRepository extends JpaRepository<TrainingRequest, Long> {
    List<TrainingRequest> findAllByIsApprovedAndMentor(boolean approved, User Mentor);
    List<TrainingRequest> findAllByRequester(User requester);
    List<TrainingRequest> findAllByMentor(User mentor);
}
