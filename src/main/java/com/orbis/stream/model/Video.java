package com.orbis.stream.model;

import jakarta.persistence.*;
import lombok.*;

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

    private String name;
    private String extension;

    private Integer durationInSeconds;

    @Column(columnDefinition = "TEXT")
    private String streamPlatform;

    @OneToOne
    private VideoLiveInformation videoLiveInformation;

}
