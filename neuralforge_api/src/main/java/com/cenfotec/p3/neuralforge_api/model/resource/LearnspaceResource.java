package com.cenfotec.p3.neuralforge_api.model.resource;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class LearnspaceResource {
    private Long id;
    private String nombre;
    private String descripcion;
    private String visibilidad;
}
