package com.vladien.kursovaya.kursovaya.entity.dto;

import com.vladien.kursovaya.kursovaya.entity.Message;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ChatRepresentation {
    private Long id;
    private Long trainingCourseId;
    private List<Message> messages;
    private String name;
    private List<UserRepresentationDto> participants;
}
