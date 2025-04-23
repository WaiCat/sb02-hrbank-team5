package com.hrbank.mapper;

import com.hrbank.dto.backup.BackupDto;
import com.hrbank.entity.Backup;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BackupMapper {

  @Mapping(source = "file.id", target = "fileId")
  BackupDto toDto(Backup backup);
}
