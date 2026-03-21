package com.orbis.stream.record;

public record VideoRecord(
        Integer videoCodec,
        Integer pixelFormat,
        Integer videoBitrate,
        Integer gopSize,
        VideoOptionRecord[] videoOptions,
        String videoFormat,
        AudioRecord audioRecord
) {
}
