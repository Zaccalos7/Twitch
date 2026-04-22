package com.orbis.stream.configuration;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Getter
@Configuration
@PropertySource("video.settings.streaming.properties")
public class VideoSettingsStreamingConfiguration {

    @Value("${stream.recorder.format}")
    private String format;

    @Value("${stream.recorder.video.codec}")
    private int videoCodec;

    @Value("${stream.recorder.audio.codec}")
    private int audioCodec;

    @Value("${stream.recorder.pixel-format}")
    private int pixelFormat;

    @Value("${stream.recorder.frame-rate}")
    private int frameRate;

    @Value("${stream.recorder.video.bitrate}")
    private int videoBitrate;

    @Value("${stream.recorder.audio.bitrate}")
    private int audioBitrate;

    @Value("${stream.recorder.gop-size}")
    private int gopSize;

    @Value("${stream.recorder.option.preset}")
    private String presetKey;

    @Value("${stream.recorder.option.preset.value}")
    private String presetValue;

    @Value("${stream.recorder.option.tune}")
    private String tuneKey;

    @Value("${stream.recorder.option.tune.value}")
    private String tuneValue;

}
