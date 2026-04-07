package com.orbis.stream.record;

import java.time.LocalDateTime;


public record VideoSettingsRecord(
        Integer videoCodec,
        Integer pixelFormat,
        Integer videoBitrate,
        Integer gopSize,
        LocalDateTime lastModified,

                VideoOptionRecord[] videoOptions,
        String videoFormat,
        AudioSettingsRecord audioSettingRecord
) {
}
