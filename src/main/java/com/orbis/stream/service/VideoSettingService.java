package com.orbis.stream.service;

import com.orbis.stream.component.LoggerMessageComponent;
import com.orbis.stream.handler.ResponseHandler;
import com.orbis.stream.mapping.VideoSettingRecordMapper;
import com.orbis.stream.model.VideoSetting;
import com.orbis.stream.record.VideoSettingsRecord;
import com.orbis.stream.repository.VideoSettingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@RequiredArgsConstructor
@Service
@Slf4j
public class VideoSettingService {

    private final VideoSettingRecordMapper videoSettingRecordMapper;
    private final VideoSettingRepository videoSettingRepository;
    private final LoggerMessageComponent loggerMessageComponent;
    private final ResponseHandler responseHandler;

    public ResponseEntity<Map<String,String>> saveSettingsVideo(VideoSettingsRecord videoSettingsRecord){
        mappingAndSaveValue(videoSettingsRecord);
        log.info(loggerMessageComponent.printMessage("video.settings.saved"));

        return responseHandler.buildResponse("video.settings.saved", HttpStatus.CREATED);
    }

    @Transactional
    private void mappingAndSaveValue(VideoSettingsRecord videoSettingsRecord){
        VideoSetting videoSetting =  videoSettingRecordMapper.toModel(videoSettingsRecord);
        videoSettingRepository.save(videoSetting);
    }



}
