package com.orbis.stream.controller;

import com.orbis.stream.component.LoggerMessageComponent;
import com.orbis.stream.service.StreamService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.FrameGrabber;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/live/")
@Tag(name="Setting handle live start stop")
@RequiredArgsConstructor
public class StreamController {

    private final LoggerMessageComponent loggerMessageComponent;

    private final StreamService streamService;

    @GetMapping("/start")
    public ResponseEntity<Map<String,String>> startLive() throws FrameGrabber.Exception, FFmpegFrameRecorder.Exception {
        var response = streamService.startLive();
        log.info("successo");
        return response;
    }
}
