package com.mjc.school.controller.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import com.mjc.school.controller.CommentController;
import com.mjc.school.service.CommentService;
import com.mjc.school.service.dto.comment.CommentDtoRequest;
import com.mjc.school.service.dto.comment.CommentDtoResponse;
import com.mjc.school.service.validator.group.CreateAction;
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

import javax.validation.groups.Default;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/comments")
@Tag(name = "Comments", description = "Operations for creating, updating, retrieving and deleting comment in the application")
public class CommentRestController implements CommentController {

    private final CommentService commentService;
    private final ObjectMapper objectMapper;

    @GetMapping
    @Override
    @Operation(summary = "View all comments")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved all comments"),
            @ApiResponse(responseCode = "401", description = "You are not authorized to view the resource"),
            @ApiResponse(responseCode = "403", description = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(responseCode = "500", description = "Application failed to process the request")}
    )
    public List<CommentDtoResponse> readAll(Pageable pageable) {
        return commentService.readAll(pageable);
    }

    @GetMapping("/{id}")
    @Override
    @Operation(summary = "Retrieve specific comment with the supplied id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the comment with the supplied id"),
            @ApiResponse(responseCode = "401", description = "You are not authorized to view the resource"),
            @ApiResponse(responseCode = "403", description = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(responseCode = "404", description = "The resource you were trying to reach is not found"),
            @ApiResponse(responseCode = "500", description = "Application failed to process the request")}
    )
    public CommentDtoResponse readById(@PathVariable Long id) {
        return commentService.readById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Override
    @Operation(summary = "Create a comment")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully created a comment"),
            @ApiResponse(responseCode = "400", description = "The request parameters are invalid"),
            @ApiResponse(responseCode = "401", description = "You are not authorized to view the resource"),
            @ApiResponse(responseCode = "403", description = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(responseCode = "500", description = "Application failed to process the request")}
    )
    public CommentDtoResponse create(@RequestBody @Validated({CreateAction.class, Default.class}) CommentDtoRequest createRequest) {
        return commentService.create(createRequest);
    }

    @Override
    @PutMapping("/{id}")
    @Operation(summary = "Update comment information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated comment information"),
            @ApiResponse(responseCode = "400", description = "The request parameters are invalid"),
            @ApiResponse(responseCode = "401", description = "You are not authorized to view the resource"),
            @ApiResponse(responseCode = "403", description = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(responseCode = "404", description = "The resource you were trying to reach is not found"),
            @ApiResponse(responseCode = "500", description = "Application failed to process the request")}
    )
    public CommentDtoResponse update(@PathVariable Long id,
                                     @RequestBody @Validated CommentDtoRequest updateRequest) {
        return commentService.update(id, updateRequest)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @PatchMapping(path = "/{id}", consumes = "application/json-patch+json")
    @Override
    @Operation(summary = "Update comment specific information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated comment information"),
            @ApiResponse(responseCode = "401", description = "You are not authorized to view the resource"),
            @ApiResponse(responseCode = "403", description = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(responseCode = "404", description = "The resource you were trying to reach is not found"),
            @ApiResponse(responseCode = "500", description = "Application failed to process the request")}
    )
    public CommentDtoResponse patch(@PathVariable Long id,
                                    @RequestBody JsonPatch patch) {
        return commentService.readById(id)
                .map(comment -> {
                    CommentDtoRequest commentDtoRequest = applyPatch(patch, comment);
                    return commentService.patch(id, commentDtoRequest);
                })
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @Override
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Deletes specific comment with the supplied id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Successfully deletes the specific comment"),
            @ApiResponse(responseCode = "401", description = "You are not authorized to view the resource"),
            @ApiResponse(responseCode = "403", description = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(responseCode = "404", description = "The resource you were trying to reach is not found"),
            @ApiResponse(responseCode = "500", description = "Application failed to process the request")}
    )
    public void deleteById(@PathVariable Long id) {
        commentService.deleteById(id);
    }

    @SneakyThrows
    private CommentDtoRequest applyPatch(JsonPatch patch, CommentDtoResponse dto) {
        JsonNode patched = patch.apply(objectMapper.convertValue(dto, JsonNode.class));
        CommentDtoResponse commentDtoResponse = objectMapper.treeToValue(patched, CommentDtoResponse.class);
        return buildRequest(commentDtoResponse);
    }

    private CommentDtoRequest buildRequest(CommentDtoResponse dto) {
        return CommentDtoRequest.builder()
                .content(dto.content())
                .newsId(dto.news().id())
                .build();
    }
}