package com.bevans.kafka.connect.springboot.data.user;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_data")
@Getter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class UserData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "age")
    private int age;
}
