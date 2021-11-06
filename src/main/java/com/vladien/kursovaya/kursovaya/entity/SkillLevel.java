package com.vladien.kursovaya.kursovaya.entity;

public enum SkillLevel {
    NOVICE("Новичок"),
    EXPERIENCED("Опытный"),
    ADVANCED("Продвинутый"),
    PROFESSIONAL("Профессионал");

    private String russianAnalogue;

    public String getRussianAnalogue() {
        return russianAnalogue;
    }

    SkillLevel(String russianVariation) {
        this.russianAnalogue = russianVariation;
    }
}
