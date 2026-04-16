package com.orbis.stream.record;

import java.time.LocalDateTime;


public record VideoSettingsRecord(
        String title,
        Integer videoCodec,
        Integer pixelFormat,
        Integer videoBitrate,
        Integer gopSize,
        LocalDateTime lastModified,
        Boolean isVideoAndAudioSettingActive,
        VideoOptionRecord[] videoOptions,
        String videoFormat,
        AudioSettingsRecord audioSettingRecord
) {
}
