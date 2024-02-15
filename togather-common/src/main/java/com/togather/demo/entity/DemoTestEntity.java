package com.togather.demo.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "demo_test")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DemoTestEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "firstName")
    private String firstName;

    @Column(name = "lastName")
    private String lastName;
}
