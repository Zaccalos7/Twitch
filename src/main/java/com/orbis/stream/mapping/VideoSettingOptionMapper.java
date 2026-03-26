package com.orbis.stream.mapping;

import com.orbis.stream.model.VideoSettingsOption;
import com.orbis.stream.record.VideoOptionRecord;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface VideoSettingOptionMapper extends BaseMapper<VideoOptionRecord,VideoSettingsOption>{
}
