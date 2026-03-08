package com.orbis.stream.mapping;

import com.orbis.stream.model.Setting;
import com.orbis.stream.record.SettingRecord;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface SettingRecordMapper extends BaseMapper<SettingRecord, Setting>{
}
