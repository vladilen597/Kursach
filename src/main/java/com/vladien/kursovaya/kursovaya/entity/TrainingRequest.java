package com.vladien.kursovaya.kursovaya.entity;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class TrainingRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "requester_id")
    private User requester;

    @ManyToOne
    @JoinColumn(name = "training_id")
    private TrainingCourse trainingCourse;

    @ManyToOne
    @JoinColumn(name = "mentor_id")
    private User mentor;

    private Boolean isApproved;
    private LocalDateTime creationDateTime;
}
