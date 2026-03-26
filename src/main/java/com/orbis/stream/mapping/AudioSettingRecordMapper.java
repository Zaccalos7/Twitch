package com.orbis.stream.mapping;

import com.orbis.stream.model.AudioSetting;
import com.orbis.stream.record.AudioSettingsRecord;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AudioSettingRecordMapper extends BaseMapper<AudioSettingsRecord, AudioSetting>{
}
