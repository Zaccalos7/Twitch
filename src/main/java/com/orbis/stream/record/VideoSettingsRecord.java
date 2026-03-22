package com.orbis.stream.record;

public record VideoSettingsRecord(
        Integer videoCodec,
        Integer pixelFormat,
        Integer videoBitrate,
        Integer gopSize,
        VideoOptionRecord[] videoOptions,
        String videoFormat,
        AudioRecord audioRecord
) {
}
