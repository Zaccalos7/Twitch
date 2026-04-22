package com.orbis.stream.configuration.beans;

import com.orbis.stream.configuration.VideoSettingsStreamingConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class VideoSettingsStreamingBean {

    @Bean
    public VideoSettingsStreamingConfiguration createVideoSettingsStreaming(){
        return new VideoSettingsStreamingConfiguration();
    }
}
