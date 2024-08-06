package io.project.mapper;

import io.project.dto.TranslationDTO;
import io.project.model.Translation;
import io.project.repository.TranslationRepository;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;


@Mapper(
        uses = {JsonNullableMapper.class, ReferenceMapper.class},
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public abstract class TranslationMapper {

    @Autowired
    private TranslationRepository translationRepository;

    public abstract TranslationDTO map(Translation translation);
}
