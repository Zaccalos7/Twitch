package com.orbis.stream.controller;

import com.orbis.stream.component.LoggerMessageComponent;
import com.orbis.stream.record.VideoSettingsRecord;
import com.orbis.stream.service.VideoSettingService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/video-setting/")
@Slf4j
public class VideoSettingController {


    private final LoggerMessageComponent loggerMessageComponent;
    private final VideoSettingService videoSettingService;

    @PostMapping("save")
    @Operation(summary = "creazione della configurazione del video")
    public ResponseEntity<Map<String,String>> saveVideoSettings(@RequestBody VideoSettingsRecord videoSettingsRecord){
        return videoSettingService.saveSettingsVideo(videoSettingsRecord);
    }
}
