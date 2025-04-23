package com.cenfotec.p3.neuralforge_api.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.util.List;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "virtual_classes")
public class VirtualClassEntity {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private String id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "owner_id", nullable = false)
    private UserEntity owner;

    @OneToMany(mappedBy = "virtualClass", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WeekEntity> weeks;

    @OneToMany(mappedBy = "virtualClass", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VirtualStudentEntity> students;

    @Column(nullable = false)
    private String title;

    private String description;
}
