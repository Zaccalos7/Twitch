package com.orbis.stream.record;

public record VideoRecord(
        Integer videoCodec,
        Integer pixelFormat,
        Integer videoBitrate,
        Integer gopSize,
        String[] videoOptions,
        String videoFormat,
        AudioRecord audioRecord
) {
}
