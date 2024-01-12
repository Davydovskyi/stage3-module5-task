package com.mjc.school.controller.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import com.mjc.school.controller.NewsController;
import com.mjc.school.service.AuthorService;
import com.mjc.school.service.CommentService;
import com.mjc.school.service.NewsService;
import com.mjc.school.service.TagService;
import com.mjc.school.service.dto.author.AuthorDtoResponse;
import com.mjc.school.service.dto.comment.CommentDtoResponse;
import com.mjc.school.service.dto.news.NewsDtoRequest;
import com.mjc.school.service.dto.news.NewsDtoResponse;
import com.mjc.school.service.dto.news.NewsQueryParams;
import com.mjc.school.service.dto.tag.TagDtoResponse;
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
@RequestMapping("/api/v1/news")
@Api("Operations for creating, updating, retrieving and deleting news in the application")
public class NewsRestController implements NewsController {
    private final NewsService newsService;
    private final AuthorService authorService;
    private final TagService tagService;
    private final CommentService commentService;
    private final ObjectMapper objectMapper;

    @Override
    @GetMapping
    @ApiOperation(value = "View all news", response = List.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved all news"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 500, message = "Application failed to process the request")}
    )
    public List<NewsDtoResponse> readAll(Pageable pageable) {
        return newsService.readAll(pageable);
    }

    @GetMapping("/{id}")
    @Override
    @ApiOperation(value = "Retrieve specific news with the supplied id", response = NewsDtoResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved the news with the supplied id"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
            @ApiResponse(code = 500, message = "Application failed to process the request")}
    )
    public NewsDtoResponse readById(@PathVariable Long id) {
        return newsService.readById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @Override
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    @ApiOperation(value = "Create a news", response = NewsDtoResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Successfully created a news"),
            @ApiResponse(code = 400, message = "The request parameters are invalid"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 500, message = "Application failed to process the request")}
    )
    public NewsDtoResponse create(@RequestBody @Validated({CreateAction.class, Default.class}) NewsDtoRequest dtoRequest) {
        return newsService.create(dtoRequest);
    }

    @Override
    @PutMapping("/{id}")
    @ApiOperation(value = "Update news information", response = NewsDtoResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully updated news information"),
            @ApiResponse(code = 400, message = "The request parameters are invalid"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
            @ApiResponse(code = 500, message = "Application failed to process the request")}
    )
    public NewsDtoResponse update(@PathVariable Long id,
                                  @RequestBody @Validated NewsDtoRequest dtoRequest) {
        return newsService.update(id, dtoRequest)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @Override
    @PatchMapping(path = "/{id}", consumes = "application/json-patch+json")
    @ApiOperation(value = "Update news information", response = NewsDtoResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully updated news information"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
            @ApiResponse(code = 500, message = "Application failed to process the request")}
    )
    public NewsDtoResponse patch(@PathVariable Long id, @RequestBody JsonPatch patch) {
        return newsService.readById(id)
                .map(news -> {
                    NewsDtoRequest newsDtoRequest = applyPatch(patch, news);
                    return newsService.patch(id, newsDtoRequest);
                })
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @Override
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ApiOperation(value = "Deletes specific news with the supplied id")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Successfully deletes the specific news"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
            @ApiResponse(code = 500, message = "Application failed to process the request")}
    )
    public void deleteById(@PathVariable Long id) {
        newsService.deleteById(id);
    }

    @Override
    @GetMapping("/filter")
    @ApiOperation(value = "Retrieve news with the supplied filter", response = NewsDtoResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved the news with the supplied filter"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 500, message = "Application failed to process the request")
    })
    public List<NewsDtoResponse> readAllByFilter(NewsQueryParams filter, Pageable pageable) {
        return newsService.readAllByFilter(filter, pageable);
    }

    @Override
    @GetMapping("/{id}/author")
    @ApiOperation(value = "Retrieve author with the supplied news id", response = AuthorDtoResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved the author with the supplied news id"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 500, message = "Application failed to process the request")
    })
    public AuthorDtoResponse readAuthorByNewsId(@PathVariable Long id) {
        return authorService.readByNewsId(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @Override
    @GetMapping("/{id}/tags")
    @ApiOperation(value = "Retrieve tags with the supplied news id", response = List.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved the tags with the supplied news id"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 500, message = "Application failed to process the request")
    })
    public List<TagDtoResponse> readAllTagsByNewsId(@PathVariable Long id) {
        return tagService.readAllByNewsId(id);
    }

    @Override
    @GetMapping("/{id}/comments")
    @ApiOperation(value = "Retrieve comments with the supplied news id", response = List.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved the comments with the supplied news id"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 500, message = "Application failed to process the request")
    })
    public List<CommentDtoResponse> readAllCommentsByNewsId(@PathVariable Long id) {
        return commentService.readAllByNewsId(id);
    }

    @SneakyThrows
    private NewsDtoRequest applyPatch(JsonPatch patch, NewsDtoResponse dto) {
        JsonNode patched = patch.apply(objectMapper.convertValue(dto, JsonNode.class));
        NewsDtoResponse newsDtoResponse = objectMapper.treeToValue(patched, NewsDtoResponse.class);
        return buildRequest(newsDtoResponse);
    }

    private NewsDtoRequest buildRequest(NewsDtoResponse dto) {
        return NewsDtoRequest.builder()
                .title(dto.title())
                .content(dto.content())
                .authorId(dto.author().id())
                .build();
    }
}