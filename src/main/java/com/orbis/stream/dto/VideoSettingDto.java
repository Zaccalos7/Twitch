package com.orbis.stream.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VideoSettingDto {
    private Integer id;

    private Integer videoCodec;
    private Integer pixelFormat;
    private Integer videoBitrate;
    private Integer gopSize;
    private String videoFormat;


    private List<VideoSettingsOptionDto> videoSettingsOptions;


    private AudioSettingDto audioSetting;
}
