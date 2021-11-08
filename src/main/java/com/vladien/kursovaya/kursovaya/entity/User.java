package com.vladien.kursovaya.kursovaya.entity;

import lombok.*;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String username;
    private String firstName;
    private String lastName;
    private String patronymic;
    private String password;
    private String aboutMe;
    private String profilePicture;
    private boolean enabled;

    @OneToMany(mappedBy = "personRated", fetch = FetchType.LAZY)
    private Set<Review> receivedReviews;

    @OneToMany(mappedBy = "authorOfRating", fetch = FetchType.LAZY)
    private Set<Review> givenReviews;

    @ManyToMany(mappedBy = "users", fetch = FetchType.EAGER)
    private List<CoreSkill> coreSkills;

    @ManyToMany(mappedBy = "participants")
    private Set<ChatRoom> chatRooms;

    @ManyToMany(mappedBy = "mentors", fetch = FetchType.LAZY)
    private List<User> students;

    @ManyToMany
    @JoinTable(
            name = "students_mentors",
            joinColumns = {@JoinColumn(name = "student_id")},
            inverseJoinColumns = {@JoinColumn(name = "mentor_id")}
    )
    private List<User> mentors;

    @OneToMany(mappedBy = "owner", fetch = FetchType.LAZY)
    private List<TrainingCourse> coursesOfMentor;

    @ManyToMany(mappedBy = "activeStudents", fetch = FetchType.LAZY)
    private List<TrainingCourse> coursesOfStudents;

    @OneToMany(mappedBy = "mentor", fetch = FetchType.LAZY)
    private List<TrainingRequest> trainingRequests;

    @ElementCollection(targetClass = UserRole.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "user_role", joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING)
    private Set<UserRole> roles;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return enabled;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
}
