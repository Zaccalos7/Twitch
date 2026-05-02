package com.orbis.stream.controller;

import com.orbis.stream.dto.VideoDto;
import com.orbis.stream.record.VideoRecord;
import com.orbis.stream.service.VideoService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


import java.util.Map;

@RestController
@RequestMapping("/video/")
@RequiredArgsConstructor
public class VideoController {

    private final VideoService videoService;

    @Operation(summary = "Endpoint per recuperare la lista dello storico dei video",
            description = "Restituisce la pagina richiesta")
    @GetMapping("getPage")
    public Page<VideoDto> getVideoList(@PageableDefault(size = 20, sort = "pkid", direction = Sort.Direction.DESC) Pageable pageable){
        return videoService.getVideoList(pageable);
    }

    @Operation(summary = "Restituisce tutti i video, oppure filtrati")
    @GetMapping("getAllVideo")
    public Page<VideoRecord> getAllVideoList(@RequestParam  Map<String, String> filtersMap,
                                             @PageableDefault(size = 4, sort = "startDateLive", direction = Sort.Direction.DESC)
                                          Pageable pageable){
        filtersMap.remove("page");
        filtersMap.remove("size");
        return videoService.getAllVideoList(filtersMap, pageable);
    }
}
