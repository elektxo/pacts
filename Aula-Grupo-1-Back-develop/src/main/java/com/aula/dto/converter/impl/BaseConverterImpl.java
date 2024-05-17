package com.aula.dto.converter.impl;

import com.aula.dto.BaseDTO;
import com.aula.dto.converter.BaseConverter;
import com.aula.entity.BaseEntity;

public abstract class BaseConverterImpl<DTO extends BaseDTO, E extends BaseEntity> implements BaseConverter<DTO, E> {

	@Override
	public E dtoToEntity(DTO dto) {
		return null;
	}
	
	@Override
	public E dtoToEntity(DTO dto, E entity) {
		return null;
	}

	@Override
	public DTO entityToDTO(E entity) {
		return null;
	}

}
