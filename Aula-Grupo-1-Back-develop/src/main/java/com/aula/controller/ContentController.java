package com.aula.controller;

import com.aula.dto.ResponseContentDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;

import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.aula.dto.RequestContentDTO;
import com.aula.service.ContentService;
import java.util.List;

@RestController
@RequestMapping("api/v1/")
@AllArgsConstructor
@CrossOrigin(origins= {"http://localhost:4200"})
public class ContentController {

    private ContentService contentService;

    @PostMapping(value = {"content"}, consumes = {MediaType.MULTIPART_FORM_DATA_VALUE} )
    public ResponseEntity<String> createContent(
    		@Valid @RequestPart("content") RequestContentDTO requestContentDTO, 
    		@NotNull @RequestPart("image") final MultipartFile image) {
        return contentService.createContent(requestContentDTO, image);
    }

    @GetMapping("contents")
    public ResponseEntity<List<ResponseContentDTO>> getAllContents() {
        return contentService.getAllContents();
    }
    
    @GetMapping("teacher/{teacherId}/contents")
    public ResponseEntity<?> getTeacherContents(@PathVariable String teacherId) {
        return contentService.findTeacherContents(teacherId);
    }

    @GetMapping("/searchContent")
    public ResponseEntity<?> searchContent(@RequestParam String filter){
        return contentService.searchContent(filter);
    }

    @GetMapping("content/{contentId}")
    public ResponseEntity<ResponseContentDTO> getContentById(@PathVariable Long contentId) {
        return contentService.getContentById(contentId);
    }

    @DeleteMapping("content/{contentId}")
    public ResponseEntity<String> deleteContent(@PathVariable Long contentId) {
        return contentService.deleteContent(contentId);
    }

    @PutMapping(value = {"content/{contentId}"}, consumes = {MediaType.MULTIPART_FORM_DATA_VALUE} )
    public ResponseEntity<String> updateContent(
    		@PathVariable Long contentId, 
    		@Valid @RequestPart("content") RequestContentDTO requestContentDTO,
    		@RequestPart("image") final MultipartFile image) {
        return contentService.updateContent(contentId, requestContentDTO, image);
    }

    @GetMapping("contents/image/{contentId}")
    public ResponseEntity<Resource> getFile(@PathVariable Long contentId) {
        return contentService.getFile(contentId);
    }

    @GetMapping("contents/course/{courseId}")
    public ResponseEntity<List<ResponseContentDTO>> getContentsByCourse(@PathVariable Long courseId) {
        return contentService.getContentsByCourseId(courseId);
    }
}

