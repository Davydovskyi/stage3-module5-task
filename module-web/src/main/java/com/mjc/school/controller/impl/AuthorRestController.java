package com.mjc.school.controller.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import com.mjc.school.controller.AuthorController;
import com.mjc.school.service.AuthorService;
import com.mjc.school.service.dto.author.AuthorDtoRequest;
import com.mjc.school.service.dto.author.AuthorDtoResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/authors")
@Tag(name = "Authors", description = "Operations for creating, updating, retrieving and deleting author in the application")
public class AuthorRestController implements AuthorController {
    private final AuthorService authorService;
    private final ObjectMapper objectMapper;

    @Override
    @GetMapping()
    @Operation(summary = "View all authors")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved all authors"),
            @ApiResponse(responseCode = "401", description = "You are not authorized to view the resource"),
            @ApiResponse(responseCode = "403", description = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(responseCode = "500", description = "Application failed to process the request")}
    )
    public List<AuthorDtoResponse> readAll(Pageable pageable) {
        return authorService.readAll(pageable);
    }

    @GetMapping("/{id}")
    @Override
    @Operation(summary = "Retrieve specific author with the supplied id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the author with the supplied id"),
            @ApiResponse(responseCode = "401", description = "You are not authorized to view the resource"),
            @ApiResponse(responseCode = "403", description = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(responseCode = "404", description = "The resource you were trying to reach is not found"),
            @ApiResponse(responseCode = "500", description = "Application failed to process the request")}
    )
    public AuthorDtoResponse readById(@PathVariable Long id) {
        return authorService.readById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @Override
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    @Operation(summary = "Create an author")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully created an author"),
            @ApiResponse(responseCode = "400", description = "The request parameters are invalid"),
            @ApiResponse(responseCode = "401", description = "You are not authorized to view the resource"),
            @ApiResponse(responseCode = "403", description = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(responseCode = "500", description = "Application failed to process the request")}
    )
    public AuthorDtoResponse create(@RequestBody @Validated AuthorDtoRequest createRequest) {
        return authorService.create(createRequest);
    }

    @Override
    @PatchMapping(path = "/{id}", consumes = "application/json-patch+json")
    @Operation(summary = "Update author information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated author information"),
            @ApiResponse(responseCode = "401", description = "You are not authorized to view the resource"),
            @ApiResponse(responseCode = "403", description = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(responseCode = "404", description = "The resource you were trying to reach is not found"),
            @ApiResponse(responseCode = "500", description = "Application failed to process the request")}
    )
    public AuthorDtoResponse patch(@PathVariable Long id, @RequestBody JsonPatch patch) {
        return authorService.readById(id)
                .map(dto -> {
                    AuthorDtoRequest authorDtoRequest = applyPatch(patch, dto);
                    return authorService.patch(id, authorDtoRequest);
                })
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @Override
    @PutMapping("/{id}")
    @Operation(summary = "Update author information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated author information"),
            @ApiResponse(responseCode = "400", description = "The request parameters are invalid"),
            @ApiResponse(responseCode = "401", description = "You are not authorized to view the resource"),
            @ApiResponse(responseCode = "403", description = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(responseCode = "404", description = "The resource you were trying to reach is not found"),
            @ApiResponse(responseCode = "500", description = "Application failed to process the request")}
    )
    public AuthorDtoResponse update(@PathVariable Long id, @RequestBody @Validated AuthorDtoRequest updateRequest) {
        return authorService.update(id, updateRequest)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @Override
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Deletes specific author with the supplied id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Successfully deletes the specific author"),
            @ApiResponse(responseCode = "401", description = "You are not authorized to view the resource"),
            @ApiResponse(responseCode = "403", description = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(responseCode = "404", description = "The resource you were trying to reach is not found"),
            @ApiResponse(responseCode = "500", description = "Application failed to process the request")}
    )
    public void deleteById(@PathVariable Long id) {
        authorService.deleteById(id);
    }

    @SneakyThrows
    private AuthorDtoRequest applyPatch(JsonPatch patch, AuthorDtoResponse dto) {
        JsonNode patched = patch.apply(objectMapper.convertValue(dto, JsonNode.class));
        return objectMapper.treeToValue(patched, AuthorDtoRequest.class);
    }
}