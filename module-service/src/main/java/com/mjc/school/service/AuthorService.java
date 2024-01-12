package com.mjc.school.service;

import com.mjc.school.service.dto.author.AuthorDtoRequest;
import com.mjc.school.service.dto.author.AuthorDtoResponse;

import java.util.Optional;

public interface AuthorService extends BaseService<AuthorDtoRequest, AuthorDtoResponse, Long> {

    Optional<AuthorDtoResponse> readByNewsId(Long id);
}