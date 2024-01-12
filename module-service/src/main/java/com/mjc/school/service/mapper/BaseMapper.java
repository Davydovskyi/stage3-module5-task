package com.mjc.school.service.mapper;

import java.util.List;

public interface BaseMapper<F, M, T> {

    List<T> modelListToDtoList(List<M> models);

    T modelToDto(M model);

    M dtoToModel(F dtoRequest);
}