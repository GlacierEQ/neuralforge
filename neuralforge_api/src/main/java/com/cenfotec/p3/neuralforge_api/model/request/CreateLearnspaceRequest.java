package com.cenfotec.p3.neuralforge_api.model.request;

import jakarta.validation.constraints.*;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateLearnspaceRequest {

    @NotBlank
    @Size(max = 100)
    private String nombre;

    @Size(max = 500)
    private String descripcion;

    @NotBlank
    @Pattern(regexp = "Público|Privado", message = "La visibilidad debe ser 'Público' o 'Privado'")
    private String visibilidad;
}
