package com.orbis.stream.controller;

import com.orbis.stream.component.ImageComponent;
import com.orbis.stream.component.LoggerMessageComponent;
import com.orbis.stream.handler.ResponseHandler;
import com.orbis.stream.utilities.ImageUtilities;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/image/")
public class ImageController {



    private final ImageComponent imageComponent;
    private final ResponseHandler responseHandler;
    private final LoggerMessageComponent loggerMessageComponent;

    @PostMapping("upload")
    public ResponseEntity<Map<String, String>> uploadImage(@RequestParam("image") @NotNull(message = "input.not.valid") MultipartFile file) {
        var response = imageComponent.saveImage(file);
        log.info(loggerMessageComponent.printMessage("image.saved"));

        return response;
    }


    @GetMapping("/loadimage")
    public ResponseEntity<Resource> getImage() {

        ImageUtilities imageUtilities = imageComponent.loadImage();

        log.info(loggerMessageComponent.printMessage("image.retrive"));

        return ResponseEntity.ok()
                .header("Content-Type", imageUtilities.getContentType())
                .body(imageUtilities.getResource());
    }

}

