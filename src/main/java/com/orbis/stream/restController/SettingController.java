package com.orbis.stream.restController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/settings/")
@Tag(name="Setting for streaming")
public class SettingController {

    @PostMapping("save")
    @Operation(summary = "Endpoint per il salvataggio delle impostazioni dello streaming, tutti i parametri sono required",
            description = "Restituisce l'esito dell'operazione")
    public ResponseEntity<Map<String, String>> saveSettings(){
        return null;
    }

    @PutMapping("change")
    @Operation(summary = "Endpoint per la maodifica della configurazione di streaming, tutti i parametri sono required",
            description = "Restituisce l'esito dell'operazione")
    public ResponseEntity<Map<String, String>> changeSettings(){
        return null;
    }

    @GetMapping("retrive")
    @Operation(summary = "Endpoint per prendere tutte le configurazioni associate a quell'utente",
            description = "Restituisce la lista delle configurazioni dell'utente")
    public ResponseEntity<Map<String, String>> retriveSettings(){
        return null;
    }
}
