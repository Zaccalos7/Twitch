package com.orbis.stream.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

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
    private ZoneId startLiveDate;

    private String user;
}
