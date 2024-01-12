package com.mjc.school.controller;

import com.mjc.school.service.dto.author.AuthorDtoRequest;
import com.mjc.school.service.dto.author.AuthorDtoResponse;

public interface AuthorController extends BaseController<AuthorDtoRequest, AuthorDtoResponse, Long> {
}