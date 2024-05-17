package com.aula.service;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class FileStorageService {
    @Value("${media.location}")
    private String mediaLocation;
    private Path rootLocation;

    @PostConstruct
    public void init() throws IOException {
        rootLocation = Paths.get(mediaLocation).toAbsolutePath().normalize();
        Files.createDirectories(rootLocation);
    }

    public String store(MultipartFile file, String subdirectory) throws IOException {
        if (file.isEmpty()) {
            throw new RuntimeException("Failed to store empty file.");
        }

        Path directory = rootLocation.resolve(subdirectory).normalize().toAbsolutePath();
        Files.createDirectories(directory); // Crea el subdirectorio si no existe
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String originalFilename = file.getOriginalFilename();
        String filename = timestamp + "-" + originalFilename;
        Path destinationFile = directory.resolve(Paths.get(filename))
                .normalize().toAbsolutePath();
        if (!destinationFile.getParent().equals(directory)) {
            throw new RuntimeException("Cannot store file outside current directory.");
        }
        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
        }
        return mediaLocation+ "/" + subdirectory + "/" + filename;
    }

    public Resource loadAsResource(String path) throws MalformedURLException {
        Path filePath = Path.of(path);
        Resource resource = new UrlResource(filePath.toUri());
        if (resource.exists() && resource.isReadable()) {
            return resource;
        } else {
            throw new RuntimeException();
        }
    }

    public void deleteFile(String path) {
        try {
            Files.deleteIfExists(Path.of(path));
        } catch (NoSuchFileException e) {
            throw new RuntimeException("File not found: " + path);
        } catch (IOException e) {
            throw new RuntimeException("Could not delete file: " + path, e);
        }
    }
}

