package com.orbis.stream.enums;

import lombok.Getter;

@Getter
public enum AudioCodecEnum {

    AV_CODEC_ID_MP3(86017),
    AV_CODEC_ID_AAC(86018),
    AV_CODEC_ID_FLAC(86028),
    AV_CODEC_ID_VORBIS(86021),
    AV_CODEC_ID_OPUS(86076),
    AV_CODEC_ID_AC3(86019),
    AV_CODEC_ID_EAC3(86056),
    AV_CODEC_ID_DTS(86020),
    AV_CODEC_ID_ALAC(86032),

    AV_CODEC_ID_PCM_S16LE(65536),
    AV_CODEC_ID_PCM_S24LE(65548),
    AV_CODEC_ID_PCM_F32LE(65557);

    private final int value;

    AudioCodecEnum(int value) {
        this.value = value;
    }
}
