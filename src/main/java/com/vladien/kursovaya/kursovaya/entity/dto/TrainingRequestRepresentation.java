package com.vladien.kursovaya.kursovaya.entity.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class TrainingRequestRepresentation {
    private Long requestId;
    private Long courseId;
    private UserRepresentationDto mentor;
    private UserRepresentationDto requester;
    private LocalDateTime creationTime;
}
