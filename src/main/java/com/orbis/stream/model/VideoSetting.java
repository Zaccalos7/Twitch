package com.orbis.stream.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class VideoSetting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer videoCodec;
    private Integer pixelFormat;
    private Integer videoBitrate;
    private Integer gopSize;
    private String videoFormat;

    @ElementCollection
    @CollectionTable(
            name = "video_settings_options",
            joinColumns = @JoinColumn(name = "video_setting_id")
    )
    private List<VideoSettingsOption> videoSettingsOptions;

    @OneToOne(cascade = CascadeType.ALL)
    private AudioSetting audioSetting;
}
