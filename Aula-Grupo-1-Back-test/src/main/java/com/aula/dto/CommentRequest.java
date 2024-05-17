package com.aula.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentRequest {
    @NotBlank(message = "El cuerpo del comentario no puede estar vacío.")
    @NotNull(message = "El cuerpo del comentario no puede ser nulo.")
    @Size(max = 255, message = "El cuerpo del comentario no puede tener más de 255 caracteres.")
    private String text;
    @NotBlank(message = "El creador del comentario no puede estar vacío.")
    @NotNull(message = "El creador del comentario no puede ser nulo.")
    private String creatorUsername;
}
