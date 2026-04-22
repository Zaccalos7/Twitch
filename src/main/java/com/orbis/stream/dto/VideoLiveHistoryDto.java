package com.orbis.stream.dto;



import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VideoLiveHistoryDto {
    private Long pkid;

    private String folderOfVideoToStream;

    private LocalDateTime localDateTimeStartLive;

    private String userName;

    private String streamUrl;

    private String streamKey;

    private String platformStreamName;
}
