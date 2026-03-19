package com.orbis.stream.record;

public record VideoRecord(
        int videoCodec,
        int pixelFormat,
        int videoBitrate,
        int gopSize,
        String[] videoOptions,
        String videoFormat,
        AudioRecord audioRecord
) {
}
