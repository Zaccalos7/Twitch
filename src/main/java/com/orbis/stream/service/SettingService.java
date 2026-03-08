package com.orbis.stream.service;

import com.orbis.stream.component.LoggerMessageComponent;
import com.orbis.stream.dto.SettingDto;
import com.orbis.stream.exceptions.DuplicationEntityException;
import com.orbis.stream.exceptions.NotFoundCustomException;
import com.orbis.stream.handler.ResponseHandler;
import com.orbis.stream.mapping.SettingMapper;
import com.orbis.stream.mapping.SettingRecordMapper;
import com.orbis.stream.model.Setting;
import com.orbis.stream.model.User;
import com.orbis.stream.record.RegisterRecord;
import com.orbis.stream.record.SettingRecord;
import com.orbis.stream.repository.SettingRepository;
import com.orbis.stream.repository.UserRepository;
import com.orbis.stream.restController.filter.DynamicSpecificationBuilder;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class SettingService {

    private final LoggerMessageComponent loggerMessageComponent;
    private final ResponseHandler responseHandler;

    private final SettingRepository settingRepository;
    private final UserRepository userRepository;

    private final SettingMapper settingMapper;
    private final SettingRecordMapper settingRecordMapper;

    //for now user is only mario
    private static final String nickName = "Mario";

    public ResponseEntity<Map<String, String>> addNewConfiguration(SettingRecord settingRecord){
        ResponseEntity<Map<String, String>>  mapResponseEntity;

        saveSettings(settingRecord);

        mapResponseEntity = responseHandler
                .buildResponse("setting.created", HttpStatus.CREATED);

        return mapResponseEntity;
    }

    @Transactional
    private void saveSettings(SettingRecord settingRecord){
        String streamKey = settingRecord.streamKey();
        String streamUrl = settingRecord.streamUrl();

        checkUniqueConstrain(streamKey, streamUrl);

        Setting setting = settingRecordMapper.toModel(settingRecord);

        //@TODO insert login and pass the real user
        User user = userRepository
                .findByNickName(nickName)
                .orElseThrow(()-> {
                    log.error(loggerMessageComponent.printMessage("Utente Mario non trovato"));
                    return new EntityNotFoundException("Utente Mario non trovato");
                });

        setting.setUser(user);
        settingRepository.save(setting);

    }


    private void checkUniqueConstrain(String streamKey, String streamUrl) {
        Optional<Setting> setting = settingRepository.findByStreamUrlAndStreamKey(streamUrl, streamKey);

        if(setting.isEmpty()){
            return;
        }
        log.error("setting.is.already.present");
        throw new DuplicationEntityException("setting.is.already.present");
    }

    public List<SettingDto> retrieveSettings(Map<String, String> filtersMap) {

        return retrieveSettingsWithFiltersOrNot(filtersMap);
    }

    @Transactional(readOnly = true)
    public List<SettingDto> retrieveSettingsWithFiltersOrNot(Map<String, String> filtersMap){

        Specification<Setting> dynamicFilteringSpecification =
                DynamicSpecificationBuilder.buildSpecification(filtersMap);

        return settingRepository.findAll(dynamicFilteringSpecification)
                .stream()
                .map(settingMapper::toDto)
                .toList();
    }

    public ResponseEntity<Map<String, String>> modifySetting(Integer id, SettingRecord settingRecord) {
        ResponseEntity<Map<String, String>> response;

        checkIfExistAndUpdateIt(id, settingRecord);

        response = responseHandler.buildResponse("setting.update",HttpStatus.ACCEPTED);

        return response;
    }

    @Transactional
    private void checkIfExistAndUpdateIt(Integer id, SettingRecord settingRecord){
        Setting setting = settingRepository
                .findById(id)
                .orElseThrow(() -> {
                    log.error(loggerMessageComponent.printMessage("setting.not.found"));
                    return new NotFoundCustomException("setting.not.found");
                });
        settingRecordMapper.updateSettingFromSettingRecord(settingRecord, setting);

        settingRepository.save(setting);
    }

    public ResponseEntity<Map<String,String>> deleteAStreamingSetting(Integer id) {
        ResponseEntity<Map<String, String>> mapResponseEntity = responseHandler.buildResponse("setting.delete",HttpStatus.OK);

        checkIfSettingExistAndThenDeleteIt(id);

        return mapResponseEntity;
    }


    @Transactional
    private void checkIfSettingExistAndThenDeleteIt(Integer id) {
        Setting setting = settingRepository
                .findById(id)
                .orElseThrow(() -> {
                    log.error(loggerMessageComponent.printMessage("setting.not.found"));
                    return new NotFoundCustomException("setting.not.found");
                });

        settingRepository.delete(setting);
    }

}
