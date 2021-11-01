package com.vladien.kursovaya.kursovaya.service;

import com.vladien.kursovaya.kursovaya.entity.CoreSkill;
import com.vladien.kursovaya.kursovaya.entity.User;
import com.vladien.kursovaya.kursovaya.repository.CoreSkillsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class CoreSkillsService {
    private final CoreSkillsRepository coreSkillsRepository;

    public CoreSkill findSkillByName(String skillName) {
        return coreSkillsRepository.findByName(skillName);
    }

    public List<CoreSkill> transformSkillNamesToSkillsWithSaving(List<String> skillNames, User user) {
        List<CoreSkill> skills = skillNames.stream()
                .map(skillName -> {
                    CoreSkill skill = coreSkillsRepository.findByName(skillName);
                    return Objects.requireNonNullElseGet(
                            skill, () -> {
                                CoreSkill newSkill = coreSkillsRepository.save(CoreSkill.builder().name(skillName).build());
                                newSkill.setUsers(new ArrayList<>());
                                return newSkill;
                            });
                })
                .collect(Collectors.toList());
        skills.forEach(skill -> {
            skill.getUsers().add(user);
            coreSkillsRepository.save(skill);
        });
        removeDeletedSkills(user, user.getCoreSkills(), skills);
        return skills;
    }

    public List<CoreSkill> transformSkillNamesToSkills(List<String> skillNames) {
        return skillNames.stream().map(coreSkillsRepository::findByName).collect(Collectors.toList());
    }

    private void removeDeletedSkills(User user, List<CoreSkill> previousSkills, List<CoreSkill> newSkills) {
        previousSkills.removeAll(newSkills);
        previousSkills.forEach(skill -> {
            List<User> users = skill.getUsers();
            users.remove(user);
            skill.setUsers(users);
            coreSkillsRepository.save(skill);
        });
    }
}
