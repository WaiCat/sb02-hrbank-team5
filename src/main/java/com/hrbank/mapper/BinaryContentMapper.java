package com.hrbank.mapper;

import com.hrbank.dto.binarycontent.BinaryContentDto;
import com.hrbank.entity.BinaryContent;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BinaryContentMapper {
  BinaryContentDto toDto(BinaryContent binaryContent);
  BinaryContent toEntity(BinaryContentDto dto);
}
