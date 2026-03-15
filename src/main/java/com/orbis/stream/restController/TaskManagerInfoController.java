package com.orbis.stream.restController;

import com.orbis.stream.component.LoggerMessageComponent;
import com.orbis.stream.component.TaskManagerInfoComponent;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/taskManager/statistics/")
@Tag(name="Info for system os")
@RequiredArgsConstructor
public class TaskManagerInfoController {

    private final TaskManagerInfoComponent taskManagerInfoComponent;
    private final LoggerMessageComponent loggerMessageComponent;

    @GetMapping("system/info")
    @Operation(summary = "Endpoint per tutte le statistiche del sistema os",
            description = "Restituisce le percentuali di utilizzo")
    public List<Integer> getAllSystemInfo() throws InterruptedException {
        List<Integer> responseList;
        responseList = taskManagerInfoComponent.getAllSystemInfo();
        log.info("get.all.system.info");
        return responseList;
    }

    @GetMapping("cpu")
    @Operation(summary = "Endpoint per le statistiche delle cpu del sistema os",
            description = "Restituisce la percentuale di utilizzo")
    public int getCpuUsage() throws InterruptedException {
        int cpuPercent = taskManagerInfoComponent.getCpuPercent();
        log.info(loggerMessageComponent.printMessage("get.cpu.info"));
        return cpuPercent;
    }

    @GetMapping("ram")
    @Operation(summary = "Endpoint per le statistiche della RAM del sistema os",
            description = "Restituisce la percentuale di utilizzo")
    public int getRamUsage() {
        int ramPercente = taskManagerInfoComponent.getRamPercent();
        log.info(loggerMessageComponent.printMessage("get.ram.info"));
        return ramPercente;
    }

    @GetMapping("swap")
    @Operation(summary = "Endpoint per le statistiche della SWAP del sistema os",
            description = "Restituisce la percentuale di utilizzo")
    public int getSwapPercent() {
        int swapPercente = taskManagerInfoComponent.getSwapPercent();
        log.info(loggerMessageComponent.printMessage("get.swap.info"));
        return swapPercente;
    }

    @GetMapping("cpu/temperature")
    @Operation(summary = "Endpoint per le statistiche della temperatura della cpu del sistema os se disponibile",
            description = "Restituisce la percentuale di utilizzo")
    public int getCpuTemp() {
        int valueOfCpuTemperature = taskManagerInfoComponent.getCpuTemp();

        if (valueOfCpuTemperature == 0) {
            log.warn(loggerMessageComponent.printMessage("cpu.temp.not.available"));
        } else {
            log.info(loggerMessageComponent.printMessage("get.cpu.temp.info"));
        }

        return valueOfCpuTemperature;
    }

}
