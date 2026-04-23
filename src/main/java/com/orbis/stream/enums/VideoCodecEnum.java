package com.orbis.stream.enums;

import lombok.Getter;

@Getter
public enum VideoCodecEnum {

    AV_CODEC_ID_H264(27),
    AV_CODEC_ID_HEVC(173),
    AV_CODEC_ID_MPEG4(12),
    AV_CODEC_ID_MPEG2VIDEO(2),
    AV_CODEC_ID_VP8(139),
    AV_CODEC_ID_VP9(167),
    AV_CODEC_ID_AV1(225),
    AV_CODEC_ID_THEORA(30),
    AV_CODEC_ID_WMV3(71),
    AV_CODEC_ID_FLV1(21);

    private final int value;

    VideoCodecEnum(int value) {
        this.value = value;
    }
}