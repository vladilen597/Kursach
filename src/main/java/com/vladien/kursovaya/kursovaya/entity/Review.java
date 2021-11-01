package com.vladien.kursovaya.kursovaya.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@RequiredArgsConstructor
@Entity
@AllArgsConstructor
@Builder
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private int rating;
    private String comment;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "rated_id")
    private User personRated;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "author_id")
    private User authorOfRating;
}
