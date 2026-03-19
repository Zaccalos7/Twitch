package com.orbis.stream.model;

import com.orbis.stream.enums.LiveStatusEnum;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LiveVideoInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer pkid;

    private LiveStatusEnum liveStatus;

    private Long timestampOfLastFrameBeforeStopped;

}
