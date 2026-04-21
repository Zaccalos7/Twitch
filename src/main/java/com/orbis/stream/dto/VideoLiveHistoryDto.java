package com.orbis.stream.dto;



import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZoneId;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VideoLiveHistoryDto {
    private Long pkid;

    private String folderOfVideoToStream;

    private ZoneId startLiveDate;

    private String user;
}
