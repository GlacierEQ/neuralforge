package com.cenfotec.p3.neuralforge_api.model.entity;
import jakarta.persistence.*;
import lombok.*;

    @Entity
    @Table(name = "learnspaces")
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public class LearnspaceEntity {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(length = 100, nullable = false)
        private String nombre;

        @Column(length = 500)
        private String descripcion;

        @Column(nullable = false)
        private String visibilidad;
    }

