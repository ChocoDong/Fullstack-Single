package com.dcu.test.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileService {
    // 이미지 파일 저장
    public String imageSave(MultipartFile image) throws IOException {
        Files.createDirectories(Paths.get("./upload/images/"));
        String filename = UUID.randomUUID() + "_" + image.getOriginalFilename();
        Path filePath = Paths.get("./upload/images/" + filename);
        Files.copy(image.getInputStream(), filePath);
        return "/upload/images/" + filename;
    }

    // 파일 삭제
    public void fileDelete(String filePath) throws IOException {
        if (filePath.startsWith("/upload/images/")) {
            String fileName = filePath.substring("/upload/images/".length());
            Path path = Paths.get("./upload/images/" + fileName);
            Files.deleteIfExists(path);
        }
    }
}
