package com.mjc.school.controller.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import com.mjc.school.controller.TagController;
import com.mjc.school.service.TagService;
import com.mjc.school.service.dto.tag.TagDtoRequest;
import com.mjc.school.service.dto.tag.TagDtoResponse;
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
@RequestMapping("/api/v1/tags")
@RequiredArgsConstructor
@Tag(name = "Tags", description = "Operations for creating, updating, retrieving and deleting tag in the application")
public class TagRestController implements TagController {
    private final TagService tagService;
    private final ObjectMapper objectMapper;

    @Override
    @GetMapping
    @Operation(summary = "View all tags")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved all tags"),
            @ApiResponse(responseCode = "401", description = "You are not authorized to view the resource"),
            @ApiResponse(responseCode = "403", description = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(responseCode = "500", description = "Application failed to process the request")}
    )
    public List<TagDtoResponse> readAll(Pageable pageable) {
        return tagService.readAll(pageable);
    }

    @Override
    @GetMapping("/{id}")
    @Operation(summary = "Retrieve specific tag with the supplied id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the tag with the supplied id"),
            @ApiResponse(responseCode = "401", description = "You are not authorized to view the resource"),
            @ApiResponse(responseCode = "403", description = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(responseCode = "404", description = "The resource you were trying to reach is not found"),
            @ApiResponse(responseCode = "500", description = "Application failed to process the request")}
    )
    public TagDtoResponse readById(@PathVariable Long id) {
        return tagService.readById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @Override
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a tag")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully created a tag"),
            @ApiResponse(responseCode = "400", description = "The request parameters are invalid"),
            @ApiResponse(responseCode = "401", description = "You are not authorized to view the resource"),
            @ApiResponse(responseCode = "403", description = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(responseCode = "500", description = "Application failed to process the request")}
    )
    public TagDtoResponse create(@RequestBody @Validated TagDtoRequest createRequest) {
        return tagService.create(createRequest);
    }

    @Override
    @PutMapping("/{id}")
    @Operation(summary = "Update tag information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated tag information"),
            @ApiResponse(responseCode = "400", description = "The request parameters are invalid"),
            @ApiResponse(responseCode = "401", description = "You are not authorized to view the resource"),
            @ApiResponse(responseCode = "403", description = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(responseCode = "404", description = "The resource you were trying to reach is not found"),
            @ApiResponse(responseCode = "500", description = "Application failed to process the request")}
    )
    public TagDtoResponse update(@PathVariable Long id,
                                 @RequestBody @Validated TagDtoRequest updateRequest) {
        return tagService.update(id, updateRequest)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @Override
    @PatchMapping(path = "/{id}", consumes = "application/json-patch+json")
    @Operation(summary = "Update tag information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated tag information"),
            @ApiResponse(responseCode = "401", description = "You are not authorized to view the resource"),
            @ApiResponse(responseCode = "403", description = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(responseCode = "404", description = "The resource you were trying to reach is not found"),
            @ApiResponse(responseCode = "500", description = "Application failed to process the request")}
    )
    public TagDtoResponse patch(@PathVariable Long id, @RequestBody JsonPatch patch) {
        return tagService.readById(id)
                .map(tagDtoResponse -> {
                    TagDtoRequest tagDtoRequest = applyPatch(patch, tagDtoResponse);
                    return tagService.patch(id, tagDtoRequest);
                })
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @Override
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Deletes specific tag with the supplied id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Successfully deletes the specific tag"),
            @ApiResponse(responseCode = "401", description = "You are not authorized to view the resource"),
            @ApiResponse(responseCode = "403", description = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(responseCode = "404", description = "The resource you were trying to reach is not found"),
            @ApiResponse(responseCode = "500", description = "Application failed to process the request")}
    )
    public void deleteById(@PathVariable Long id) {
        tagService.deleteById(id);
    }

    @SneakyThrows
    private TagDtoRequest applyPatch(JsonPatch patch, TagDtoResponse dto) {
        JsonNode patched = patch.apply(objectMapper.convertValue(dto, JsonNode.class));
        return objectMapper.treeToValue(patched, TagDtoRequest.class);
    }
}