package com.orbis.stream.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class SettingDto {
    private String streamUrl;

    private String streamKey;

    private String platformStreamName;
}
