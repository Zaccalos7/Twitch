package com.orbis.stream.mapping;

import com.orbis.stream.dto.SettingDto;
import com.orbis.stream.model.Setting;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface SettingMapper extends BaseMapper<SettingDto, Setting>{
}
