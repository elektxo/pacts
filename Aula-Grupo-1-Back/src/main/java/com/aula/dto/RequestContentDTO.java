package com.aula.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestContentDTO {
    @NotBlank(message = "El título no puede estar vacío.")
    @Size(max = 255, message = "El título no puede tener más de 255 caracteres.")
    private String title;

    @NotBlank(message = "La descripción no puede estar vacía.")
    @Size(max = 500, message = "La descripción no puede tener más de 500 caracteres.")
    private String description;

    @NotNull(message = "Las horas estimadas son obligatorias.")
    @Min(value = 1, message = "Las horas estimadas deben ser al menos 1.")
    @Max(value = 1000, message = "Las horas estimadas no pueden ser más de 1000.")
    private Integer estimatedHours;
    
    @NotNull
    private String teacherId;
}
