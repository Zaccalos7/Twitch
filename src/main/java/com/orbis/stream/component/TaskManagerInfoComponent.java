package com.orbis.stream.component;

import com.orbis.stream.dto.SystemInfoDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.GlobalMemory;
import oshi.hardware.Sensors;


import java.util.List;


@RequiredArgsConstructor
@Component
public class TaskManagerInfoComponent {
    private static final SystemInfo sistemInfo = new SystemInfo();
    private static final int NOT_FOUND = -1;

    public List<SystemInfoDto> getAllSystemInfo() throws InterruptedException {
        return List.of(
                SystemInfoDto.builder()
                        .value(getCpuPercent())
                        .field("cpu")
                        .build(),
                SystemInfoDto.builder()
                        .field("ram")
                        .value(getRamPercent())
                        .build(),
                SystemInfoDto.builder()
                        .value(getSwapPercent())
                        .field("swap")
                        .build(),
                SystemInfoDto.builder()
                        .field("cpuTemp")
                        .value( getCpuTemp())
                        .build()
        );
    }

    public int getCpuPercent() throws InterruptedException {
        CentralProcessor cpu = sistemInfo.getHardware().getProcessor();
        long[] prevTicks = cpu.getSystemCpuLoadTicks();

        Thread.sleep(500);
        double cpuLoad = cpu.getSystemCpuLoadBetweenTicks(prevTicks) * 100;
        return  (int) cpuLoad;

    }

    public int getRamPercent(){
        GlobalMemory globalMemory = sistemInfo.getHardware().getMemory();

        long total = globalMemory.getTotal();
        long available = globalMemory.getAvailable();
        long used = total - available;

        return (int) ((used * 100.0) / total);
    }


    public int getSwapPercent() {
        GlobalMemory mem = sistemInfo.getHardware().getMemory();

        long total = mem.getVirtualMemory().getSwapTotal();
        long used = mem.getVirtualMemory().getSwapUsed();
        int swapPercent = (int) ((used * 100.0) / total);

        return total == 0 ? 0 : swapPercent;
    }


    public int getCpuTemp() {
        Sensors sensors = sistemInfo.getHardware().getSensors();
        double temp = sensors.getCpuTemperature();

        return Double.isNaN(temp) ? NOT_FOUND : (int) temp;
    }


}
