package com.vladien.kursovaya.kursovaya.entity.dto;

import lombok.Data;

import java.util.List;

@Data
public class ChatDto {
    private String chatName;
    private List<String> membersUsernames;
}