package com.orbis.stream.record;

import com.orbis.stream.enums.LiveStatusEnum;
import lombok.Builder;

@Builder
public record VideoRecord(
        Integer pkid,
        String name,
        String videoPath,
        String extension,
        LiveStatusEnum liveStatus,
        Long lastTimeStampBeforeStop,
        String message,
        VideoLiveHistoryRecord videoLiveHistory,
        VideoSettingsRecord videoSetting
) {
}
