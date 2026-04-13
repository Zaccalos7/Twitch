package com.orbis.stream.mapping.mapperDTO;

import com.orbis.stream.dto.VideoSettingDto;
import com.orbis.stream.mapping.BaseMapper;
import com.orbis.stream.model.VideoSetting;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {VideoSettingOptionMapper.class, AudioSettingMapper.class})
public interface VideoSettingMapper extends BaseMapper<VideoSettingDto, VideoSetting> {
}
