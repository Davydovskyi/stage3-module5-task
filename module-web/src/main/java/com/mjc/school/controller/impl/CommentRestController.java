package com.mjc.school.controller.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import com.mjc.school.controller.CommentController;
import com.mjc.school.service.CommentService;
import com.mjc.school.service.dto.comment.CommentDtoRequest;
import com.mjc.school.service.dto.comment.CommentDtoResponse;
import com.mjc.school.service.validator.group.CreateAction;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
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
@Api("Operations for creating, updating, retrieving and deleting comment in the application")
public class CommentRestController implements CommentController {

    private final CommentService commentService;
    private final ObjectMapper objectMapper;

    @GetMapping
    @Override
    @ApiOperation(value = "View all comments", response = List.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved all comments"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 500, message = "Application failed to process the request")}
    )
    public List<CommentDtoResponse> readAll(Pageable pageable) {
        return commentService.readAll(pageable);
    }

    @GetMapping("/{id}")
    @Override
    @ApiOperation(value = "Retrieve specific comment with the supplied id", response = CommentDtoResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved the comment with the supplied id"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
            @ApiResponse(code = 500, message = "Application failed to process the request")}
    )
    public CommentDtoResponse readById(@PathVariable Long id) {
        return commentService.readById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Override
    @ApiOperation(value = "Create a comment", response = CommentDtoResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Successfully created a comment"),
            @ApiResponse(code = 400, message = "The request parameters are invalid"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 500, message = "Application failed to process the request")}
    )
    public CommentDtoResponse create(@RequestBody @Validated({CreateAction.class, Default.class}) CommentDtoRequest createRequest) {
        return commentService.create(createRequest);
    }

    @Override
    @PutMapping("/{id}")
    @ApiOperation(value = "Update comment information", response = CommentDtoResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully updated comment information"),
            @ApiResponse(code = 400, message = "The request parameters are invalid"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
            @ApiResponse(code = 500, message = "Application failed to process the request")}
    )
    public CommentDtoResponse update(@PathVariable Long id,
                                     @RequestBody @Validated CommentDtoRequest updateRequest) {
        return commentService.update(id, updateRequest)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @PatchMapping(path = "/{id}", consumes = "application/json-patch+json")
    @Override
    @ApiOperation(value = "Update comment specific information", response = CommentDtoResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully updated comment information"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
            @ApiResponse(code = 500, message = "Application failed to process the request")}
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
    @ApiOperation(value = "Deletes specific comment with the supplied id")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Successfully deletes the specific comment"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 500, message = "Application failed to process the request")}
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