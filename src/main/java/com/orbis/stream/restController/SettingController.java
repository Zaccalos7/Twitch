package com.orbis.stream.restController;

import com.orbis.stream.component.LoggerMessageComponent;
import com.orbis.stream.dto.SettingDto;
import com.orbis.stream.record.SettingRecord;
import com.orbis.stream.service.SettingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/settings/")
@Tag(name="Setting for streaming")
@RequiredArgsConstructor
public class SettingController {

    private final SettingService settingService;
    private final LoggerMessageComponent loggerMessageComponent;

    @PostMapping("save")
    @Operation(summary = "Endpoint per il salvataggio delle impostazioni dello streaming, tutti i parametri sono required",
            description = "Restituisce l'esito dell'operazione")
    public ResponseEntity<Map<String, String>> saveSettings(@RequestBody SettingRecord settingRecord){
        var response = settingService.addNewConfiguration(settingRecord);
        log.info(loggerMessageComponent.printMessage("success.operations"));

        return response;
    }

    @PutMapping("change")
    @Operation(summary = "Endpoint per la modifica della configurazione di streaming, tutti i parametri sono required",
            description = "Restituisce l'esito dell'operazione")
    public ResponseEntity<Map<String, String>> changeSettings(@RequestParam Integer id){
        var response = settingService.modifySetting(id);

        log.info(loggerMessageComponent.printMessage("success.operations"));

        return response;
    }

    @GetMapping("retrive")
    @Operation(summary = "Endpoint per prendere tutte le configurazioni associate a quell'utente",
            description = "Restituisce la lista delle configurazioni dell'utente")
    public List<SettingDto> retriveSettings(){
        return null;
    }


    @DeleteMapping("delete")
    @Operation(summary = "Endpoint per la cancellazione della configurazione di streaming",
            description = "Restituisce l'esito dell'operazione")
    public ResponseEntity<Map<String, String>> deleteAStreamingSetting(@RequestParam Integer id){
        var response = settingService.deleteAStreamingSetting(id);

        log.info(loggerMessageComponent.printMessage("success.operations"));

        return response;
    }
}
