package com.orbis.stream.model;

import com.orbis.stream.enums.LiveStatusEnum;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Video {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer pkid;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String videoPath;

    @Column(nullable = false)
    private String extension;

    @ColumnDefault("1")
    private LiveStatusEnum liveStatus;

    @ColumnDefault("0")
    private Long lastTimeStampBeforeStop;

    @ManyToOne
    private VideoLiveHistory videoLiveHistory;

    @ManyToOne
    private VideoSetting videoSetting;
}
