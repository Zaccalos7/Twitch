package com.orbis.stream.model;

import jakarta.persistence.*;

import lombok.*;

import java.time.LocalDateTime;
import java.time.ZoneId;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
public class VideoLiveHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pkid;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String folderOfVideoToStream;

    @Column(nullable = false)
    private LocalDateTime localDateTimeStartLive;

    private String userName;
}
