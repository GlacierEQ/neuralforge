package com.cenfotec.p3.neuralforge_api.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "weeks")
public class WeekEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "virtual_class_id", nullable = false)
    private VirtualClassEntity virtualClass;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "week_id")
    private List<DynamicContentEntity> content;

    @Column(nullable = false)
    private String topic;

    @Column(nullable = false)
    private Boolean visible;
}
