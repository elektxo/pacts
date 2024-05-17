package com.aula.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseContentDTO {
    private Long id;
    private String title;
    private String description;
    private Integer estimatedHours;
    private String imagePath;
    private List<CommentResponse> comments;
}
