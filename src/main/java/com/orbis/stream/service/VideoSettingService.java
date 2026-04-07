package com.orbis.stream.service;

import com.orbis.stream.component.LoggerMessageComponent;
import com.orbis.stream.dto.VideoSettingDto;
import com.orbis.stream.exceptions.NotFoundCustomException;
import com.orbis.stream.handler.ResponseHandler;
import com.orbis.stream.mapping.mapperDTO.VideoSettingMapper;
import com.orbis.stream.mapping.mapperRECORD.VideoSettingRecordMapper;
import com.orbis.stream.model.VideoSetting;
import com.orbis.stream.record.VideoSettingsRecord;
import com.orbis.stream.repository.VideoSettingRepository;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
@Slf4j
public class VideoSettingService {

    private final VideoSettingRecordMapper videoSettingRecordMapper;
    private final VideoSettingMapper videoSettingMapper;

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
        LocalDateTime localDate = LocalDateTime.now();
        videoSetting.setLastModified(localDate);
        videoSettingRepository.save(videoSetting);
    }


    public List<VideoSettingDto> getAllVideoSettings() {
        List<VideoSetting> videoSettingList = retrivesVideoSettings();
        return videoSettingList.stream()
                .map(videoSettingMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    private List<VideoSetting> retrivesVideoSettings() {
        return videoSettingRepository.findAllByOrderByLastModifiedDesc();
    }

    public ResponseEntity<Map<String,String>> editSettingsVideo(VideoSettingDto videoSettingDto, Integer id){
        editVideoSetting(videoSettingDto, id);
        log.info(loggerMessageComponent.printMessage("video.settings.modified"));

        return responseHandler.buildResponse("video.settings.modified", HttpStatus.CREATED);
    }

    @Transactional
    private void editVideoSetting(VideoSettingDto videoSettingDto, Integer id) {
        checkIfVideoSettingExist(id);
        VideoSetting videoSetting = videoSettingMapper.toModel(videoSettingDto);
        LocalDateTime localDate = LocalDateTime.now();
        videoSetting.setLastModified(localDate);
        videoSettingRepository.save(videoSetting);
    }

    private void checkIfVideoSettingExist(Integer id){
        videoSettingRepository.findById(id).orElseThrow(()->{
            log.error(loggerMessageComponent.printMessage("video.settings.not.found"));
            return new NotFoundCustomException("video.settings.not.found");
        });
    }

    public ResponseEntity<Map<String, String>> deleteVideoSetting(Integer id) {
        deleteById(id);
        log.info(loggerMessageComponent.printMessage("delete.successful"));
        return responseHandler.buildResponse("delete.successful", HttpStatus.OK);
    }

    private void deleteById(Integer id){
        videoSettingRepository.deleteById(id);
    }
}
