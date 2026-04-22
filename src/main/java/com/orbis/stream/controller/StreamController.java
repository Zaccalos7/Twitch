package com.orbis.stream.controller;

import com.orbis.stream.component.LoggerMessageComponent;
import com.orbis.stream.record.StartLiveRecord;
import com.orbis.stream.service.StreamService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/live/")
@Tag(name="Setting handle live start stop")
@RequiredArgsConstructor
public class StreamController {

    private final LoggerMessageComponent loggerMessageComponent;

    private final StreamService streamService;


    @PostMapping("/start-live")
    @Operation(summary = "Endpoint per lo start della live",
            description = "Restituisce l'esito dell'operazione")
    public ResponseEntity<Map<String,String>> startLive(@RequestBody StartLiveRecord startLiveRecord) {

        var response = streamService.startLive(startLiveRecord);
        log.info(loggerMessageComponent.printMessage("live.started"));
        return response;
    }

}
