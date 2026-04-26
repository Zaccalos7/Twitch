package com.orbis.stream.service;

import com.orbis.stream.component.LoggerMessageComponent;
import com.orbis.stream.dto.VideoDto;
import com.orbis.stream.mapping.mapperDTO.VideoMapper;
import com.orbis.stream.repository.VideoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Slf4j
public class VideoService {

    private final VideoRepository videoRepository;
    private final VideoMapper videoMapper;

    private final LoggerMessageComponent loggerMessageComponent;

    @Transactional(readOnly = true)
    public Page<VideoDto> getVideoList (Pageable pageable){
        Page<VideoDto> videoDtoPage = videoRepository
                .findAll(pageable)
                .map(videoMapper::toDto);
        log.info(loggerMessageComponent.printMessage("retried.video.page"));
        return videoDtoPage;
    }
}
