package com.orbis.stream.service;

import com.orbis.stream.component.LoggerMessageComponent;
import com.orbis.stream.controller.filter.DynamicSpecificationBuilder;
import com.orbis.stream.dto.VideoDto;
import com.orbis.stream.mapping.mapperDTO.VideoMapper;
import com.orbis.stream.model.Video;
import com.orbis.stream.repository.VideoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;


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
        log.info(loggerMessageComponent.printMessage("recovered.video.page"));
        return videoDtoPage;
    }

    public Page<VideoDto> getAllVideoList(Map<String, String> filtersMap, Pageable pageable) {
        Page<VideoDto> videoList = getAllVideoByFilters(filtersMap, pageable);
        log.info(loggerMessageComponent.printMessage("recovered.video"));
        return videoList;
    }

    @Transactional(readOnly = true)
    private Page<VideoDto> getAllVideoByFilters(Map<String, String> filtersMap, Pageable pageable){
        Specification<Video> dynamicSpecification = DynamicSpecificationBuilder.buildSpecification(filtersMap);
        return videoRepository.findAll(dynamicSpecification, pageable)
                .map(videoMapper::toDto);
    }
}
