package com.vladien.kursovaya.kursovaya.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "ChatRoom")
public class ChatRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToMany(mappedBy = "chatRoom")
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<Message> messages;

    @ManyToMany
    @LazyCollection(LazyCollectionOption.FALSE)
    @JsonIgnore
    @JoinTable(
            name = "ChatRoom_Users",
            joinColumns = {@JoinColumn(name = "hatRoom_id")},
            inverseJoinColumns = {@JoinColumn(name = "participants_id")}
    )
    private Set<User> participants;

    private String name;
}
