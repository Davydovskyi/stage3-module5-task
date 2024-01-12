package com.mjc.school.controller.handler;

import lombok.Builder;
import org.springframework.http.HttpStatus;

import java.util.Map;

@Builder
public record ApiError(HttpStatus status,
                       int code,
                       String message,
                       Map<String, String> errors,
                       String instance) {
}