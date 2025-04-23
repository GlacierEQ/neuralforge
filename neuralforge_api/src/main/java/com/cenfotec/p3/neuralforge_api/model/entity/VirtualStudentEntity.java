package com.cenfotec.p3.neuralforge_api.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "virtual_students")
public class VirtualStudentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "virtual_class_id", nullable = false)
    private VirtualClassEntity virtualClass;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "FK_virtualstudent_user"))
    @OnDelete(action = OnDeleteAction.CASCADE)
    private UserEntity user;

    @Column
    private Double grade;
}
