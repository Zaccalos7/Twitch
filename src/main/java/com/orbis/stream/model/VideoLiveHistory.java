package com.orbis.stream.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.ZoneId;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class VideoLiveHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pkid;

    private ZoneId timeZone;

    private String message;

    private boolean success;

    @ManyToOne
    private Video video;
}
