package com.orbis.stream.service;

import com.orbis.stream.component.LoggerMessageComponent;
import com.orbis.stream.dto.SettingDto;
import com.orbis.stream.exceptions.DuplicationEntityException;
import com.orbis.stream.handler.ResponseHandler;
import com.orbis.stream.mapping.SettingMapper;
import com.orbis.stream.model.Setting;
import com.orbis.stream.model.User;
import com.orbis.stream.repository.SettingRepository;
import com.orbis.stream.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

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

    //for now user is only mario
    private static final String nickName = "Mario";

    public ResponseEntity<Map<String, String>> addNewConfiguration(SettingDto settingDto){
        ResponseEntity<Map<String, String>>  mapResponseEntity = responseHandler
                .buildResponse("success.operations", HttpStatus.CREATED);

        saveSettings(settingDto);

        return mapResponseEntity;
    }

    @Transactional
    private void saveSettings(SettingDto settingDto){
        String streamKey = settingDto.getStreamKey();
        String streamUrl = settingDto.getStreamUrl();

        checkUniqueConstrain(streamKey, streamUrl);

        Setting setting = settingMapper.toModel(settingDto);

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

    public ResponseEntity<Map<String, String>> modifySetting(SettingDto settingDto) {
        return null;
    }
}
